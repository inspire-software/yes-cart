



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (450, 'liqPayPaymentGateway',
'LP_MERCHANT_ID',
'!!!ЗНАЧЕНИЕ!!!'
, 'id мерчанта', 'id мерчанта');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (451, 'liqPayPaymentGateway',
'LP_MERCHANT_KEY',
'!!!ЗНАЧЕНИЕ!!!'
, 'Ключ для подписи', 'Ключ для подписи');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (452, 'liqPayPaymentGateway',
'LP_RESULT_URL',
'http://@domain@/yes-shop/liqpayreturn'
, 'URL для показа результата платежа', 'URL для показа результата платежа');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (453, 'liqPayPaymentGateway',
'LP_SERVER_URL',
'http://@domain@/yes-shop/liqpaycallback'
, 'URL страница на которую прийдет ответ от сервера', 'URL страница на которую прийдет ответ от сервера');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (454, 'liqPayPaymentGateway',
'LP_POST_URL',
'https://www.liqpay.com/?do=clickNbuy'
, 'Post URL формы отправки платежа', 'Post URL формы отправки платежа');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (455, 'liqPayPaymentGateway',
'LP_PAYWAY_URL',
'card'
, 'способ оплаты карта,телефон,liqpay', 'способ оплаты карта,телефон,liqpay');

