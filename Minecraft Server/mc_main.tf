terraform {
  required_providers {
    aws = {
        source = "hashicorp/aws"
        version = "~> 3.27"
    }
    tls = {
        source = "hashicorp/tls"
        version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = var.mc_region
}

resource "aws_security_group" "mc_sec_grp" {
  ingress {
    description = "Receive SSH from home."
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${var.mc_admin_ip}/32"]
  }
  ingress {
    description = "Receive Minecraft from everywhere."
    from_port   = 25565
    to_port     = 25565
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    description = "Send everywhere."
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    name = "Mincraft"
  }
}

# Generate a PPK pair
resource "tls_private_key" "mc_priv_key" {
  algorithm = "RSA"
  rsa_bits = 4096
}

resource "aws_key_pair" "mc_key_pair" {
  key_name = "MC-Keys"
  public_key = tls_private_key.mc_priv_key.public_key_openssh
}

# Deploy and configure the Minecraft Server
resource "aws_instance" "minecraft" {
  ami = data.aws_ami.amazon_linux.id
  instance_type = var.ec2_instance_type
  vpc_security_group_ids = [aws_security_group.mc_sec_grp.id]
  associate_public_ip_address = true
  key_name = aws_key_pair.mc_key_pair.key_name

  connection {
    type = "ssh"
    user = "ec2-user"
    private_key = tls_private_key.mc_priv_key.private_key_pem
    host = self.public_ip
    timeout = "1m"
  }

  ## Leave this commented out if you want to generate a new world
  # provisioner "file" {
  #   when = create
  #   source = "${path.module}/world.zip"
  #   destination = "/tmp/world.zip"
  # }

  # Keep this part updated as needed, most specifically the java dowload.
  user_data = <<-EOF
    #!/bin/bash

    # Download my favorites
    sudo yum install -y tree wget unzip zip

    # Download Java
    wget https://corretto.aws/downloads/latest/amazon-corretto-21-aarch64-linux-jdk.rpm
    sudo yum localinstall amazon-corretto-21-aarch64-linux-jdk.rpm -y
    rm amazon-corretto-21-aarch64-linux-jdk.rpm

    # Install MC Java server in a directory we create
    adduser minecraft
    mkdir /opt/minecraft
    mkdir /opt/minecraft/server/
    cd /tmp
    sudo unzip -d /opt/minecraft/server/ world.zip
    cd /opt/minecraft/server

    # Download server jar file from Minecraft official website
    wget ${var.mojang_server_url}

    # Generate Minecraft server files and create script
    chown -R minecraft:minecraft /opt/minecraft/
    java -Xmx1300M -Xms1300M -jar server.jar nogui
    sleep 40
    sed -i 's/false/true/p' eula.txt
    touch start
    printf '#!/bin/bash\njava -Xmx1300M -Xms1300M -jar server.jar nogui\n' >> start
    chmod +x start
    sleep 1
    touch stop
    printf '#!/bin/bash\nkill -9 $(ps -ef | pgrep -f "java")' >> stop
    chmod +x stop
    sleep 1
    sudo sed -i 's/motd=A Minecraft Server/motd=${var.server_name}/p' server.properties
    # sudo rm -rf "world/"
    # cd /tmp
    # sudo unzip -d /opt/minecraft/server/ world.zip

    # Create SystemD Script to run Minecraft server jar on reboot
    cd /etc/systemd/system/
    touch minecraft.service
    printf '[Unit]\nDescription=Minecraft Server on start up\nWants=network-online.target\n[Service]\nUser=minecraft\nWorkingDirectory=/opt/minecraft/server\nExecStart=/opt/minecraft/server/start\nStandardInput=null\n[Install]\nWantedBy=multi-user.target' >> minecraft.service
    sudo systemctl daemon-reload
    sudo systemctl enable minecraft.service
    sudo systemctl start minecraft.service
    # cd /opt/minecraft/server
    # sudo ./start
    EOF
  tags = {
    Name = "Minecraft"
  }
}

resource "null_resource" "on_destroy" {
  triggers = {
    aws_connect_ip = "${replace(aws_instance.minecraft.public_ip, ".", "-")}",
    rsc_ip = "${aws_instance.minecraft.public_ip}"
    key_location = "${aws_instance.minecraft.key_name}.pem"
  }

  connection {
    type = "ssh"
    user = "ec2-user"
    private_key = file("${self.triggers.key_location}")
    host = "${self.triggers.rsc_ip}"
    timeout = "1m"
  }

  provisioner "remote-exec" {
    when = destroy
    inline = [ "cd /opt/minecraft/server/ && sudo zip -r /tmp/world.zip ./world" ]
  }

  provisioner "remote-exec" {
    when = destroy
    inline = [ "sudo chown -R minecraft:minecraft /tmp/world.zip" ]
  }
  
  provisioner "local-exec" {
    when = destroy
    command = "scp -i ${self.triggers.key_location} ec2-user@ec2-${self.triggers.aws_connect_ip}.compute-1.amazonaws.com:/tmp/world.zip ./"
  }
}
