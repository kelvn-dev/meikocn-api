########################################################################################
# NOTE: token doesn't contain permission because it's only for authentication purpose
# Prerequisite:
# - jq
#   sudo apt install jq (linux)
#   brew install jq (mac)
# - client_id
#   export client_id=${client_id}
# - client_secret
#   export client_secret=${client_secret}
########################################################################################
domain=meikocn.jp.auth0.com
realm='Username-Password-Authentication'
audience='https://meikocn.jp.auth0.com/api/v2/'

if [ "$#" -ne 2 ]; then
  echo "Please enter username and password"
  exit 1
fi

curl -X 'POST' \
--url https://${domain}/oauth/token \
--header 'content-type: application/x-www-form-urlencoded' \
--data grant_type=http://auth0.com/oauth/grant-type/password-realm \
--data username=$1 \
--data password=$2 \
--data client_id=${client_id} \
--data client_secret=${client_secret} \
--data realm=${realm}
--data audience=${audience} | jq
