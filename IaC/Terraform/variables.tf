variable "db_username" {
  description = "Value of the db username for login"
  type        = string
  default     = "ernestk86"
}

variable "db_password" {
  description = "Value of the db password for login"
  type        = string
  default     = "stopstealingpasswords"
}

variable "pipeline_server_name" {
  description = "Value for the name of the server hosting the pipeline"
  type        = string
  default     = "vgr-backend-server"
}

variable "key_pair_name" {
  description = "Value of the key name used to access server"
  type        = string
  default     = "access"
}