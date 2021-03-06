output "instance_public_ip" {
  description = "Public ip of the EC2 instance"
  value       = aws_instance.jenkinsServer.public_ip
}

output "instance_public_dns" {
  description = "Public dns of the EC2 instance"
  value       = aws_instance.jenkinsServer.public_dns
}

output "database_endpoint" {
  description = "Endpoint for database"
  value = aws_db_instance.database.endpoint
}

output "database_port" {
  description = "Port for database"
  value = aws_db_instance.database.port
}

output "database_username" {
  description = "Username of database"
  value = aws_db_instance.database.username
}

output "cluster_endpoint" {
  description = "Endpoint ip address for master node of cluster"
  value       = google_container_cluster.kubernetesCluster.endpoint
}