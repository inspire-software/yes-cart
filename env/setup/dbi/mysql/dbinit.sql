-- Initialisation SQL for YesCart
-- @author Denys Pavlov

CREATE DATABASE yes CHARACTER SET utf8 COLLATE utf8_general_ci;

SET character_set_client = utf8;
SET character_set_results = utf8;
SET character_set_connection = utf8;

-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yes.* TO yes@localhost IDENTIFIED BY 'y3$PaSs';

CREATE DATABASE yespay CHARACTER SET utf8 COLLATE utf8_general_ci;
-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yespay.* TO yespay@localhost IDENTIFIED BY 'y3$PaSs';

SET foreign_key_checks = 0;

USE yespay;

SOURCE core-modules/core-module-payment-base/src/main/resources/sql/mysql/create-tables.sql;
SOURCE core-modules/core-module-payment-base/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-liqpay/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-cybersource/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-authorizenet/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-paypal/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-postfinance/src/main/resources/sql/payinitdata.sql;

USE yes;

SOURCE persistence/sql/resources/mysql/create-tables.sql;
SOURCE env/setup/dbi/initdata.sql;

SET foreign_key_checks = 1;
