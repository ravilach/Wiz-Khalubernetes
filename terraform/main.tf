############################################################
# AWS provider configuration using variables from terraform.tfvars
############################################################
variable "aws_region" {
  description = "AWS region to deploy resources in"
  type        = string
}

variable "key_name" {
  description = "EC2 Key Pair name for SSH access"
  type        = string
}

variable "ami_id" {
  description = "AMI ID for EC2 instance"
  type        = string
}

variable "aws_access_key" {
  description = "AWS Access Key ID"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS Secret Access Key"
  type        = string
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  # For local development, you can also set AWS credentials via environment variables:
  # export AWS_ACCESS_KEY_ID=... AWS_SECRET_ACCESS_KEY=... AWS_DEFAULT_REGION=...
}

############################################################
# EC2 instance for MongoDB
# Idempotency: If the instance already exists, Terraform will skip creation.
# You can safely run 'terraform apply' multiple times.
############################################################
resource "aws_instance" "mongo_ec2" {
  ami           = var.ami_id
  instance_type = "t2.micro"
  key_name      = var.key_name

  tags = {
    Name = "MongoDB-Server"
  }

  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo amazon-linux-extras install epel -y
    sudo yum install -y mongodb-org
    sudo systemctl start mongod
    sudo systemctl enable mongod
    sudo systemctl status mongod
  EOF

  lifecycle {
    prevent_destroy = true
    create_before_destroy = false
    ignore_changes = [tags, user_data]
  }
}

output "ec2_public_ip" {
  value = aws_instance.mongo_ec2.public_ip
}
