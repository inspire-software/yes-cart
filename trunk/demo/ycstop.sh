#!/bin/sh

#
# YesCart demo shutdown script
#
# @author Denys Pavlov


YC_HOME=`pwd`


echo "================================================";
echo " Stopping Tomcat Server                         ";
echo "================================================";

cd $YC_HOME/yes-server/bin

./shutdown.sh

echo "================================================";
echo " Stopping Derby database (background mode)      ";
echo "================================================";

export DERBY_INSTALL=$YC_HOME/yes-db
export CLASSPATH=$DERBY_INSTALL/derby.jar:$DERBY_INSTALL/derbytools.jar:$DERBY_INSTALL/derbyclient.jar:$DERBY_INSTALL/derbynet.jar:.
cd $DERBY_INSTALL

java -jar derbyrun.jar server shutdown

echo " Derby server stopped...                        ";

cd $YC_HOME

echo "================================================";
echo " YesCart has been shutdown successfully         ";
echo "================================================";
