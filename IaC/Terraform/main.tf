terraform {
  backend "remote" {
    organization = "vgr-backend"
    workspaces {
      name = "Production"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region  = "us-east-2"
}

resource "aws_db_instance" "database" {
  allocated_storage = 10
  max_allocated_storage = 20
  engine = "PostgreSQL"
  engine_version = "12.5"
  instance_class = "db.t2.micro"
  name = "vgr-backend"
  username = "admin"
  password = var.db_password
  skip_final_snapshot = true
}

resource "aws_instance" "pipelineServer" {
  ami           = "ami-077e31c4939f6a2f3"
  instance_type = "t2.micro"
  key_name = var.key_pair_name
  security_groups = "Ernest_Home"

  ebs_block_device = {
    device_name = var.pipeline_server_name
    volume_size = 30
    volume_type = "gp2"
  }

  tags = {
    Name = var.pipeline_server_name
  }
}