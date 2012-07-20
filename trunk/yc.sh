#!/bin/sh

YC_HOME=`pwd`

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
    echo " YesCart environment script                     ";
    echo "================================================";
    echo " commands:                                      ";
    echo "  env       - show environment variables        ";
    echo "                                                ";
    echo "  i3rd      - setup necessary artifacts 3rd     ";
    echo "              party artifacts for maven         ";
    echo "                                                ";
    echo "  cpclient  - copy swf (from target) to webapps ";
    echo "                                                ";
    echo "  dbimysql  - initialise db for mysql           ";
    echo "                                                ";
    echo "  dbiderby  - initialise db for derby           ";
    echo "  derbygo   - start derby server                ";
    echo "  derbycon  - connect to derby with ij          ";
    echo "                                                ";
    echo "================================================";
}

add_mvn_extra_dep() {

    echo "================================================";
    echo " Setup environment           ";
    echo "================================================";
    echo "Adding extra dependencies...";

    cd $YC_HOME/env/setup/lib3rdparty

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/authorize.net/anet-java-sdk-1.4.2.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/cybersource/cybssecurity.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-security -Dversion=1.5 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/cybersource/cybsclients15.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-client -Dversion=1.5 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/payflow/payflow.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=paypal.payflow -DartifactId=payflow-client -Dversion=4.31 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/paypal/paypal_base.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.paypal -DartifactId=paypal-client -Dversion=5.1.1 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/google.checkout/checkout-sdk-2.5.1.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.google.checkout -DartifactId=checkout-sdk -Dversion=2.5.1 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/jai/mlibwrapper_jai.jar
    $M2_HOME/bin/mvn install:install-file -DgroupId=com.sun.media -DartifactId=mlibwrapper_jai -Dversion=1.1.3 -Dpackaging=jar -Dfile=$LIBFILE

    cd $YC_HOME

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
    echo " Initialise MySQL database                      ";
    echo "================================================";

    DBINITSCRIPT=$YC_HOME/env/setup/dbi/mysql/dbinit.sql
    echo "Running init script as root user $DBINITSCRIPT"
    mysql -uroot -p < $DBINITSCRIPT

    echo "Initialisation complete."

}

db_derby_go() {

    echo "================================================";
    echo " Starting Derby database                        ";
    echo "================================================";

    export DERBY_INSTALL=$YC_HOME/env/derby
    export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
    cd $DERBY_INSTALL/lib

    java -jar derbyrun.jar server start

}

init_db_derby() {

    echo "================================================";
    echo " Initialise Derby database                      ";
    echo "================================================";
    
    echo "Setting Derby environment variables";
    export DERBY_INSTALL=$YC_HOME/env/derby
    export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
    cd $DERBY_INSTALL/lib

    #java org.apache.derby.tools.sysinfo
    DBINITSCRIPT=$YC_HOME/env/setup/dbi/derby/dbinit.sql
    echo "Running init script $DBINITSCRIPT"
    java -Dderby.system.home=$YC_HOME -Dij.outfile=$YC_HOME/derbyinit.log org.apache.derby.tools.ij $DBINITSCRIPT
#    java -Dderby.system.home=$YC_HOME org.apache.derby.tools.ij $DBINITSCRIPT
    
    cd $YC_HOME;
    echo "Initialisation complete. See log: $YC_HOME/derbyinit.log"

}

db_derby_connect() {

    echo "================================================";
    echo " Starting Derby client                          ";
    echo "================================================";

    export DERBY_INSTALL=$YC_HOME/env/derby
    export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
    cd $DERBY_INSTALL/lib

    echo "Working directory is"
    pwd
    
    echo "Use:"
    echo "  connect 'jdbc:derby://localhost:1527/yes'; - to connect to main db";
    echo "  connect 'jdbc:derby://localhost:1527/yespay'; - to connect to payment gateway db";
    echo "  disconnect; - to quit";
    
    java org.apache.derby.tools.ij

}


if [ $1 ];
then
    if [ $1 = "help" ];
    then
        show_help;
        exit 0;
    elif [ $1 = "i3rd" ];
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
    elif [ $1 = "dbiderby" ];
    then
        init_db_derby;
        exit 0;
    elif [ $1 = "derbygo" ];
    then
        db_derby_go;
        exit 0;
    elif [ $1 = "derbycon" ];
    then
        db_derby_connect;
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