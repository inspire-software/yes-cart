#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov


RUNDIR=`pwd`

YC_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

MVN="mvn"

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
    echo "  cpres     - copy extra resources to webapps   ";
    echo "                                                ";
    echo "  luke      - start luke                        ";
    echo "                                                ";
    echo "  nullsmtp  - start DevNullSmtp                 ";
    echo "                                                ";
    echo "  dbimysql  - initialise db for mysql           ";
    echo "                                                ";
    echo "  dbiderby  - initialise db for derby           ";
    echo "  derbygo   - start derby server                ";
    echo "  derbygob  - start derby server (in back mode) ";
    echo "  derbyend  - stop derby server                 ";
    echo "  derbycon  - connect to derby with ij          ";
    echo "  derbycon  - connect to derby with ij          ";
    echo "                                                ";
    echo "  builddemo - build demo                        ";
    echo "  buildidea - build idea                        ";
    echo "  builddev  - build dev                         ";
    echo "                                                ";
    echo "  aws       - initialise aws image              ";
    echo "================================================";
}

add_mvn_extra_dep() {

    echo "================================================";
    echo " Setup environment           ";
    echo "================================================";
    echo " Adding extra dependencies...";

#    cd "$YC_HOME/env/setup/lib3rdparty"

#    cd "$YC_HOME"

    echo " All dependencies are setup"
    echo "================================================";

}

cp_resources_to_webapp() {

    echo "================================================";
    echo " Copy resources to webapps                      ";
    echo "================================================";

    echo " copying resources... manager/server";
    cd "$YC_HOME/manager/server"
    "$MVN" validate -Ptemplates

    echo " copying resources... web/store";
    cd "$YC_HOME/web/store"
    "$MVN" validate -Ptemplates

    echo " copying resources... web/api";
    cd "$YC_HOME/web/api"
    "$MVN" validate -Ptemplates

}

init_db_mysql() {

    echo "================================================";
    echo " Initialise MySQL database                      ";
    echo "================================================";

    DBINITSCRIPT="$YC_HOME/env/setup/dbi/mysql/dbinit.sql"
    echo "Running init script as root user $DBINITSCRIPT"
    mysql -uroot -p < "$DBINITSCRIPT"

    echo "Initialisation complete."

}

db_derby_go() {

    echo "================================================";
    echo " Starting Derby database                        ";
    echo "================================================";

    export DERBY_INSTALL="$YC_HOME/env/derby"
    export CLASSPATH="$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:."
    cd "$DERBY_INSTALL/lib"

    java -jar derbyrun.jar server start

}

db_derby_gob() {

    echo "================================================";
    echo " Starting Derby database (background mode)      ";
    echo "================================================";

    export DERBY_INSTALL="$YC_HOME/env/derby"
    export CLASSPATH="$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:."
    cd "$DERBY_INSTALL/lib"

    java -jar derbyrun.jar server start &

    echo " Derby server started on port 1527...           ";
    echo "                                                ";
    echo " Tips:                                          ";
    echo " * if you see 'started and ready to accept connections on port 1527' but no prompt, just hit ENTER";
    echo " * if you encounter problems check http://www.inspire-software.com/documentation/wiki/docyescart/view/3.3.x/For%20Technical%20Users/Installation/From%20source/Derby%20quick%20start/";
    echo "                                                ";

}

db_derby_end() {

    echo "================================================";
    echo " Stopping Derby database                        ";
    echo "================================================";

    export DERBY_INSTALL="$YC_HOME/env/derby"
    export CLASSPATH="$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:."
    cd "$DERBY_INSTALL/lib"

    java -jar derbyrun.jar server shutdown

    echo " Derby server stopped...                        ";

}

