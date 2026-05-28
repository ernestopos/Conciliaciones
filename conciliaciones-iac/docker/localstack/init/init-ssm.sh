#!/bin/sh
set -e

echo "Inicializando Parameter Store en LocalStack..."

awslocal ssm put-parameter \
  --name "/dev/test/param1" \
  --value "valor1" \
  --type "String" \
  --overwrite

echo "Parameter Store listo."
