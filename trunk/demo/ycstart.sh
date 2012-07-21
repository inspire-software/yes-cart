#!/bin/sh

#
# YesCart demo startup script
#
# @author Denys Pavlov

YC_HOME=`pwd`

echo "================================================";
echo " Starting Derby database (background mode)      ";
echo "================================================";

export DERBY_INSTALL=$YC_HOME/yes-db
export CLASSPATH=$DERBY_INSTALL/derby.jar:$DERBY_INSTALL/derbytools.jar:$DERBY_INSTALL/derbyclient.jar:$DERBY_INSTALL/derbynet.jar:.
cd $DERBY_INSTALL

java -jar derbyrun.jar server start &

echo " Derby server started on port 1527...           ";

cd $YC_HOME/yes-server/bin

echo "================================================";
echo " Starting Tomcat Server                         ";
echo "================================================";

./startup.sh

cd $YC_HOME


echo "================================================";
echo " YesCart has been started successfully          ";
echo " YesShop URL http://localhost:8080/yes-shop     ";
echo " YesManager URL http://localhost:8080/yes-manager";
echo "================================================";

