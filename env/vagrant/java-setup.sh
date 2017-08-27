#!/bin/sh
#
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)


if which java >/dev/null; then
   	echo "skip java 8 installation"
else
   	echo "java 8 installation"

        apt-get install -y software-properties-common debconf-utils
        add-apt-repository -y ppa:webupd8team/java
        apt-get update
        echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
        apt-get install -y oracle-java8-installer


        wget http://apache.ip-connect.vn.ua/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
        tar xzvf apache-maven-3.5.0-bin.tar.gz -C /usr/local/
        ln -s /usr/local/apache-maven-3.5.0/bin/mvn /usr/bin/mvn

        sudo apt-get install -y tomcat7

fi

