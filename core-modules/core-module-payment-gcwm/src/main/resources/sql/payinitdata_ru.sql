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
'1234567890'
, 'id мерчанта', 'id мерчанта');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (451, 'liqPayPaymentGateway',
'LP_MERCHANT_KEY',
'1234567890'
, 'Ключ для подписи', 'Ключ для подписи');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (452, 'liqPayPaymentGateway',
'LP_RESULT_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/liqpayreturn'
, 'URL для показа результата платежа', 'URL для показа результата платежа');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (453, 'liqPayPaymentGateway',
'LP_SERVER_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/liqpaycallback'
, 'URL страница на которую прийдет ответ от сервера', 'URL страница на которую прийдет ответ от сервера');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_POST_URL',
'https://www.liqpay.com/?do=clickNbuy'
, 'Post URL формы отправки платежа', 'Post URL формы отправки платежа');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_PAYWAY_URL',
'card'
, 'способ которым оплатит покупатель с карты, с телефона card, liqpay', 'способ которым оплатит покупатель с карты, с телефона card, liqpay');

