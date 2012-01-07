
INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (100, 'cyberSourcePaymentGateway',
'merchantID',
'iazarny'
, 'Merchand Id', 'Merchand Id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (101, 'cyberSourcePaymentGateway',
'keysDirectory',
'/npa/resources/paymentgateway/cskeys'
, 'Directory with keys', 'Directory with keys');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (102, 'cyberSourcePaymentGateway',
'targetAPIVersion',
'1.28'
, 'Cyber source API Version', 'Cyber source API Version');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (103, 'cyberSourcePaymentGateway',
'sendToProduction',
'false'
, 'Send to production', 'Send to production, false value for test');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (104, 'cyberSourcePaymentGateway',
'useHttpClient',
'true'
, 'Use apache http client for communication', 'Use apache http client for communication');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (105, 'cyberSourcePaymentGateway',
'enableLog',
'true'
, 'Enable log', 'Enable log');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (106, 'cyberSourcePaymentGateway',
'logDirectory',
'/npa/log'
, 'Log directory', 'Log directory');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (107, 'cyberSourcePaymentGateway',
'logMaximumSize',
'10'
, 'Max size of log file', 'Max size of log file. Value in MB');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (109, 'cyberSourcePaymentGateway',
'proxyHost',
'192.168.1.1'
, 'Proxy host', 'Proxy host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (110, 'cyberSourcePaymentGateway',
'proxyPort',
'3128'
, 'Proxy port', 'Proxy port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (111, 'cyberSourcePaymentGateway',
'proxyUser',
'proxyUserName'
, 'Proxy user', 'Proxy user');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (112, 'cyberSourcePaymentGateway',
'proxyPassword',
'password'
, 'Proxy password', 'Proxy password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (113, 'cyberSourcePaymentGateway',
'htmlForm',
'
<table>
    <tr>
        <td>Card type</td>
        <td><select name="ccType" class="paymentlongfield">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="Eurocard">Eurocard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Carte Blanche">Carte Blanche</option>
            <option value="JCB">JCB</option>
            <option value="EnRoute">EnRoute</option>
            <option value="Maestro (UK Domestic), Solo">Maestro (UK Domestic), Solo</option>
            <option value="Delta">Delta</option>
            <option value="Visa Electron">Visa Electron</option>
            <option value="Dankort">Dankort</option>
            <option value="JAL">JAL</option>
            <option value="Laser">Laser</option>
            <option value="Carte Bleue">Carte Bleue</option>
            <option value="Carta Si">Carta Si</option>
            <option value="UATP">UATP</option>
        </select></td>
    </tr>
    <tr>
        <td>Card number</td>
        <td><input type="text" class="paymentlongfield" name="ccNumber"  maxlength="16"/></td>
    </tr>
    <tr>
        <td>Expiration date</td>
        <td><select name="ccExpireMonth" class="paymentnormalfield">
            <option value="01">01 - January</option>
            <option value="02">02 - February</option>
            <option value="03">03 - March</option>
            <option value="04">04 - April</option>
            <option value="05">05 - May</option>
            <option value="06">06 - June</option>
            <option value="07">07 - July</option>
            <option value="08">08 - August</option>
            <option value="09">09 - September</option>
            <option value="10">10 - October</option>
            <option value="11">11 - November</option>
            <option value="12">12 - December</option>
        </select> <select name="ccExpireYear" class="paymentshortfield">
            <option value="2011">2011</option>
            <option value="2012">2012</option>
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>
'
, 'Part of html form', 'Part of html form, that display when user select this gateway to pay');






INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (120, 'authorizeNetAimPaymentGateway',
'MERCHANT_ENVIRONMENT',
'SANDBOX'
, 'Environment name.', 'Merchant environment name.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (121, 'authorizeNetAimPaymentGateway',
'API_LOGIN_ID',
'6uY7T8YfwR6'
, 'Merchant login.', 'Merchant login.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (122, 'authorizeNetAimPaymentGateway',
'TRANSACTION_KEY',
'4765Udc8t48qBNSa'
, 'Transaction key', 'Transaction key');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (127, 'authorizeNetAimPaymentGateway',
'htmlForm',
'
<table>
    <tr>
        <td>Card type</td>
        <td><select name="ccType" class="paymentlongfield">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
        </select></td>
    </tr>
    <tr>
        <td>Card number</td>
        <td><input type="text" class="paymentlongfield" name="ccNumber"  maxlength="16"/></td>
    </tr>
    <tr>
        <td>Expiration date</td>
        <td><select name="ccExpireMonth" class="paymentnormalfield">
            <option value="01">01 - January</option>
            <option value="02">02 - February</option>
            <option value="03">03 - March</option>
            <option value="04">04 - April</option>
            <option value="05">05 - May</option>
            <option value="06">06 - June</option>
            <option value="07">07 - July</option>
            <option value="08">08 - August</option>
            <option value="09">09 - September</option>
            <option value="10">10 - October</option>
            <option value="11">11 - November</option>
            <option value="12">12 - December</option>
        </select> <select name="ccExpireYear" class="paymentshortfield">
            <option value="2011">2011</option>
            <option value="2012">2012</option>
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>
'
, 'Part of html form', 'Part of html form, that display when user select this gateway to pay');




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (140, 'payflowPaymentGateway',
'HOST',
'pilot-payflowpro.paypal.com'
, 'Payment gateway host', 'Payment gateway host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (141, 'payflowPaymentGateway',
'PORT',
'443'
, 'Payment gateway port', 'Payment gateway port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (142, 'payflowPaymentGateway',
'TIMEOUT',
'55'
, 'Call timeout in seconds', 'Call timeout in seconds');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (143, 'payflowPaymentGateway',
'LOG_FILENAME',
'/npa/log/payflow.log'
, 'Log filename', 'Log filename');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (144, 'payflowPaymentGateway',
'LOG_LEVEL',
'SEVERITY_DEBUG'
, 'Log level', 'Allowed values: SEVERITY_FATAL, SEVERITY_ERROR, SEVERITY_WARN, SEVERITY_INFO, SEVERITY_DEBUG');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (145, 'payflowPaymentGateway',
'LOG_SIZESIZE',
'1000000'
, 'Log size in bytes', 'Log size in bytes');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (146, 'payflowPaymentGateway',
'LOG_ENABLED',
'true'
, 'Is Log enabled', 'Is Log enabled');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (147, 'payflowPaymentGateway',
'PROXY_HOST',
'192.168.1.1'
, 'Proxy host', 'Proxy host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (148, 'payflowPaymentGateway',
'PROXY_PORT',
'3128'
, 'Proxy port', 'Proxy port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (149, 'payflowPaymentGateway',
'PROXY_USER',
'proxyuser'
, 'Proxy user', 'Proxy user');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (150, 'payflowPaymentGateway',
'PROXY_PASSWORD',
'password'
, 'Proxy password', 'Proxy password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (151, 'payflowPaymentGateway',
'PROXY_ENABLED',
'false'
, 'Is proxy enabled', 'Is proxy enabled');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (152, 'payflowPaymentGateway',
'USER_NAME',
'aaz911'
, 'Payment gateway user name', 'Payment gateway user name (Merchant id ?)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (153, 'payflowPaymentGateway',
'USER_PASSWORD',
'sharpevil77'
, 'Payment gateway user password', 'Payment gateway user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (154, 'payflowPaymentGateway',
'VENDOR',
'aaz911'
, 'Vendor', 'Vendor');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (155, 'payflowPaymentGateway',
'PARTNER',
'PayPal'
, 'Partner', 'Partner');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (156, 'payflowPaymentGateway',
'htmlForm',
'
<table>
    <tr>
        <td>Name on card</td>
        <td><input type="text" class="paymentlongfield" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
    </tr>
    <tr>
        <td>Card type</td>
        <td><select name="ccType" class="paymentlongfield">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
        </select></td>
    </tr>
    <tr>
        <td>Card number</td>
        <td><input type="text" class="paymentlongfield" name="ccNumber"  maxlength="16"/></td>
    </tr>
    <tr>
        <td>Expiration date</td>
        <td><select name="ccExpireMonth" class="paymentnormalfield">
            <option value="01">01 - January</option>
            <option value="02">02 - February</option>
            <option value="03">03 - March</option>
            <option value="04">04 - April</option>
            <option value="05">05 - May</option>
            <option value="06">06 - June</option>
            <option value="07">07 - July</option>
            <option value="08">08 - August</option>
            <option value="09">09 - September</option>
            <option value="10">10 - October</option>
            <option value="11">11 - November</option>
            <option value="12">12 - December</option>
        </select> <select name="ccExpireYear" class="paymentshortfield">
            <option value="2011">2011</option>
            <option value="2012">2012</option>
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>
'
, 'Part of html form', 'Part of html form, that display when user select this gateway to pay');









INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (170, 'payPalNvpPaymentGateway',
'API_USER_NAME',
'azarny_1324325086_biz_api1.gmail.com'
, 'Api user name', 'Api user name');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (171, 'payPalNvpPaymentGateway',
'API_USER_PASSWORD',
'1324325124'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (172, 'payPalNvpPaymentGateway',
'SIGNATURE',
'AlH.6ZOY.CsakUGCdfdTg4JxaG4tA71FLEoZ5abQPArA5wL.poNPPQcB'
, 'Signature', 'Signature');

--INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
--VALUES (173, 'payPalNvpPaymentGateway',
--'KEY_PATH',
--'/npa/resources/paymentgateway/paypal/some.p12'
--, 'Path to key', 'Path to key');


--INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
--VALUES (174, 'payPalNvpPaymentGateway',
--'KEY_PASSWORD',
--'passwd for key'
--, 'Key password', 'Key password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (175, 'payPalNvpPaymentGateway',
'ENVIRONMENT',
'sandbox'
, 'Environment', 'Environment allowed live or sandbox');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (176, 'payPalNvpPaymentGateway',
'htmlForm',
'
<table>
    <tr>
        <td>Card type</td>
        <td><select name="ccType" class="paymentlongfield">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
        </select></td>
    </tr>
    <tr>
        <td>Card number</td>
        <td><input type="text" class="paymentlongfield" name="ccNumber"  maxlength="16"/></td>
    </tr>
    <tr>
        <td>Expiration date</td>
        <td><select name="ccExpireMonth" class="paymentnormalfield">
            <option value="01">01 - January</option>
            <option value="02">02 - February</option>
            <option value="03">03 - March</option>
            <option value="04">04 - April</option>
            <option value="05">05 - May</option>
            <option value="06">06 - June</option>
            <option value="07">07 - July</option>
            <option value="08">08 - August</option>
            <option value="09">09 - September</option>
            <option value="10">10 - October</option>
            <option value="11">11 - November</option>
            <option value="12">12 - December</option>
        </select> <select name="ccExpireYear" class="paymentshortfield">
            <option value="2011">2011</option>
            <option value="2012">2012</option>
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>
'
, 'Part of html form', 'Part of html form, that display when user select this gateway to pay');




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (200, 'authorizeNetSimPaymentGateway',
'POST_URL',
'https://test.authorize.net/gateway/transact.dll'
, 'Url to post form.', 'Test - https://test.authorize.net/gateway/transact.dll  production - https://secure.authorize.net/gateway/transact.dll');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (201, 'authorizeNetSimPaymentGateway',
'API_LOGIN_ID',
'6uY7T8YfwR6'
, 'Merchant login.', 'Merchant login.');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (202, 'authorizeNetSimPaymentGateway',
'TRANSACTION_KEY',
'4765Udc8t48qBNSa'
, 'Transaction key', 'Transaction key');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (203, 'authorizeNetSimPaymentGateway',
'MD5_HASH_KEY',
'bender'
, 'MD5 hash key', 'MD5 hash key. SIM only');

--INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
--VALUES (204, 'authorizeNetSimPaymentGateway',
--'MERCHANT_HOST',
--'----'
--, 'Host', 'Host. SIM only');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (205, 'authorizeNetSimPaymentGateway',
'RELAY_RESPONCE_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/responce/page'
, 'Releay responce url', 'Releay responce url. SIM only');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (206, 'authorizeNetSimPaymentGateway',
'ORDER_RECEIPT_URL',
'http://testdevshop.yes-cart.org:8080/yes-shop/receipt/page'
, 'SIM/DPM order receipt url', 'SIM/DPM order receipt url. SIM only');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (207, 'authorizeNetSimPaymentGateway',
'TEST_REQUEST',
'FALSE'
, 'SIM test request flag', 'SIM test request flag');







INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (220, 'payPalExpressPaymentGateway',
'API_USER_NAME',
'yescas_1324326469_biz_api1.gmail.com'
, 'Api user name', 'Api user name');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (221, 'payPalExpressPaymentGateway',
'API_USER_PASSWORD',
'1324326494'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (222, 'payPalExpressPaymentGateway',
'SIGNATURE',
'AarMlaEPlNPHEsSS-tLgoTRafwgYAkJWGgDrLpn-Wp90NhGz102UYPy2'
, 'Signature', 'Signature');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (223, 'payPalExpressPaymentGateway',
'RETURNURL',
'http://testdevshop.yes-cart.org:8080/yes-shop/paypallreturn'
, 'Return url', 'Return url');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (224, 'payPalExpressPaymentGateway',
'CANCELURL',
'http://testdevshop.yes-cart.org:8080/yes-shop'
, 'Cancel url', 'Cancel url');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (225, 'payPalExpressPaymentGateway',
'PP_EC_API_URL',
'https://api-3t.sandbox.paypal.com/nvp'
, 'Api call url', 'Cancel url');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (226, 'payPalExpressPaymentGateway',
'PP_EC_PAYPAL_URL',
'https://www.sandbox.paypal.com/cgi-bin/webscr'
, 'Paypal url to perform payment', 'Paypal url to perform payment');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (227, 'payPalExpressPaymentGateway',
'PP_SUBMIT_BTN',
'<input type="image" name="Paypal checkout" alt="Fast checkout with paypal"  src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">'
, 'Paypal submit button', 'Paypal submit button');

