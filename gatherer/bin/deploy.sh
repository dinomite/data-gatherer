#!/usr/bin/env bash
set -eoux pipefail

VERSION=$1

docker build -f Dockerfile.gatherer --tag data-gatherer:"$VERSION" .
docker stop data-gatherer
docker rm data-gatherer
docker run --name data-gatherer --log-opt max-size=15m --restart=always -d data-gatherer:"$VERSION"
git tag -am "$VERSION" "$VERSION"
