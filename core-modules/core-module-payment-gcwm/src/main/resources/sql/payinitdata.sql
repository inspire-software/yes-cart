




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (450, 'liqPayPaymentGateway',
'LP_MERCHANT_ID',
'!!!PROVIDE VALUE!!!'
, 'Merchant Id', 'Merchant Id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (451, 'liqPayPaymentGateway',
'LP_MERCHANT_KEY',
'!!!PROVIDE VALUE!!!'
, 'Merchant signature', 'Merchant signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (452, 'liqPayPaymentGateway',
'LP_RESULT_URL',
'http://@domain@/yes-shop/liqpayreturn'
, 'Page URL to show payment result', 'Page URL to show payment result');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (453, 'liqPayPaymentGateway',
'LP_SERVER_URL',
'http://@domain@/yes-shop/liqpaycallback'
, 'Call back URL with payment result.', 'Call back URL with payment result. ');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_POST_URL',
'https://www.liqpay.com/api/'
, 'Form post url', 'Form post url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (455, 'liqPayPaymentGateway',
'LP_PAYWAY_URL',
'card'
, 'Payment method - card, liqpay', 'Payment method - card, liqpay');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (456, 'liqPayPaymentGateway', 'name', 'LiqPay', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (457, 'liqPayPaymentGateway', 'name_en', 'LiqPay', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (458, 'liqPayPaymentGateway', 'name_ru', 'LiqPay', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');


