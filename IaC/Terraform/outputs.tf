output "instance_public_ip" {
  description = "ID of the EC2 instance"
  value       = aws_instance.jenkinsServer.public_ip
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