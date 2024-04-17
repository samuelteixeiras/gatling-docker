#!/bin/bash

set -e
set -x

kubectl create namespace api-load-test || true
kubectl create secret generic aws-credentials-api-load-test --from-file=credentials="$HOME/.aws/credentials"
kubectl apply -f k8s-job.yaml -n api-load-test
