#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push cpollet/covid19-stats-loader:latest
curl "https://webhook.cpollet.io/hooks/net.cpollet.covid19:stats-loader:update?token=$WEBHOOK_TOKEN"