init_db_derby() {

    echo "================================================";
    echo " Initialise Derby database                      ";
    echo "================================================";
    
    echo "Setting Derby environment variables";
    export DERBY_INSTALL="$YC_HOME/env/derby"
    export CLASSPATH="$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:."
    cd "$DERBY_INSTALL/lib"

    #java org.apache.derby.tools.sysinfo
    DBINITSCRIPT="$YC_HOME/env/setup/dbi/derby/dbinit.sql"
    echo "Running init script $DBINITSCRIPT"
    java -Dderby.system.home="$YC_HOME" -Dij.outfile="$YC_HOME/derbyinit.log" -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij "$DBINITSCRIPT"
#    java -Dderby.system.home=$YC_HOME org.apache.derby.tools.ij $DBINITSCRIPT
    
    cd "$YC_HOME";
    echo "Initialisation complete. See log: $YC_HOME/derbyinit.log"

}

db_derby_connect() {

    echo "================================================";
    echo " Starting Derby client                          ";
    echo "================================================";

    export DERBY_INSTALL="$YC_HOME/env/derby"
    export CLASSPATH="$DERBY_INSTALL/lib/derby.jar:$DERBY_INSTALL/lib/derbytools.jar:$DERBY_INSTALL/lib/derbyclient.jar:$DERBY_INSTALL/lib/derbynet.jar:."
    cd "$DERBY_INSTALL/lib"

    echo "Working directory is"
    pwd
    
    echo "Use:"
    echo "  connect 'jdbc:derby://localhost:1527/yes'; - to connect to main db";
    echo "  connect 'jdbc:derby://localhost:1527/yespay'; - to connect to payment gateway db";
    echo "  disconnect; - to quit";
    
    java -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij

}

start_luke() {

    echo "================================================";
    echo " Starting Luke (lucene index browser)           ";
    echo "================================================";

    java -jar "$YC_HOME/env/luke/luke-with-deps-6.5.jar" &

}

start_nullsmtp() {

    echo "================================================";
    echo " Starting DevNullSmtp (dummy SMTP server)       ";
    echo "================================================";

    java -jar "$YC_HOME/env/devnullsmtp/DevNullSmtp.jar" &

}

build_demo() {

    echo "================================================";
    echo " Build DEMO           ";
    echo "================================================";
    echo " ";

    "$MVN" clean install -PbuildDemo,mysql,ftEmbededLucene,paymentAll,pricerules -DskipTests=true

    cp $YC_HOME/manager/jam/target/yes-manager.war $YC_HOME/
    cp $YC_HOME/web/store-wicket/target/ROOT.war $YC_HOME/

}

build_dev() {

    echo "================================================";
    echo " Build DEV           ";
    echo "================================================";
    echo " ";

    "$MVN" clean install -Pdev,derby,ftEmbededLucene,paymentAll,pricerules -DskipTests=true

}

build_idea() {

    echo "================================================";
    echo " Build DEV           ";
    echo "================================================";
    echo " ";

    "$MVN" clean install -PdevIntellijIDEA,derby,ftEmbededLucene,paymentAll,pricerules -DskipTests=true

}

