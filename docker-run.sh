#!/bin/sh
tag='transit:dev'
port=8081
env='.env'
while getopts t:e:p: flag
do
    case "${flag}" in
        t) tag=${OPTARG};;
        e) env=${OPTARG};;
        p) port=${OPTARG};;
    esac
done
echo "tag: $tag";
echo "port: $port";
echo "env path: $env";
ls -la
docker run --env-file $env -p $port:8080 -t $tag
