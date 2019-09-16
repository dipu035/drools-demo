# drools-demo

## Description ##
This demo project has 2 endpoints, one is to upload/process dmn files to a repository and another one is to execute the rules.

## Kubernetes configuration ##
The configuration file of kubernetes can be found in `/kubernetes-manifest` directory.

# Building the software #

## Required Software ##
* Java
* Maven

## Build instructions ##
`mvn clean install`

## Build and deploy in kubernetes clusters ##
`sh build-and-deploy.sh`
