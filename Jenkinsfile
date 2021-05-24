pipeline {
    agent any
    tools {
        maven 'Maven'    
    }
    stages {
        // stage('Clean') {
        //     steps {
        //         sh 'mvn clean'
        //     }
        // }

        // stage('Build Jar File') {
        //     steps {
        //         sh 'mvn package'
        //     }
        // }
        
        // stage('Quality Gates') {
        //     steps {
        //         //withSonarQubeEnv(credentialsId: 'sonar-blockbuster-token', installationName: 'sonarcloud') {
        //             //sh 'mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar'
        //         //}
        //         sh 'mvn sonar:sonar -Dsonar.login=a2363573af11a80d894023cbcc0a6c76916017dc'
        //     }
        // }
        
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
