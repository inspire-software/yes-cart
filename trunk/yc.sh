#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov


RUNDIR=`pwd`

YC_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

MVN=$M2_HOME/bin/mvn

show_env() {
    echo "================================================";
    echo " Environment variables                          ";
    echo "================================================";
    echo " user.name     : $HOME";
    echo " user.home     : $HOME";
    echo " YC_HOME       : $YC_HOME";
    echo " RUNNIG_HOME   : $RUNDIR";
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
    echo "  derbygob  - start derby server (in back mode) ";
    echo "  derbyend  - stop derby server                 ";
    echo "  derbycon  - connect to derby with ij          ";
    echo "                                                ";
    echo "  pkgdemo   - prepare demo package              ";
    echo "  clndemo   - clean demo package                ";
    echo "================================================";
}

add_mvn_extra_dep() {

    echo "================================================";
    echo " Setup environment           ";
    echo "================================================";
    echo "Adding extra dependencies...";

    cd $YC_HOME/env/setup/lib3rdparty

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/authorize.net/anet-java-sdk-1.4.2.jar
    $MVN install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/cybersource/cybssecurity.jar
    $MVN install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-security -Dversion=1.5 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/cybersource/cybsclients15.jar
    $MVN install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-client -Dversion=1.5 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/payflow/payflow.jar
    $MVN install:install-file -DgroupId=paypal.payflow -DartifactId=payflow-client -Dversion=4.31 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/paypal/paypal_base.jar
    $MVN install:install-file -DgroupId=com.paypal -DartifactId=paypal-client -Dversion=5.1.1 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/google.checkout/checkout-sdk-2.5.1.jar
    $MVN install:install-file -DgroupId=com.google.checkout -DartifactId=checkout-sdk -Dversion=2.5.1 -Dpackaging=jar -Dfile=$LIBFILE

    LIBFILE=$YC_HOME/env/setup/lib3rdparty/jai/mlibwrapper_jai.jar
    $MVN install:install-file -DgroupId=com.sun.media -DartifactId=mlibwrapper_jai -Dversion=1.1.3 -Dpackaging=jar -Dfile=$LIBFILE

    cd $YC_HOME

    echo "================================================";

}

