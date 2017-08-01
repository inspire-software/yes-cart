# AWS configuration 

## MySql changes 

 * localhost has beed change to mysqlhost . real ip of mysqlhost will be placed into /etc/hosts

aws rds create-db-instance \
    --db-instance-identifier mysqlhost \
    --db-instance-class db.t2.micro \
    --engine MySQL \
    --allocated-storage 20 \
    --master-username yes \
    --master-user-password U34y3PaSs98 \
    --backup-retention-period 3 \
    --region eu-central-1


aws rds describe-db-instances --db-instance-identifier  mysqlhost --region eu-central-1

aws rds delete-db-instance  --db-instance-identifier  mysqlhost --region eu-central-1 --skip-final-snapshot