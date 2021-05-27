pipeline {
    agent any

    stages {
        
        stage('Scale down Canary') {
            steps {
                sh '''curl --request PUT --url https://104.196.197.252/apis/apps/v1/namespaces/default/deployments/vgr-backend-canary \\
                    --header \'content-type: application/json\' \\
                    --header \"Authorization: Bearer $K8_ACCESS_TOKEN\" \\
                    --insecure \\
                    --data \'{
                                \"apiVersion\": \"apps/v1\",
                                \"kind\": \"Deployment\",
                                \"metadata\": {
                                    \"name\": \"vgr-backend-canary\",
                                    \"labels\": {
                                        \"app\": \"vgr-backend-canary\"
                                    }
                                },
                                \"spec\": {
                                    \"replicas\": 0,
                                    \"revisionHistoryLimit\": 3,
                                    \"selector\": {
                                        \"matchLabels\": {
                                            \"app\": \"vgr-backend-canary\",
                                            \"track\": \"stable\"
                                        }
                                    },
                                    \"template\": {
                                        \"metadata\": {
                                            \"labels\": {
                                            \"app\": \"vgr-backend-canary\",
                                            \"track\": \"stable\"
                                            }
                                        },
                                        \"spec\": {
                                            \"volumes\": null,
                                            \"containers\": [
                                            {
                                                \"name\": \"vgr-backend-canary\",
                                                \"image\": \"ernestk86/vgr-backend-canary:latest\",
                                                \"ports\": [
                                                    {
                                                        \"containerPort\": 8885
                                                    }
                                                ],
                                                \"imagePullPolicy\": \"Always\",
                                                \"env\": [
                                                    {
                                                        \"name\": \"DB_URL\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"DB_URL\"
                                                        }
                                                        }
                                                    },
                                                    {
                                                        \"name\": \"DB_USERNAME\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"DB_USERNAME\"
                                                        }
                                                        }
                                                    },
                                                    {
                                                        \"name\": \"DB_PASSWORD\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"DB_PASSWORD\"
                                                        }
                                                        }
                                                    },
                                                    {
                                                        \"name\": \"AWS_SES_HOST\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"AWS_SES_HOST\"
                                                        }
                                                        }
                                                    },
                                                    {
                                                        \"name\": \"AWS_SES_USERNAME\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"AWS_SES_USERNAME\"
                                                        }
                                                        }
                                                    },
                                                    {
                                                        \"name\": \"AWS_SES_PASSWORD\",
                                                        \"valueFrom\": {
                                                        \"secretKeyRef\": {
                                                            \"name\": \"vgr-backend-creds\",
                                                            \"key\": \"AWS_SES_PASSWORD\"
                                                        }
                                                        }
                                                    }
                                                ]
                                            }
                                            ]
                                        }
                                    }
                                }
                            }\''''

            }
        }

        stage('Promote image to Production') {
            steps {
                sh 'docker pull ernestk86/vgr-backend-canary:latest'
                sh 'docker image tag ernestk86/vgr-backend-canary:latest ernestk86/vgr-backend:production'
                sh "docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD"
                sh 'docker push ernestk86/vgr-backend:production'
            }
        }
        
        stage('Health Check') {
            steps {
                sh 'result=$(curl --request POST --url http://34.74.246.76/vgr-backend/login  --header \'content-type: application/json\' --data \'{\"username\": \"admin\", \"password\": \"password\"}\')'
                sh 'grep admin | echo $result'
                sh 'exit $?'
            }
        }
    }
}
