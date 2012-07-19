CREATE DATABASE yes CHARACTER SET utf8 COLLATE utf8_general_ci;
-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yes.* TO yes@localhost IDENTIFIED BY 'y3$PaSs';

USE yes;

SET foreign_key_checks = 0;
SOURCE persistence/sql/resources/mysql/create-tables.sql;
SOURCE util/install/dbinit/initdata.sql;
SET foreign_key_checks = 1;

CREATE DATABASE yespay CHARACTER SET utf8 COLLATE utf8_general_ci;
-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yespay.* TO yespay@localhost IDENTIFIED BY 'y3$PaSs';

USE yespay;

SET foreign_key_checks = 0;
SOURCE core-modules/core-module-payment-base/src/main/resources/sql/mysql/create-npa-pay.sql;
SOURCE core-modules/core-module-payment-base/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-capp/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-gcwm/src/main/resources/sql/payinitdata.sql;
SET foreign_key_checks = 1;
