

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

