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

## Notes

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