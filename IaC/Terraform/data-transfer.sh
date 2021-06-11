#!/bin/bash

host_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/hosts
dns_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/new_dns
github_webhook_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/github_webhook
github_token_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/ght

new_host=$(terraform output -raw instance_public_ip)
echo $new_host >> $host_path
sed -i 'N;$!P;D' $host_path
new_dns=$(terraform output -raw instance_public_dns)
sudo rm $dns_path
echo -n "http://" >> $dns_path
echo -n $new_dns >> $dns_path
echo -n ":8080" >> $dns_path

sudo rm $github_webhook_path
cat $dns_path >> $github_webhook_path
echo -n "/github-webhook" >> $github_webhook_path
github_webhook=$(<$github_webhook_path)
github_token=$(<$github_token_path)

curl -X POST -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/hooks -d "{\"config\" : {\"url\" : \"$github_webhook\"}}"