cp_client_to_webapp() {

    echo "================================================";
    echo " Copy SWF client to webapps                     ";
    echo "================================================";

    echo " copying... $YC_HOME/manager/client/target/ShopManagerApplication.swf";
    cp $YC_HOME/manager/client/target/ShopManagerApplication.swf  $YC_HOME/manager/server/src/main/webapp/ShopManagerApplication.swf

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

db_derby_gob() {

    echo "================================================";
    echo " Starting Derby database (background mode)      ";
    echo "================================================";

    export DERBY_INSTALL=$YC_HOME/env/derby
    export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
    cd $DERBY_INSTALL/lib

    java -jar derbyrun.jar server start &

    echo " Derby server started on port 1527...           ";

}

db_derby_end() {

    echo "================================================";
    echo " Stopping Derby database                        ";
    echo "================================================";

    export DERBY_INSTALL=$YC_HOME/env/derby
    export CLASSPATH=$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:.
    cd $DERBY_INSTALL/lib

    java -jar derbyrun.jar server shutdown

    echo " Derby server stopped...                        ";

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
    java -Dderby.system.home=$YC_HOME -Dij.outfile=$YC_HOME/derbyinit.log -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij $DBINITSCRIPT
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
    
    java -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij

}

prepare_demo_clean() {

    echo "================================================";
    echo " Cleaning DEMO package                          ";
    echo "================================================";
    echo "                                                ";

    echo " Cleaning Tomcat Logs                           ";
    rm -f $YC_HOME/demo/yes-server/logs/*.log
    rm -f $YC_HOME/demo/yes-server/logs/catalina.out
    echo " done...                                        ";

    echo " Cleaning Tomcat Temp                           ";
    rm -rf $YC_HOME/demo/yes-server/work/*
    rm -rf $YC_HOME/demo/yes-server/temp/*
    echo " done...                                        ";

    echo " Cleaning Derby bundle $YC_HOME/demo/yes-db/*   ";
    rm -rf $YC_HOME/demo/yes-db/*
    echo " done...                                        ";

    YESCONF=$YC_HOME/demo/yes-server/conf/Catalina/localhost

    YESCONFSHOP=$YESCONF/yes-shop.xml
    YESCONFMANAGER=$YESCONF/yes-manager.xml

    echo " Removing old context.xml:                      ";
    echo " $YESCONFSHOP                                   ";
    rm -f $YESCONFSHOP
    echo " $YESCONFMANAGER                                ";
    rm -f $YESCONFMANAGER

    YESWEBAPPS=$YC_HOME/demo/yes-server/webapps

    YESSHOP_OLD=$YESWEBAPPS/yes-shop
    YESSHOPWAR_OLD=$YESWEBAPPS/yes-shop.war
    YESMANAGER_OLD=$YESWEBAPPS/yes-manager
    YESMANAGERWAR_OLD=$YESWEBAPPS/yes-manager.war

    echo " Removing old wars:                             ";
    echo " $YESSHOP_OLD                                   ";
    rm -rf $YESSHOP_OLD
    echo " $YESSHOPWAR_OLD                                ";
    rm -f $YESSHOPWAR_OLD
    echo " $YESMANAGER_OLD                                ";
    rm -rf $YESMANAGER_OLD
    echo " $YESMANAGERWAR_OLD                             ";
    rm -f $YESMANAGERWAR_OLD
    echo " done...                                        ";


}

prepare_demo_pkg() {

    echo "================================================";
    echo " Preparing DEMO package                         ";
    echo "================================================";
    echo "                                                ";
    echo " Make sure that you have prepared derby dbs and ";
    echo " created a full maven build with derby profile. ";
    echo "                                                ";

    echo " Copying Derby package                          ";
    cp $YC_HOME/env/derby/lib/*.jar $YC_HOME/demo/yes-db
    echo " done...                                        ";

    YESDB_OLD=$YC_HOME/demo/yes-db/yes
    YESDB_NEW=$YC_HOME/env/derby/lib/yes
    echo " Copying new db: $YESDB_NEW                     ";
    cp -r $YESDB_NEW $YESDB_OLD
    echo " done...                                        ";

    YESPAYDB_OLD=$YC_HOME/demo/yes-db/yespay
    YESPAYDB_NEW=$YC_HOME/env/derby/lib/yespay
    echo " Copying new db: $YESPAYDB_NEW                  ";
    cp -r $YESPAYDB_NEW $YESPAYDB_OLD
    echo " done...                                        ";

    YESWEBAPPS=$YC_HOME/demo/yes-server/webapps


    YESSHOPWAR_NEW=$YC_HOME/web/store/target/yes-shop.war
    YESMANAGERWAR_NEW=$YC_HOME/manager/server/target/yes-manager.war
    echo " Copying new wars:                              ";
    echo " $YESSHOPWAR_NEW                                ";
    echo " $YESMANAGERWAR_NEW                             ";
    cp $YESSHOPWAR_NEW $YESWEBAPPS
    cp $YESMANAGERWAR_NEW $YESWEBAPPS
    echo " done...                                        ";

    echo " Creating zip package...                        ";
    rm -f $YC_HOME/yescart.zip
    zip -r --exclude=*.svn* $YC_HOME/yescart.zip $YC_HOME/demo
    echo " done...                                        ";
    echo "                                                ";
    echo "================================================";
    echo " Demo package created                           ";
    echo "================================================";

}


if [ $1 ];
then
    if [ $1 = "help" ];
    then
        show_help;
        exit 0;
    elif [ $1 = "i3rd" ];
    then
        cd $YC_HOME
        add_mvn_extra_dep;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "cpclient" ];
    then
        cd $YC_HOME
        cp_client_to_webapp;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "dbimysql" ];
    then
        cd $YC_HOME
        init_db_mysql;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "dbiderby" ];
    then
        cd $YC_HOME
        init_db_derby;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "derbygo" ];
    then
        cd $YC_HOME
        db_derby_go;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "derbygob" ];
    then
        cd $YC_HOME
        db_derby_gob;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "derbyend" ];
    then
        cd $YC_HOME
        db_derby_end;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "derbycon" ];
    then
        cd $YC_HOME
        db_derby_connect;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "env" ];
    then
        cd $YC_HOME
        show_env;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "clndemo" ];
    then
        cd $YC_HOME
        prepare_demo_clean;
        cd $RUNDIR
        exit 0;
    elif [ $1 = "pkgdemo" ];
    then
        cd $YC_HOME
        prepare_demo_clean;
        prepare_demo_pkg;
        cd $RUNDIR
        exit 0;
    else
        echo "Provide command..."; show_help; exit 100;
    fi
else echo "Provide command..."; show_help; exit 100;
fi