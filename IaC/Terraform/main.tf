terraform {
  backend "remote" {
    organization = "vgr-backend"
    workspaces {
      name = "vgr-backend"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }

    google = {
      source  = "hashicorp/google"
      version = "3.71.0"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region  = "us-east-2"
}

provider "google" {
  credentials = file("/home/erniek86/terraform/vgr-backend-a54795b62091.json")
  project     = "vgr-backend"
  region      = "us-east4"
  zone        = "us-east4-c"
}

resource "google_container_cluster" "kubernetesCluster" {
  name                     = "vgr-backend-production"
  location                 = "us-east4-c"
  remove_default_node_pool = true
  initial_node_count       = 1
}

resource "google_container_node_pool" "nodes" {
  name       = "vgr-backend-nodepool"
  location   = "us-east4-c"
  cluster    = google_container_cluster.kubernetesCluster.name
  node_count = 3

  node_config {
    preemptible  = true
    machine_type = "g1-small"

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}

data "aws_db_snapshot" "db_snapshot" {
    most_recent = true
    db_instance_identifier = "us-east-2"
}

resource "aws_db_instance" "database" {
  allocated_storage = 20
  engine = "postgres"
  engine_version = 12.5
  instance_class = "db.t2.micro"
  name = "VGLibrary"
  username = var.db_username
  password = var.db_password
  skip_final_snapshot = true
  publicly_accessible = true
  vpc_security_group_ids = tolist(["sg-0349ef4cf405e6d86"])
  snapshot_identifier = "${data.aws_db_snapshot.db_snapshot.id}"
}

resource "aws_instance" "jenkinsServer" {
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