output "private_key" {
  value = tls_private_key.mc_priv_key.private_key_pem
  sensitive = true
}

output "instance_ip_addr" {
    value = aws_instance.minecraft.public_ip
}

resource "local_sensitive_file" "download_MC-Keys" {
  content = tls_private_key.mc_priv_key.private_key_openssh
  filename = "${path.module}/${aws_key_pair.mc_key_pair.key_name}.pem"
}