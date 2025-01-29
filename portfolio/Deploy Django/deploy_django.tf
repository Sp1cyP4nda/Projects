# Always start with the shebang
provider "aws" {
  region = var.region
  access_key = var.access_key
  secret_key = var.secret_key
}

resource "aws_security_group" "dj_sec_grp" {
  ingress {
    description = "Receive SSH from anywhere."
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "TCP port 80 for HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "TCP port 443 for HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "TCP port 8000 for python manage.py runserver"
    from_port = 8000
    to_port = 8000
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Outbound traffic to anywhere"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Generate a PPK pair
resource "tls_private_key" "dj_priv_key" {
  algorithm = "RSA"
  rsa_bits = 4096
}

resource "aws_key_pair" "dj_key_pair" {
  key_name = "DJ-Keys"
  public_key = tls_private_key.dj_priv_key.public_key_openssh
}

resource "aws_instance" "django_website" {
  ami = data.aws_ami.amazon_linux.id
  instance_type = var.ec2_instance_type
  vpc_security_group_ids = [ aws_security_group.dj_sec_grp.id ]
  associate_public_ip_address = true
  key_name = aws_key_pair.dj_key_pair.key_name

  provisioner "local-exec" {
    when = create
    command = "..\\path\\to\\Scripts\\activate;python -m pip freeze > requirements.txt"
    interpreter = [ "Powershell", "-Command" ]
  }

  provisioner "local-exec" {
    when = create
    command = "Start-Sleep 30;python ${path.module}/build_django.py"
    interpreter = [ "Powershell", "-Command" ]
  }

  connection {
    type = "ssh"
    user = var.ami_username
    private_key = tls_private_key.dj_priv_key.private_key_pem
    host = self.public_ip
    timeout = "1m"
  }

  provisioner "file" {
    when = create
    source = "${path.module}/portfolio.zip"
    destination = "/tmp/portfolio.zip"
  }

  user_data = <<-EOF
    #!/bin/bash

    ### Toolkit installation
    sudo apt update -y && sudo apt upgrade -y
    sudo apt install -y wget make tree unzip zip nano apache2 libapache2-mod-wsgi-py3 python3-pip python3-venv

    ### Put relevant files in /django/server
    adduser django
    cd /opt
    sudo python3 -m venv django
    sudo mkdir /opt/django/server
    cd /tmp
    sudo sleep 45
    sudo unzip -d /opt/django/server portfolio.zip
    sudo chown -R django:django /opt/django

    ### Configure and launch the website
    source /opt/django/bin/activate && pip3 install -r /opt/django/server/requirements.txt
    sudo sed -i 's/include-system-site-packages = false/include-system-site-packages = true/' /opt/django/pyvenv.cfg
    cd /opt/django/server
    sudo sed -i "s/filepath = '.\/productionfiles\/cbpeterson_infosec_resume.pdf'/filepath = '\/opt\/django\/server\/productionfiles\/cbpeterson_infosec_resume.pdf'" projects/views.py
    sudo cp -f 000-default.conf /etc/apache2/sites-available/000-default.conf
    sudo chmod 664 db.sqlite3
    sudo chown :www-data db.sqlite3
    sudo chown :www-data .
    sudo service apache2 restart
    EOF
  tags = {
    Name = "Django"
  }
}