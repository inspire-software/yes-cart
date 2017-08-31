#!/bin/sh
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

while [ ! $(docker ps -q | wc -l) = 0 ]; do
    for i in $(docker ps -q); do
        docker stop $i
        docker rm -v $i
    done
done

for i in $(docker ps -q -a); do
    docker rm -v $i;
done

for m in $(mount | grep /var/lib/kubelet | awk '{print $3}'); do
    sudo umount $m
done
sudo rm -rf /var/lib/kubelet/*
