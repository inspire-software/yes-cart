#!/bin/sh

HOME=$(dirname `pwd`)

echo "================================================";
echo " Starting Derby database (background mode)      ";
echo "================================================";

export DERBY_INSTALL=$HOME/derbydb
export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
cd $DERBY_INSTALL/bin

sh startNetworkServer &

echo " Derby server started on port 1527...           ";

sleep 2s

echo "================================================";
echo " Starting Tomcat server (background mode)       ";
echo "================================================";

CONFIGURATION_SERVLET="http://localhost:8080/yes-shop/"

cd $HOME/bin

sh catalina.sh start

echo "$(date)";
echo "Now waiting until server is responding";

while true

do

  STATUS=$(curl -s -o /dev/null -w '%{http_code}' $CONFIGURATION_SERVLET)

  if [ $STATUS -eq 200 ]; then

    echo "Got 200! All done!"

    break

  else

    echo "Got $STATUS Server is starting, please wait..."

  fi

  sleep 5s

done

echo "$(date)"

$SHELL
