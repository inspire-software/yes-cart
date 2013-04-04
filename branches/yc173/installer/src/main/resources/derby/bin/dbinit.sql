/*
Initialisation SQL for YesCart
@author Igor Azarny
 */
connect 'jdbc:derby://localhost:1527/yes;create=true;unicode=true';
run 'create-tables.sql';
run 'initdata.sql';
disconnect;
connect 'jdbc:derby://localhost:1527/yespay;create=true;unicode=true';
run 'pay-create-tables.sql';
run 'base-payinitdata-en.sql';
run 'capp-payinitdata-en.sql';
run 'gcwm-payinitdata-en.sql';
disconnect;
exit;

