#!/bin/sh

show_env() {
    echo "================================================";
    echo " Environment variables                          ";
    echo "================================================";
    echo " user.name     : $HOME";
    echo " user.home     : $HOME";
    echo " ANT_HOME  : $ANT_HOME";
    echo " FLEX_HOME : $FLEX_HOME";
    echo " JAVA_HOME : $JAVA_HOME";
    echo " M2_HOME   : $M2_HOME";
    echo "================================================";
}

show_help() {
    echo "================================================";
    echo " Use this script to setup environment           ";
    echo "================================================";
    echo " commands:                                      ";
    echo "  env       - show environment variables        ";
    echo "  setup     - setup necessary artifacts for     ";
    echo "              maven                             ";
    echo "  cpclient  - copy swf (from target) to webapps ";
    echo "  dbimysql  - initialise db for mysql           ";
    echo "================================================";
}

add_mvn_extra_dep() {

    echo "================================================";
    echo " Setup environment           ";
    echo "================================================";
    echo "Adding extra dependencies...";

    cd ./util/lib3rdparty/authozize.net
    $M2_HOME/bin/mvn install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=anet-java-sdk-1.4.2.jar
    cd ../../../

    cd ./util/lib3rdparty/authozize.net
    $M2_HOME/bin/mvn install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=anet-java-sdk-1.4.2.jar
    cd ../../../

    cd ./util/lib3rdparty/cybersource
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-security -Dversion=1.5 -Dpackaging=jar -Dfile=cybssecurity.jar
    cd ../../../

    cd ./util/lib3rdparty/cybersource
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-client -Dversion=1.5 -Dpackaging=jar -Dfile=cybsclients15.jar
    cd ../../../

    cd ./util/lib3rdparty/payflow
    $M2_HOME/bin/mvn install:install-file -DgroupId=paypal.payflow -DartifactId=payflow-client -Dversion=4.31 -Dpackaging=jar -Dfile=payflow.jar
    cd ../../../

    cd ./util/lib3rdparty/paypal
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.paypal -DartifactId=paypal-client -Dversion=5.1.1 -Dpackaging=jar -Dfile=paypal_base.jar
    cd ../../../

    cd ./util/lib3rdparty/google.checkout
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.google.checkout -DartifactId=checkout-sdk -Dversion=2.5.1 -Dpackaging=jar -Dfile=checkout-sdk-2.5.1.jar
    cd ../../../

    cd ./util/lib3rdparty/jai
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.sun.media -DartifactId=mlibwrapper_jai -Dversion=1.1.3 -Dpackaging=jar -Dfile=mlibwrapper_jai.jar
    cd ../../../

    echo "================================================";

}

cp_client_to_webapp() {

    echo "================================================";
    echo " Copy SWF client to webapps                     ";
    echo "================================================";

    echo " copying... `pwd`/manager/client/target/ShopManagerApplication.swf";
    cp ./manager/client/target/ShopManagerApplication.swf  ./manager/server/src/main/webapp/ShopManagerApplication.swf

}

init_db_mysql() {

    echo "================================================";
    echo " Initialise database                            ";
    echo "================================================";

    echo "Creating yes";
    mysql -uroot -p -e "CREATE DATABASE yes CHARACTER SET utf8 COLLATE utf8_general_ci;"
    echo "Loading yes tables";
    mysql -uroot -p yes < persistence/sql/resources/mysql/create-tables.sql
    echo "Loading yes initial data";
    mysql -uroot -p yes < util/install/initdata.sql

    echo "Creating yespay";
    mysql -uroot -p -e "CREATE DATABASE yespay CHARACTER SET utf8 COLLATE utf8_general_ci;"
    echo "Loading yespay tables";
    mysql -uroot -p yespay < core-modules/core-module-payment-base/src/main/resources/sql/mysql/create-npa-pay.sql
    echo "Loading yespay.base initial data";
    mysql -uroot -p yespay < core-modules/core-module-payment-base/src/main/resources/sql/payinitdata.sql
    echo "Loading yespay.capp initial data";
    mysql -uroot -p yespay < core-modules/core-module-payment-capp/src/main/resources/sql/payinitdata.sql
    echo "Loading yespay.gcwm initial data";
    mysql -uroot -p yespay < core-modules/core-module-payment-gcwm/src/main/resources/sql/payinitdata.sql

}

if [ $1 ];
then
    if [ $1 = "help" ];
    then
        show_help;
        exit 0;
    elif [ $1 = "setup" ];
    then
        add_mvn_extra_dep;
        exit 0;
    elif [ $1 = "cpclient" ];
    then
        cp_client_to_webapp;
        exit 0;
    elif [ $1 = "dbimysql" ];
    then
        init_db_mysql;
        exit 0;
    elif [ $1 = "env" ];
    then
        show_env;
        exit 0;
    else
        echo "Provide command..."; show_help; exit 100;
    fi
else echo "Provide command..."; show_help; exit 100;
fi