start_aws() {
    echo "================================================";
    echo " Starting YC AWS. Required:                     ";
    echo "  * AWS Access Key ID                           ";
    echo "  * AWS Secret Access Key                       ";
    echo "  * Default region                              ";
    echo "                                                ";
    echo "  * Run under root                              ";
    echo "================================================";

    if [[ $EUID -ne 0 ]]; then
        echo "You must be a root user" 2>&1
            exit 1
    fi


    aws configure

    aws ec2 create-security-group \
        --group-name yescart \
        --description "Yes cart security group. HTTP(s), ssh and mysql" 

    aws ec2 authorize-security-group-ingress \
        --group-name yescart \
        --protocol tcp --port 22 --cidr 0.0.0.0/0 
   
    aws ec2 authorize-security-group-ingress \
        --group-name yescart  \
        --protocol tcp --port 80 --cidr 0.0.0.0/0 

    aws ec2 authorize-security-group-ingress \
        --group-name yescart   \
        --protocol tcp --port 8080 --cidr 0.0.0.0/0 

    aws ec2 authorize-security-group-ingress \
        --group-name yescart    \
        --protocol tcp --port 443 --cidr 0.0.0.0/0 

    aws ec2 authorize-security-group-ingress \
        --group-name yescart \
        --protocol tcp --port 8443 --cidr 0.0.0.0/0 

    aws ec2 authorize-security-group-ingress \
        --group-name yescart \
        --protocol tcp --port 3306 --cidr 0.0.0.0/0 

    aws ec2 modify-instance-attribute \
        --groups $(aws ec2 describe-security-groups --group-names yescart | jq '.SecurityGroups[0] | .GroupId' | sed 's/\"//g') \
        --instance-id $(aws ec2 describe-instances --filter Name=tag:Name,Values=YCDEMO | jq '.Reservations[0].Instances[0] | .InstanceId' | sed 's/\"//g')

    aws rds create-db-instance \
        --db-instance-identifier yescartmysql \
        --db-instance-class db.t2.micro \
        --engine MySQL \
        --allocated-storage 20 \
        --master-username yesmaster \
        --master-user-password pwdMy34SqL \
        --backup-retention-period 3 \
        --vpc-security-group-ids $(aws ec2 describe-security-groups --group-names yescart | jq '.SecurityGroups[0] | .GroupId' | sed 's/\"//g')

    echo "================================================";
    echo " Please be patient, waiting for database ....   ";
    echo "================================================";

    aws rds wait db-instance-available --db-instance-identifier yescartmysql     


    export ycmysqldb=$(host $(aws rds describe-db-instances --db-instance-identifier  yescartmysql | jq '.DBInstances[0] | .Endpoint.Address'  | sed 's/\"//g') | grep -Eo '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}')
    echo "$ycmysqldb        yesmysqlhost" | sudo tee --append /etc/hosts
    echo "127.0.0.1            yummailhost"  | sudo tee --append /etc/hosts

    sed -i -- 's/y3$PaSs/pwdMy34SqL/g' env/setup/dbi/mysql/dbinit.sql
    sed -i -- "s/localhost/'%'/g" env/setup/dbi/mysql/dbinit.sql
    mysql -uyesmaster -hyesmysqlhost -ppwdMy34SqL < "env/setup/dbi/mysql/dbinit.sql"


    export ycdemohost=$(aws ec2 describe-instances --filter Name=tag:Name,Values=YCDEMO | jq '.Reservations[0].Instances[0] | .PublicDnsName' | sed 's/\"//g')

    mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; DELETE FROM TSHOPURL WHERE STOREURL_ID <> 12; UPDATE TSHOPURL SET URL = '$ycdemohost';" yes
    mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes;  INSERT INTO TSYSTEMATTRVALUE SET val = '/var/lib/tomcat7-ycdemo/import', code = 'JOB_LOCAL_FILE_IMPORT_FS_ROOT', SYSTEM_ID=100, GUID='JLFIR100'; " yes
    mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; DELETE FROM TSYSTEMATTRVALUE WHERE CODE='JOB_LOCAL_FILE_IMPORT_PAUSE'; INSERT INTO TSYSTEMATTRVALUE SET val = 'false', code = 'JOB_LOCAL_FILE_IMPORT_PAUSE', SYSTEM_ID=100, GUID='JLFP100'; " yes
    mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; DELETE FROM TSYSTEMATTRVALUE WHERE CODE='JOB_SEND_MAIL_PAUSE'; INSERT INTO TSYSTEMATTRVALUE SET val = 'false', code = 'JOB_SEND_MAIL_PAUSE', SYSTEM_ID=100, GUID='JSPM100'; " yes

    mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; UPDATE TSYSTEMATTRVALUE SET val = 'http://$ycdemohost:8080/' WHERE code = 'SYSTEM_DEFAULT_SHOP' AND SYSTEM_ID=100; " yes

    cp env/maven/aws/tomcat/keystore.jks /etc/tomcat7/keystore.jks
    cp env/maven/aws/tomcat/server.xml  /etc/tomcat7/server.xml

    chown tomcat:tomcat /var/lib/tomcat7/webapps/ROOT -R
    chown tomcat:tomcat /etc/tomcat7/keystore.jks
    chown tomcat:tomcat /etc/tomcat7/server.xml


    cp /home/ec2-user/yes-cart/manager/jam/target/yes-manager.war /usr/share/tomcat7/webapps/
    cp /home/ec2-user/yes-cart/web/api/target/yes-api.war /usr/share/tomcat7/webapps/
    cp /home/ec2-user/yes-cart/web/store-wicket/target/ROOT.war /usr/share/tomcat7/webapps/

    mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/config
    mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/archived
    mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/incoming
    mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/processing
    mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/processed
tee /var/lib/tomcat7-ycdemo/import/SHOP10/config/config.properties <<-'EOF'
config.0.group=YC DEMO: Initial Data
config.0.regex=import([\\.\\d{14}]*)\\.zip
config.0.reindex=true
config.0.user=admin@yes-cart.com
config.0.pass=1234567
config.1.group=YC DEMO: IceCat Catalog
config.1.regex=import\\-EN,DE,UK,RU([\\.\\d{14}]*)\\.zip
config.1.reindex=true
config.1.user=admin@yes-cart.com
config.1.pass=1234567
config.2.group=YC DEMO: Product images (IceCat)
config.2.regex=import\\-EN,DE,UK,RU\\-img([\\.\\d{14}]*)\\.zip
config.2.reindex=true
config.2.user=admin@yes-cart.com
config.2.pass=1234567
EOF
    cp /home/ec2-user/yes-cart/env/sampledata/demo-data/icecat/import/* /var/lib/tomcat7-ycdemo/import/SHOP10/processed
    mv /var/lib/tomcat7-ycdemo/import/SHOP10/processed/import-EN,DE,UK,RU.zip         /var/lib/tomcat7-ycdemo/import/SHOP10/processed/import-EN,DE,UK,RU.20170906010101.zip
    mv /var/lib/tomcat7-ycdemo/import/SHOP10/processed/import-EN,DE,UK,RU-img.zip /var/lib/tomcat7-ycdemo/import/SHOP10/processed/import-EN,DE,UK,RU-img.20170906020202.zip
    chown tomcat:tomcat /var/lib/tomcat7-ycdemo -R

    sudo /sbin/iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
    sudo /sbin/iptables -t nat -I PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443

    service tomcat7 start

    echo " Yes-Cart is available on "
    echo " http://$ycdemohost:8080/yes-manager/ "
    echo " http://$ycdemohost:8080/ "

    exit


}


if [ $1 ];
then
    if [ $1 = "help" ];
    then
        show_help;
        exit 0;
    elif [ $1 = "i3rd" ];
    then
        cd "$YC_HOME"
        add_mvn_extra_dep;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "cpres" ];
    then
        cd "$YC_HOME"
        cp_resources_to_webapp;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "dbimysql" ];
    then
        cd "$YC_HOME"
        init_db_mysql;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "dbiderby" ];
    then
        cd "$YC_HOME"
        init_db_derby;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "derbygo" ];
    then
        cd "$YC_HOME"
        db_derby_go;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "derbygob" ];
    then
        cd "$YC_HOME"
        db_derby_gob;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "derbyend" ];
    then
        cd "$YC_HOME"
        db_derby_end;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "derbycon" ];
    then
        cd "$YC_HOME"
        db_derby_connect;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "env" ];
    then
        cd "$YC_HOME"
        show_env;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "luke" ];
    then
        cd "$YC_HOME"
        start_luke;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "nullsmtp" ];
    then
        cd "$YC_HOME"
        start_nullsmtp;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "builddemo" ];
    then
        cd "$YC_HOME"
        build_demo;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "buildidea" ];
    then
        cd "$YC_HOME"
        build_idea;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "builddev" ];
    then
        cd "$YC_HOME"
        build_dev;
        cd "$RUNDIR"
        exit 0;
    elif [ $1 = "aws" ];
    then
        cd "$YC_HOME"
        start_aws;
        cd "$RUNDIR"
        exit 0;

    else
        echo "Provide a command..."; show_help; exit 100;
    fi
else echo "Provide a command..."; show_help; exit 100;
fi
