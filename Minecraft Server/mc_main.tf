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

  # connection {
  #   type = "ssh"
  #   user = "ec2-user"
  #   private_key = tls_private_key.mc_priv_key.private_key_pem
  #   host = self.public_ip
  #   timeout = "1m"
  # }

  # provisioner "file" {
  #   source = "${path.module}/world.zip"
  #   destination = "/tmp/world.zip"
  # }

  # Keep this part updated as needed, most specifically the java dowload.
  user_data = <<-EOF
    #!/bin/bash

    # Download my favorites
    sudo yum install -y tree wget

    # Download Java
    wget https://corretto.aws/downloads/latest/amazon-corretto-21-aarch64-linux-jdk.rpm
    sudo yum localinstall amazon-corretto-21-aarch64-linux-jdk.rpm -y

    # Install MC Java server in a directory we create
    adduser minecraft
    mkdir /opt/minecraft
    mkdir /opt/minecraft/server/
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
    sudo sed -i 's/motd=A Minecraft Server/motd=On Your Upkeep/p' server.properties
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
