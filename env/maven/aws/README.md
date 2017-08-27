# AWS configuration 

## Build procedure

  mvn clean install -PbuildAws

## Hosts substitutions

 * yesmysqlhost - will be substituted with aws rds mysql host
 * yespaymysqlhost  - will be substituted with aws rds mysql host
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
yum install -y tomcat



curl -sL https://rpm.nodesource.com/setup_7.x | sudo -E bash -

yum install -y nodejs 
npm install -g webpack webpack-dev-server --save
npm install -g spawn-sync --save
npm install --global gulp-cli




wget http://www-eu.apache.org/dist/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar xzvf apache-maven-3.5.0-bin.tar.gz -C /usr/local/
ln -s /usr/local/apache-maven-3.5.0/bin/mvn /usr/bin/mvn


--------- ec2-user

git clone https://github.com/inspire-software/yes-cart.git --depth 5 --branch aws
cd yes-cart

mvn clean install -PbuildAws




## Notes

[root@ip-172-31-29-86 ~]# aws configure
AWS Access Key ID [None]: SDAFASDFASDFAFSDVA
AWS Secret Access Key [None]: pvSDQFQSDFQF+1c+s@$%GBEBES#SDFGVQC
Default region name [None]:
Default output format [None]:


aws ec2 create-security-group \
    --group-name yescart \
    --description "Yes cart security group. HTTP(s), ssh and mysql"  \
    --region eu-central-1

aws ec2 authorize-security-group-ingress \
    --group-name yescart \
    --protocol tcp --port 22 --cidr 0.0.0.0/0 \
    --region eu-central-1
   
aws ec2 authorize-security-group-ingress \
    --group-name yescart  \
    --protocol tcp --port 80 --cidr 0.0.0.0/0 \
    --region eu-central-1

aws ec2 authorize-security-group-ingress \
    --group-name yescart   \
    --protocol tcp --port 8080 --cidr 0.0.0.0/0 \
    --region eu-central-1

aws ec2 authorize-security-group-ingress \
    --group-name yescart    \
    --protocol tcp --port 443 --cidr 0.0.0.0/0 \
    --region eu-central-1

aws ec2 authorize-security-group-ingress \
    --group-name yescart \
    --protocol tcp --port 8443 --cidr 0.0.0.0/0 \
    --region eu-central-1



aws rds create-db-instance \
    --db-instance-identifier yescartmysql \
    --db-instance-class db.t2.micro \
    --engine MySQL \
    --allocated-storage 20 \
    --master-username yes \
    --master-user-password U34y3PaSs98 \
    --backup-retention-period 3 \
    --region eu-central-1

aws rds wait db-instance-available --db-instance-identifier yescartmysql  --region eu-central-1
aws rds describe-db-instances --db-instance-identifier  yescartmysql --region eu-central-1 | jq '.DBInstances[0] | .Endpoint.Address'  | sed 's/\"//g'
aws rds delete-db-instance  --db-instance-identifier  yescartmysql --region eu-central-1 --skip-final-snapshot