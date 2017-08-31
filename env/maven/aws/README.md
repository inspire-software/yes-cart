# AWS configuration 

Start Yes Cart t2.medium instance and assign Name=YCDEMO 

## Hosts substitutions

 * yesmysqlhost - will be substituted with aws rds mysql host
 * yummailhost - created aws SES 
 
## Logins and passwords 
 
  Logins and passwords will be unchanged

## MySql changes 

 * localhost has beed change to mysqlhost . real ip of mysqlhost will be placed into /etc/hosts


## Start

Use ys.sh 

## AWS Image provisioning 

```
sudo su -
yum update -y
yum install -y jq
yum install -y mysql
yum install -y git
yum install -y wget
yum install -y curl
yum install -y net-tools
yum install -y mc
yum install -y unzip
yum install -y xmlstarlet
yum install -y tomcat7


wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u144-b01/090f390dda5b47b9b721c7dfaa008135/jdk-8u144-linux-x64.rpm"
rpm -ivh jdk-8u144-linux-x64.rpm
/usr/sbin/alternatives --install /usr/bin/java java  /usr/java/jdk1.8.0_144/bin/java 20000
/usr/sbin/alternatives --set java /usr/java/jdk1.8.0_144/bin/java



wget http://www-eu.apache.org/dist/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar xzvf apache-maven-3.5.0-bin.tar.gz -C /usr/local/

ln -s /usr/local/apache-maven-3.5.0/bin/mvn /usr/bin/mvn

curl -sL https://rpm.nodesource.com/setup_8.x | bash -

yum install -y nodejs 
npm install -g webpack webpack-dev-server --save
npm install -g spawn-sync --save
npm install --global gulp-cli
npm install --global gulp

exit

cd ~

git clone https://github.com/inspire-software/yes-cart.git
cd yes-cart
mvn install -Pmysql,paymentAll,ssl,buildAws -DskipTests=true

```


