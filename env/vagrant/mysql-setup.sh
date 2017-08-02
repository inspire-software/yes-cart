#!/bin/sh
#
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

#if which mysql >/dev/null; then
        echo "skip mysql installation"
#else
        echo "mysql installation"


        apt-get install -y mysql-server


#fi