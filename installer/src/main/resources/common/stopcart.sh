#! /bin/sh

HOME=$(dirname `pwd`)

echo "================================================";
echo " Stopping Derby database                        ";
echo "================================================";

export DERBY_INSTALL=$HOME/derbydb
export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
cd $DERBY_INSTALL/lib

java -jar derbyrun.jar server shutdown

echo " Derby server stopped...                        ";

sleep 2s

echo "================================================";
echo " Stopping Tomcat server                         ";
echo "================================================";

cd $HOME/bin

sh catalina.sh stop

echo " Tomcat server stopped...                       ";

sleep 2s
