variable "region" {
  description = "The AWS region to create resources in."
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name to use in resource names"
  default     = "django-aws-tf"
}

variable "access_key" {
  description = "Enter access key"
  sensitive = false
}

variable "secret_key" {
  description = "Enter secret key"
  sensitive = true
}

variable "ec2_instance_type" {
  description = "Enter instance type:"
  default = "t2.micro"
}

variable "ami_username" {
  default = "ubuntu"
}