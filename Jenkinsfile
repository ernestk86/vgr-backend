pipeline {
    agent any

    stages {
        
        stage('Scale down Canary') {
            steps {
                // Send PUT request to cluster to update deployment
                sh '''curl --request PUT --url https://$CLUSTER_ENDPOINT/apis/apps/v1/namespaces/default/deployments/vgr-backend-canary \\
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
                // Grab latest canary image, retag it to production, log into DockerHub, push tagged image to production
                sh 'docker pull ernestk86/vgr-backend-canary:latest'
                sh 'docker image tag ernestk86/vgr-backend-canary:latest ernestk86/vgr-backend:production'
                sh "docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD"
                sh 'docker push ernestk86/vgr-backend:production'
            }
        }

        stage('Rollout new image to Production') {
            steps {
                // Send PUT request to scale down replicas in production deployment to 0
                sh '''curl --request PUT --url https://$CLUSTER_ENDPOINT/apis/apps/v1/namespaces/default/deployments/vgr-backend \\
                    --header \'content-type: application/json\' \\
                    --header \"Authorization: Bearer $K8_ACCESS_TOKEN\" \\
                    --insecure \\
                    --data \'{
                                \"apiVersion\": \"apps/v1\",
                                \"kind\": \"Deployment\",
                                \"metadata\": {
                                    \"name\": \"vgr-backend\",
                                    \"labels\": {
                                        \"app\": \"vgr-backend\"
                                    }
                                },
                                \"spec\": {
                                    \"replicas\": 0,
                                    \"revisionHistoryLimit\": 3,
                                    \"selector\": {
                                        \"matchLabels\": {
                                            \"app\": \"vgr-backend\",
                                            \"track\": \"stable\"
                                        }
                                    },
                                    \"template\": {
                                        \"metadata\": {
                                            \"labels\": {
                                            \"app\": \"vgr-backend\",
                                            \"track\": \"stable\"
                                            }
                                        },
                                        \"spec\": {
                                            \"volumes\": null,
                                            \"containers\": [
                                            {
                                                \"name\": \"vgr-backend\",
                                                \"image\": \"ernestk86/vgr-backend:production\",
                                                \"ports\": [
                                                    {
                                                        \"containerPort\": 8880
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
                // Send PUT request to scale up replicas in production deployment back to 3
                sh '''curl --request PUT --url https://$CLUSTER_ENDPOINT/apis/apps/v1/namespaces/default/deployments/vgr-backend \\
                    --header \'content-type: application/json\' \\
                    --header \"Authorization: Bearer $K8_ACCESS_TOKEN\" \\
                    --insecure \\
                    --data \'{
                                \"apiVersion\": \"apps/v1\",
                                \"kind\": \"Deployment\",
                                \"metadata\": {
                                    \"name\": \"vgr-backend\",
                                    \"labels\": {
                                        \"app\": \"vgr-backend\"
                                    }
                                },
                                \"spec\": {
                                    \"replicas\": 3,
                                    \"revisionHistoryLimit\": 3,
                                    \"selector\": {
                                        \"matchLabels\": {
                                            \"app\": \"vgr-backend\",
                                            \"track\": \"stable\"
                                        }
                                    },
                                    \"template\": {
                                        \"metadata\": {
                                            \"labels\": {
                                            \"app\": \"vgr-backend\",
                                            \"track\": \"stable\"
                                            }
                                        },
                                        \"spec\": {
                                            \"volumes\": null,
                                            \"containers\": [
                                            {
                                                \"name\": \"vgr-backend\",
                                                \"image\": \"ernestk86/vgr-backend:production\",
                                                \"ports\": [
                                                    {
                                                        \"containerPort\": 8880
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
        
        stage('Health Check') {
            steps {
                // Allow cluster time to update, grab results of POST request and verify results
                sh 'sleep 10'
                sh 'result=$(curl --request POST --url http://$NGINX_ENDPOINT/vgr-backend/login  --header \'content-type: application/json\' --data \'{\"username\": \"admin\", \"password\": \"password\"}\')'
                sh 'grep admin | echo $result'
                sh 'exit $?'
            }
        }
    }


    post {
        failure {
            emailext to: 'ekim86@gmail.com', 
                subject: 'Final Pipeline Stages Failed',
                body: 'Either scaling down the canary, promoting the production, or the healthcheck failed.'
        }
    }

}
