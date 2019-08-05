#!/bin/bash

VERSION=1.0.4-$(date +%s)

echo "Building Images with version: ${VERSION}"

docker build -t benjaminslabbert/trak_logstash:"${VERSION}" -f ./logstash-conf/Dockerfile ./logstash-conf/
docker build -t benjaminslabbert/trak_prometheus:"${VERSION}" -f ./prometheus-conf/Dockerfile ./prometheus-conf/

echo "#########################"
echo "# Pushing to Docker hub #"
echo "#########################"

echo "$DOCKER_HUB_PASSWORD" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin

docker push benjaminslabbert/trak_logstash:"${VERSION}"
docker push benjaminslabbert/trak_prometheus:"${VERSION}"
