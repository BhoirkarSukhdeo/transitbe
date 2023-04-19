#!/bin/sh
tag='transit:dev'
path='build/libs/*jar'
while getopts t:p: flag
do
    case "${flag}" in
        t) tag=${OPTARG};;
        p) path=${OPTARG};;
    esac
done
echo "tag: $tag";
echo "path: $path";

gradle clean build
docker build --build-arg $path -t $tag .
