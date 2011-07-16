#! /bin/bash                                                                                                                                                                                              
#. /etc/profile                                                                                                                                                                                           
#. $HOME/.bash_profile

echo "======================================================================";
echo "> Yes-cart initialization script                                     <";
echo "======================================================================";
echo "";
BASE_DIR=`pwd`;
echo "> BASE_DIR is: $BASE_DIR";
echo "";
if [ $1 ];
  then
    export FLEX_HOME=$1;
fi
echo "> FLEX_HOME is: $FLEX_HOME";
echo "> make sure flex sdk is 4th version";
echo "";
echo "======================================================================";
echo "> Installing mvn 3rd party artifacts                                 <";
echo "======================================================================";
echo "";
echo "> authorize-client 1.4.2 ";
mvn install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=$BASE_DIR/util/lib3rdparty/authozize.net/anet-java-sdk-1.4.2.jar 
echo "";
echo "> cybersource-client, cybersource-security 1.5";
mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-client -Dversion=1.5 -Dpackaging=jar -Dfile=$BASE_DIR//util/lib3rdparty/cybersource/cybsclients15.jar                                                                 
mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-security -Dversion=1.5 -Dpackaging=jar -Dfile=$BASE_DIR//util/lib3rdparty/cybersource/cybssecurity.jar                                                                
echo ""
echo "> payflow 4.31";
mvn install:install-file -DgroupId=paypal.payflow -DartifactId=payflow-client -Dversion=4.31 -Dpackaging=jar -Dfile=$BASE_DIR/util/lib3rdparty/payflow/payflow.jar
echo "";
echo "> paypal-client 5.1.1";
mvn install:install-file -DgroupId=com.paypal -DartifactId=paypal-client -Dversion=5.1.1 -Dpackaging=jar -Dfile=$BASE_DIR/util/lib3rdparty/paypal/paypal_base.jar
echo "";
echo "> GeDA 1.1.2";
mvn install:install-file -DgroupId=dp.lib -DartifactId=dp.lib-dto.GeDA -Dversion=1.1.2 -Dpackaging=jar -Dfile=$BASE_DIR/util/lib3rdparty/geda/dp.lib-dto.GeDA-1.1.2.jar
echo "";
echo "======================================================================";
echo "> Yes-cart build                                                     <";
echo "======================================================================";
echo "";

mvn clean
if [ $? -eq 0 ];                                                                                                                                                                                      
  then 
    echo "> mvn clean... SUCCESS";
    echo "";
  else 
    echo "> mvn clean... FAILED";
    echo "";
    exit 1;                                                                                                                        
fi

cd ./manager/client
ant depl
if [ $? -eq 0 ];                                                                                                                                                                                      
  then 
    echo "> ant shopmanager module... SUCCESS";
    echo "";
  else 
    echo "> ant shopmanager... FAILED";
    echo "";
    exit 1;                                                                                                                        
fi
cd ../../

#rem mvn -o -Dmaven.test.skip=true install
mvn -o install
if [ $? -eq 0 ];                                                                                                                                                                                      
  then 
    echo "> mvn modules... SUCCESS";
    echo "";
  else 
    echo "> mvn modules... FAILED";
    echo "";
    exit 1;                                                                                                                        
fi
