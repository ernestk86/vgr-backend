#!/bin/bash

# Update Ansible host file
host_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/ansible/hosts
new_host=$(terraform output -raw instance_public_ip)
echo $new_host >> $host_path
sed -i 'N;$!P;D' $host_path

# Grab and store new instance DNS
dns_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/new_dns
new_dns=$(terraform output -raw instance_public_dns)
sudo rm $dns_path
echo -n "http://" >> $dns_path
echo -n $new_dns >> $dns_path
echo -n ":8080" >> $dns_path

# Update and grab new webhook
github_webhook_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/github_webhook
sudo rm $github_webhook_path
cat $dns_path >> $github_webhook_path
echo -n "/github-webhook" >> $github_webhook_path
github_webhook=$(<$github_webhook_path)

# Grab Github token
github_token_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/ght
github_token=$(<$github_token_path)

# Update Github webhook
curl -X POST -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/hooks -d "{\"config\" : {\"url\" : \"$github_webhook\"}}"

# Update SonarCloud webhook
sonar_token_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/st
sonar_token=$(<$sonar_token_path)
curl -u $sonar_token -d "url=http://$new_dns/generic-webhook-trigger/invoke?token=sonarcheck&name=vgr-backend&organization=ernestk86&project=vgr-backend" -X POST https://sonarcloud.io/api/webhooks/create

# Grab credentials from Google Cloud Platform
gcloud container clusters get-credentials vgr-backend-production --zone us-east4-c

# Grab and update new GKE cluster endpoint
cluster_endpoint_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/ce
sudo rm $cluster_endpoint_path
new_cluster_endpoint=$(terraform output -raw cluster_endpoint)
echo -n $new_cluster_endpoint >> $cluster_endpoint_path

# Update DB_URL in credentials k8 secret
vgr_backend_creds_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/vgr-backend-creds.yml
db_prefix="%-2sDB_URL:%-1s"
db_endpoint="jdbc:postgresql://$(terraform output -raw database_endpoint)/vgrbackend"
printf $db_prefix >> $vgr_backend_creds_path
echo -e -n $db_endpoint | base64 -w 0 >> $vgr_backend_creds_path
sed -i 'N;$!P;D' $vgr_backend_creds_path
printf '\n' >> $vgr_backend_creds_path

# Deploy promtail on cluster
log_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/log
log=$(<$log_path)
promtail_url=https://raw.githubusercontent.com/grafana/loki/master/tools/promtail.sh
curl -fsS $promtail_url | sh -s 58910 $log logs-prod-us-central1.grafana.net default | kubectl apply --namespace=default -f  -

# Grab and update public key id
public_id_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/pki
sudo rm $public_id_path
curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/public-key | jq -r '.key_id' >> $public_id_path
public_id=$(<$public_id_path)

# Grab and update public key
public_key_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/pk
sudo rm $public_key_path
curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/public-key | jq -r '.key' >> $public_key_path
public_key=$(<$public_key_path)

# Encrypt and update GitHub Repo Secret
encrypted_value=$(python3 encrypt_sodium.py $public_key $new_dns)
curl -X PUT -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/NEW_DNS -d '{"encrypted_value":"'$encrypted_value'", "key_id":"'$public_id'"}'