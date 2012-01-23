


CREATE TABLE TGOOGLENOTIFICATION (
  GOOGLENOTIFICATION_ID bigint NOT NULL AUTO_INCREMENT,
  SERIAL_NUMBER varchar(64) DEFAULT NULL,
  NOTIFICATION LONGTEXT,
  CREATED_BY varchar(64) DEFAULT NULL,
  CREATED_TIMESTAMP datetime DEFAULT NULL,
  GUID varchar(36) DEFAULT NULL,
  UPDATED_BY varchar(64) DEFAULT NULL,
  UPDATED_TIMESTAMP datetime DEFAULT NULL,
  PRIMARY KEY (GOOGLENOTIFICATION_ID)
);

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (400, 'googleCheckoutPaymentGateway',
'GC_ENVIRONMENT',
'SANDBOX'
, 'Google checkout environment', 'Google checkout environment');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (401, 'googleCheckoutPaymentGateway',
'GC_MERCHANT_ID',
'951076354081708'
, 'Merchant id', 'Merchant id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (402, 'googleCheckoutPaymentGateway',
'GC_MERCHANT_KEY',
'1DbImipHlIEVsi0liSeA_A'
, 'Merchant Signature', 'Merchant Signature');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (403, 'googleCheckoutPaymentGateway',
'GC_POST_URL',
'https://sandbox.google.com/checkout/api/checkout/v2/checkout/Merchant/951076354081708'
, 'Post url', 'Post url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (404, 'googleCheckoutPaymentGateway',
'GC_SUBMIT_BTN',
'<input type="image" name="Google Checkout" alt="Fast checkout through Google"  src="http://sandbox.google.com/checkout/buttons/checkout.gif?merchant_id=951076354081708&w=180&h=46&style=white&variant=text&loc=en_US"  height="46" width="180">'
, 'Submit button', 'Submit button');




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (450, 'liqPayPaymentGateway',
'LP_MERCHANT_ID',
'i6840685592'
, 'Merchant Id', 'Merchant Id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (451, 'liqPayPaymentGateway',
'LP_MERCHANT_KEY',
'JMkUT6En0uglRAKCK7STlFA7HLk1g6Xk75Wpdf9ogarpET'
, 'Merchant signature', 'Merchant signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (452, 'liqPayPaymentGateway',
'LP_RESULT_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/liqpayreturn'
, 'Page URL to show payment result', 'Page URL to show payment result');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (453, 'liqPayPaymentGateway',
'LP_SERVER_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/liqpaycallback'
, 'Call back URL with payment result.', 'Call back URL with payment result. ');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_POST_URL',
'https://www.liqpay.com/?do=clickNbuy'
, 'Form post url', 'Form post url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_PAYWAY_URL',
'card'
, 'Payment method - card, liqpay', 'Payment method - card, liqpay');



