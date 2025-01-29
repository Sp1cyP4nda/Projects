output "private_key" {
  value = tls_private_key.dj_priv_key.private_key_pem
  sensitive = true
}

output "instance_ip_addr" {
    value = aws_instance.django_website.public_ip
}

resource "local_sensitive_file" "download_DJ-Keys" {
  content = tls_private_key.dj_priv_key.private_key_openssh
  filename = "${path.module}/${aws_key_pair.dj_key_pair.key_name}.pem"
}

resource "local_file" "save_ssh" {
  content = "ssh -i \"${path.cwd}/DJ-Keys.pem\" ${var.ami_username}@ec2-${replace(aws_instance.django_website.public_ip, ".", "-")}.compute-1.amazonaws.com"
  filename = "${path.module}/DJ-ssh.ps1"
}

output "username_root_device_name" {
  value = data.aws_ami.amazon_linux.image_id
}