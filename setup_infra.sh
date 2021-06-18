#!/bin/bash

cd /mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/terraform

terraform apply

./data-transfer.sh

cd /mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/ansible

ansible-playbook global.yml

cd /mnt/c/users/ernes/desktop/ernie_work/vgr-backend/iac/terraform

./finish.sh