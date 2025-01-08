variable "mc_region" {
  default = "us-east-1"
}

variable "mc_admin_ip" {
  description = "Enter IP to whitelist.\nThe IP address entered here will be the only one to be able to SSH into the machine."
}

variable "mojang_server_url" {
  default = "https://piston-data.mojang.com/v1/objects/4707d00eb834b446575d89a61a11b5d548d8c001/server.jar"
  description = "Enter Minecraft Server URL [minecraft_server.1.21.4.jar]"
}

variable "ec2_instance_type" {
  description = "Enter arm64 instance type:"
}