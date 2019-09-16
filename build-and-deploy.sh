#!/usr/bin/env bash
echo "compiling application..."
mvn clean install -DskipTests
echo "deleting existing deployments and services..."
kubectl delete deployment drools-demo-deployment
kubectl delete service drools-demo
echo "building image..."
docker build -t "drools-demo:0.0.2" .
echo "creating image tag and push to docker hub...."
docker tag drools-demo:0.0.2 dipu035/drools-demo:0.0.2
docker push dipu035/drools-demo:0.0.2
echo "deploying application kubernetes manifest..."
kubectl apply -f kubernetes-manifest/drools-demo.yaml