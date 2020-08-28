# Gatherer

# Build
docker build -f Dockerfile.gatherer --tag data-gatherer:1.3 .

# Deploy
docker stop data-gatherer && docker rm data-gatherer
docker run --name data-gatherer --restart=always -d data-gatherer:<VERSION>
