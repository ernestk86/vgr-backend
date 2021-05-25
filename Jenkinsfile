pipeline {
    agent any
    tools {
        maven 'Maven'    
    }
    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Github Handoff for Build') {
            steps {
                sh 'curl -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Authorization: Bearer $GITHUB_ACCESS_TOKEN\" https://api.github.com/repos/ernestk86/vgr-backend/actions/workflows/build.yml/dispatches -d \'{\"ref\":\"main\"}\''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'echo docker_image'
            }
        }
        
        stage('Canary Deploy') {
            steps {
                sh 'echo canary'
            }
        }

        stage('Load Test') {
            steps {
                sh 'echo loadtest'
            }
        }

        stage('Production Deploy') {
            steps {
                sh 'echo production'
            }
        }
        
        stage('Health Check') {
            steps {
                sh 'echo health'
            }
        }
    }
}
