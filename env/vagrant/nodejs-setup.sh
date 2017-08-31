#!/bin/sh
#
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

#if which nodejs >/dev/null; then
        echo "skip nodejs installation"
#else
        echo "nodejs installation"

        curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash -

        apt-get install -y nodejs

        npm install -g webpack webpack-dev-server --save
        npm install -g spawn-sync --save

#fi