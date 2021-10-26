#!/bin/bash
set -x
awslocal kinesis create-stream --shard-count 1 --stream-name localstack || true
set +x
