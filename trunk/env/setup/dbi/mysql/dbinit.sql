CREATE DATABASE yes CHARACTER SET utf8 COLLATE utf8_general_ci;
-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yes.* TO yes@localhost IDENTIFIED BY 'y3$PaSs';

CREATE DATABASE yespay CHARACTER SET utf8 COLLATE utf8_general_ci;
-- grant statement creates the user if the user does not exist (as long as the no_auto_create_user is not set).
GRANT ALL ON yespay.* TO yespay@localhost IDENTIFIED BY 'y3$PaSs';

SET foreign_key_checks = 0;

USE yespay;

SOURCE core-modules/core-module-payment-base/src/main/resources/sql/mysql/create-tables.sql;
SOURCE core-modules/core-module-payment-base/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-capp/src/main/resources/sql/payinitdata.sql;
SOURCE core-modules/core-module-payment-gcwm/src/main/resources/sql/payinitdata.sql;

USE yes;

SOURCE persistence/sql/resources/mysql/create-tables.sql;
SOURCE env/setup/dbi/initdata.sql;

SET foreign_key_checks = 1;
