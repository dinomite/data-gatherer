#!/usr/bin/env bash
set -eoux pipefail

VERSION=$1

docker build -f Dockerfile.gatherer --tag data-gatherer:"$VERSION" .
docker stop data-gatherer
docker rm data-gatherer
docker run --name data-gatherer --restart=always -d data-gatherer:"$VERSION"
