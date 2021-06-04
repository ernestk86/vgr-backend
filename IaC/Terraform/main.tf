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
  engine = "postgres"
  engine_version = 12.5
  instance_class = "db.t2.micro"
  name = "vgrbackend"
  username = var.db_username
  password = var.db_password
  skip_final_snapshot = true
  security_group_names = tolist(["Ernest_Home"])
}

resource "aws_instance" "pipelineServer" {
  ami           = "ami-077e31c4939f6a2f3"
  instance_type = "t2.micro"
  key_name = var.key_pair_name
  security_groups = tolist(["Ernest_Home"])

  ebs_block_device {
    device_name = "/dev/xvda"
    volume_size = 30

    tags = {
      Name = var.pipeline_server_name
    }
  }

  tags = {
    Name = var.pipeline_server_name
  }
}