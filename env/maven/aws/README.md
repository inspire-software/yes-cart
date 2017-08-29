# AWS configuration 

## Build procedure

incorect   mvn clean install -PbuildAws

## Hosts substitutions

 * yesmysqlhost - will be substituted with aws rds mysql host
 * yummailhost - created aws SES 
 
## Logins and passwords 
 
  Logins and passwords will be unchanged

## MySql changes 

 * localhost has beed change to mysqlhost . real ip of mysqlhost will be placed into /etc/hosts

## AWS Image provisioning 

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

#curl -sL https://rpm.nodesource.com/setup_7.x | sudo -E bash -

curl -sL https://rpm.nodesource.com/setup_8.x | bash -

yum install -y nodejs 
npm install -g webpack webpack-dev-server --save
npm install -g spawn-sync --save
npm install --global gulp-cli
npm install --global gulp



--------- ec2-user

git clone https://github.com/inspire-software/yes-cart.git --depth 5 --branch aws
cd yes-cart


mvn install -Pmysql,paymentAll,ssl,buildAws -DskipTests=true

-------------------------------------
sudo su -

aws configure
#AWS Access Key ID [None]: SDAFASDFASDFAFSDVA
#AWS Secret Access Key [None]: pvSDQFQSDFQF+1c+s@$%GBEBES#SDFGVQC
#Default region name [None]:
#Default output format [None]:


cp /home/ec2-user/yes-cart/manager/jam/target/yes-manager.war /usr/share/tomcat7/webapps/
cp /home/ec2-user/yes-cart/web/api/target/yes-api.war /usr/share/tomcat7/webapps/
cp /home/ec2-user/yes-cart/web/store-wicket/target/yes-shop.war /usr/share/tomcat7/webapps/
mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/config
mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/archived
mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/incoming
mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/processing
mkdir -p /var/lib/tomcat7-ycdemo/import/SHOP10/processed
tee /var/lib/tomcat7-ycdemo/import/SHOP10/config/config.properties <<-'EOF'
config.0.group=YC DEMO: Initial Data
config.0.regex=import\\.zip
config.0.reindex=true
config.0.user=admin@yes-cart.com
config.0.pass=1234567
config.1.group=YC DEMO: IceCat Catalog
config.1.regex=import\\-EN,DE,UK,RU\\.zip
config.1.reindex=true
config.1.user=admin@yes-cart.com
config.1.pass=1234567
config.2.group=YC DEMO: Product images (IceCat)
config.2.regex=import\\-EN,DE,UK,RU\\-img\\.zip
config.2.reindex=true
config.2.user=admin@yes-cart.com
config.2.pass=1234567
EOF
cp /home/ec2-user/yes-cart/env/sampledata/demo-data/icecat/import/* /var/lib/tomcat7-ycdemo/import/SHOP10/incoming
chown tomcat:tomcat /var/lib/tomcat7-ycdemo -R



#service tomcat7 start


## Notes

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


#aws ec2 describe-security-groups --group-names yescart | jq '.SecurityGroups[0] | .GroupId'

aws rds create-db-instance \
    --db-instance-identifier yescartmysql \
    --db-instance-class db.t2.micro \
    --engine MySQL \
    --allocated-storage 20 \
    --master-username yesmaster \
    --master-user-password pwdMy34SqL \
    --backup-retention-period 3 \
    --vpc-security-group-ids $(aws ec2 describe-security-groups --group-names yescart | jq '.SecurityGroups[0] | .GroupId' | sed 's/\"//g')

aws rds wait db-instance-available --db-instance-identifier yescartmysql 

#host $(aws rds describe-db-instances --db-instance-identifier  yescartmysql | jq '.DBInstances[0] | .Endpoint.Address'  | sed 's/\"//g') | grep -Eo '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}'

export ycmysqldb=$(host $(aws rds describe-db-instances --db-instance-identifier  yescartmysql | jq '.DBInstances[0] | .Endpoint.Address'  | sed 's/\"//g') | grep -Eo '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}')
echo "$ycmysqldb        yesmysqlhost" | sudo tee --append /etc/hosts

sed -i -- 's/y3$PaSs/pwdMy34SqL/g' env/setup/dbi/mysql/dbinit.sql
sed -i -- "s/localhost/'%'/g" env/setup/dbi/mysql/dbinit.sql
mysql -uyesmaster -hyesmysqlhost -ppwdMy34SqL < "env/setup/dbi/mysql/dbinit.sql"

#Get domain name

export ycdemohost=$(aws ec2 describe-instances --filter Name=tag:Name,Values=YCDEMO | jq '.Reservations[0].Instances[0] | .PublicDnsName' | sed 's/\"//g')

mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; DELETE FROM TSHOPURL WHERE URL <> 'localhost'; UPDATE TSHOPURL SET URL = '$ycdemohost';" yes
mysql -uyes -hyesmysqlhost -ppwdMy34SqL -e "USE yes; INSERT INTO TSYSTEMATTRVALUE SET val = '/var/lib/tomcat7-ycdemo/import', code = 'JOB_LOCAL_FILE_IMPORT_FS_ROOT', SYSTEM_ID=100; " yes





#aws rds delete-db-instance  --db-instance-identifier  yescartmysql --skip-final-snapshot
