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
        
        stage('Quality Gates') {
            steps {
                sh 'echo sonarcheck'
            }
        }
        
        stage('Build Jar File') {
            steps {
                sh 'echo mvn-build'
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
