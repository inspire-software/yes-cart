
-- TODO: Add more parameters that are required for PaySera Checkout, use 201xx range for IDs for smooth upgrades
-- TODO: below are basic fields from https://developers.paysera.com/en/checkout/integrations/integration-specification
-- TODO: that are required + few extras to support YC core functions

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20101, 'paySeraCheckoutPaymentGateway', 'name', 'PaySera Checkout', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20102, 'paySeraCheckoutPaymentGateway', 'name_en', 'PaySera Checkout', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20103, 'paySeraCheckoutPaymentGateway', 'name_ru', 'PaySera Checkout', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20104, 'paySeraCheckoutPaymentGateway', 'name_uk', 'PaySera Checkout', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20105, 'paySeraCheckoutPaymentGateway', 'name_de', 'PaySera Checkout', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION, SECURE_ATTRIBUTE, BUSINESSTYPE)
VALUES (20150, 'paySeraCheckoutPaymentGateway',
'PSC_PROJECTID',
'!!!PROVIDE VALUE!!!'
, 'Unique project number', 'Unique project number. Only activated projects can accept payments.', 1, 'SecureString');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION, SECURE_ATTRIBUTE, BUSINESSTYPE)
VALUES (20151, 'paySeraCheckoutPaymentGateway',
'PSC_SIGN_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Project password', 'Your project password', 1, 'SecureString');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20152, 'paySeraCheckoutPaymentGateway',
'PSC_API_VERSION',
'1.6'
, 'API version', 'The version number of Paysera system specification (API)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20153, 'paySeraCheckoutPaymentGateway',
'PSC_ACCEPTURL',
'http://@domain@/paymentresult?hint=ok'
, 'Return URL', 'The URL to which Paysera redirects buyers'' browser after they complete their payments. ');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20154, 'paySeraCheckoutPaymentGateway',
'PSC_CANCELURL',
'http://@domain@/paymentresult?hint=cancel'
, 'Cancel URL', 'A URL to which Paysera redirects the buyers'' browsers if they cancel checkout before completing their payments');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20155, 'paySeraCheckoutPaymentGateway',
'PSC_CALLBACKURL',
'http://@domain@/paymentpayseracheckout'
, 'Api callback URL', 'The URL to which Paysera posts information about the payment, in the form of Instant Payment Notification messages');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20156, 'paySeraCheckoutPaymentGateway',
'PSC_POSTURL',
'https://www.paysera.com/pay/'
, 'Paysera URL to perform payment', 'Paysera url to perform payment
live - https://www.paysera.com/pay/
sandbox - https://www.paysera.com/pay/');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20157, 'paySeraCheckoutPaymentGateway',
'PSC_BUYBUTTON',
'<input type="image" style="width:100px; border: 1px solid #000; border-radius:5px; padding: 3px" name="submit" alt="buy now with Paysera"  src="https://www.paysera.com/v2/compiled/logo.e2cba9ada3d3aecb551dcbcd63dce38b.svg">'
, 'Paysera submit button', 'Paysera submit button');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20158, 'paySeraCheckoutPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20159, 'paySeraCheckoutPaymentGateway', 'PSC_ENVIRONMENT', 'sandbox', 'Environment mode', 'Environment mode used by callback verification. Values can be: sandbox or live');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20160, 'paySeraCheckoutPaymentGateway', 'LANGUAGE_MAP', 'en=GB,de=DE,ru=RU,uk=RU', 'Language Mapping',
  'Language mapping can be used to map internal locale to PG supported locale');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20162, 'paySeraCheckoutPaymentGateway', 'restrictToCustomerTags', null, 'Gateway restrictions (Customer tags)', 'Gateway restrictions (Customer tags)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (20150, 'paySeraCheckoutPaymentGateway',
'PSC_MESSAGE_TEMPLATE_en',
'Payment made for ordered goods in [site_name] store (order number - [order_nr]): [items]'
, 'Message template EN', 'Order note for payment. [site_name], [order_nr], and [items] placeholders are available');
