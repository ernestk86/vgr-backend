#!/bin/bash

host_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/ansible/hosts
dns_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/ansible/new_dns

new_host=$(terraform output -raw instance_public_ip)
echo $new_host >> $host_path
sed -i 'N;$!P;D' $host_path
new_dns=$(terraform output -raw instance_public_dns)
sudo rm $dns_path
echo -n "http://" >> $dns_path
echo -n $new_dns >> $dns_path
echo -n ":8080" >> $dns_path