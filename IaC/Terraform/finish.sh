#!/bin/bash

jenkins_pass_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/jp
jenkins_pass=$(<$jenkins_pass_path)
jenkins_crumb_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/jcr
new_dns=$(</mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/new_dns)
jenkins_token_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/jt

sudo rm $jenkins_crumb_path
curl -s --cookie-jar /tmp/cookies -u admin:$jenkins_pass $new_dns/crumbIssuer/api/json | jq -r '.crumb' >> $jenkins_crumb_path
jenkins_crumb=$(<$jenkins_crumb_path)

sudo rm $jenkins_token_path
curl -X POST -H "Jenkins-Crumb:$jenkins_crumb" --cookie /tmp/cookies $new_dns/me/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken?newTokenName=jt -u admin:$jenkins_pass | jq -r '.data.tokenValue' >> $jenkins_token_path
jenkins_token=$(<$jenkins_token_path)

github_token_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/ght
github_token=$(<$github_token_path)

public_key_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/pk
sudo rm $public_key_path
curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/public-key | jq -r '.key' >> $public_key_path
public_key=$(<$public_key_path)

public_id_path=/mnt/c/users/ernes/desktop/ernie_work/vgr-backend/forbidden/pki
sudo rm $public_id_path
curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/public-key | jq -r '.key_id' >> $public_id_path
public_id=$(<$public_id_path)

encrypted_value=$(python3 encrypt_sodium.py $public_key $jenkins_token)
curl -X PUT -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $github_token" https://api.github.com/repos/ernestk86/vgr-backend/actions/secrets/JENKINS_TOKEN -d '{"encrypted_value":"'$encrypted_value'", "key_id":"'$public_id'"}'