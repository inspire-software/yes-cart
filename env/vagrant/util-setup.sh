#!/bin/sh
#
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

if which git >/dev/null; then
        echo "skip utils installation"
else
        echo "utils installation"
        yum update -y
        apt-get install -y git
        apt-get install -y wget
        apt-get install -y curl
        apt-get install -y net-tools
        apt-get install -y mc
        apt-get install -y unzip
fi
