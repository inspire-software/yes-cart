/*
Initialisation SQL for YesCart
@author Denys Pavlov
 */
connect 'jdbc:derby://localhost:1527/yes;create=true;unicode=true';
run '../../../persistence/sql/resources/derby/create-tables.sql';
run '../../../env/setup/dbi/initdata.sql';
run '../../../env/setup/dbi/derby/initdata-fix.sql';
disconnect;
connect 'jdbc:derby://localhost:1527/yespay;create=true;unicode=true';
run '../../../payment-modules/payment-module-base/src/main/resources/sql/derby/create-tables.sql';
run '../../../payment-modules/payment-module-base/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-liqpay/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-cybersource/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-authorizenet/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-paypal/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-paysera/src/main/resources/sql/payinitdata.sql';
run '../../../payment-modules/payment-module-postfinance/src/main/resources/sql/payinitdata.sql';
disconnect;
