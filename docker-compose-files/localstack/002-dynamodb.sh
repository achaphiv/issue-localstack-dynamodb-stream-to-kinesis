# #!/bin/bash
set -x
awslocal dynamodb create-table --cli-input-json file:///docker-entrypoint-initaws.d/files/dynamodb-create-table-request.json || true

stream_arn=$(awslocal kinesis describe-stream --stream-name localstack --output text --query 'StreamDescription.StreamARN')
awslocal dynamodb enable-kinesis-streaming-destination --table-name bug --stream-arn $stream_arn || true
set +x
