
INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14101, 'payflowPaymentGateway', 'name', 'PayPal Payflow', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14102, 'payflowPaymentGateway',
'htmlForm',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Name on card</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Expiration date</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
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
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card security code</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Part of html form (default)', 'Part of html form, that display when user select this gateway to pay (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14103, 'payflowPaymentGateway', 'name_en', 'PayPal Payflow', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14104, 'payflowPaymentGateway',
'htmlForm_en',
'
<div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Name on card</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Expiration date</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
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
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card security code</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Part of html form (EN)', 'Part of html form, that display when user select this gateway to pay (EN)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14105, 'payflowPaymentGateway', 'name_ru', 'PayPal Payflow', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14106, 'payflowPaymentGateway',
'htmlForm_ru',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Имя на карте</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Срок действия</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
            <option value="01">01 - Январь</option>
            <option value="02">02 - Февраль</option>
            <option value="03">03 - Март</option>
            <option value="04">04 - Апрель</option>
            <option value="05">05 - Май</option>
            <option value="06">06 - Июнь</option>
            <option value="07">07 - Июль</option>
            <option value="08">08 - Август</option>
            <option value="09">09 - Сентябрь</option>
            <option value="10">10 - Октябрь</option>
            <option value="11">11 - Ноябрь</option>
            <option value="12">12 - Декабрь</option>
          </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Код безопасности</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Часть HTML формы для оплаты (RU)', 'Часть HTML формы для оплаты, которая будет показана на последнем шаге при оформлении заказа (RU)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14107, 'payflowPaymentGateway', 'name_uk', 'PayPal Payflow', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14108, 'payflowPaymentGateway',
'htmlForm_uk',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Ім''я на картці</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Термін дії</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
            <option value="01">01 - Січень</option>
            <option value="02">02 - Лютий</option>
            <option value="03">03 - Березень</option>
            <option value="04">04 - Квітень</option>
            <option value="05">05 - Травень</option>
            <option value="06">06 - Червень</option>
            <option value="07">07 - Липень</option>
            <option value="08">08 - Серпень</option>
            <option value="09">09 - Вересень</option>
            <option value="10">10 - Жовтень</option>
            <option value="11">11 - Листопад</option>
            <option value="12">12 - Грудень</option>
          </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Код безпеки</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Частина HTML форми для оплати (UK)', 'Частина HTML форми для оплати, яка буде показана на останньому кроці при оформленні замовлення (UK)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14109, 'payflowPaymentGateway', 'name_de', 'PayPal Payflow', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14110, 'payflowPaymentGateway',
'htmlForm_de',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Name des Kreditkarten Inhabers</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">PayCard</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Kartennummer</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Verfallsdatum</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
                <option value="01">01 - Januar</option>
                <option value="02">02 - Februar</option>
                <option value="03">03 - März</option>
                <option value="04">04 - April</option>
                <option value="05">05 - Kann</option>
                <option value="06">06 - Juni</option>
                <option value="07">07 - Juli</option>
                <option value="08">08 - August</option>
                <option value="09">09 - September</option>
                <option value="10">10 - Oktober</option>
                <option value="11">11 - November</option>
                <option value="12">12 - Dezember</option>
          </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Karten-Sicherheitscode</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Ein Teil der HTML-Formular (DE)', 'Ein Teil der HTML-Formular, das Display, wenn der Benutzer wählen Sie diese Tor zu bezahlen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14150, 'payflowPaymentGateway',
'HOST',
'pilot-payflowpro.paypal.com'
, 'Payment gateway host', 'Payment gateway host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14151, 'payflowPaymentGateway',
'PORT',
'443'
, 'Payment gateway port', 'Payment gateway port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14152, 'payflowPaymentGateway',
'TIMEOUT',
'55'
, 'Call timeout in seconds', 'Call timeout in seconds');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14153, 'payflowPaymentGateway',
'LOG_FILENAME',
'/yescart/log/payflow.log'
, 'Log filename', 'Log filename');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14154, 'payflowPaymentGateway',
'LOG_LEVEL',
'SEVERITY_DEBUG'
, 'Log level', 'Allowed values: SEVERITY_FATAL, SEVERITY_ERROR, SEVERITY_WARN, SEVERITY_INFO, SEVERITY_DEBUG');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14155, 'payflowPaymentGateway',
'LOG_SIZESIZE',
'1000000'
, 'Log size in bytes', 'Log size in bytes');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14156, 'payflowPaymentGateway',
'LOG_ENABLED',
'true'
, 'Is Log enabled', 'Is Log enabled');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14157, 'payflowPaymentGateway',
'PROXY_HOST',
''
, 'Proxy host', 'Proxy host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14158, 'payflowPaymentGateway',
'PROXY_PORT',
''
, 'Proxy port', 'Proxy port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14159, 'payflowPaymentGateway',
'PROXY_USER',
''
, 'Proxy user', 'Proxy user');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14160, 'payflowPaymentGateway',
'PROXY_PASSWORD',
''
, 'Proxy password', 'Proxy password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14161, 'payflowPaymentGateway',
'PROXY_ENABLED',
'false'
, 'Is proxy enabled', 'Is proxy enabled');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14162, 'payflowPaymentGateway',
'USER_NAME',
'!!!PROVIDE VALUE!!!'
, 'Payment gateway user name', 'Payment gateway user name (Merchant id ?)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14163, 'payflowPaymentGateway',
'USER_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Payment gateway user password', 'Payment gateway user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14164, 'payflowPaymentGateway',
'VENDOR',
'!!!PROVIDE VALUE!!!'
, 'Vendor', 'Vendor');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14165, 'payflowPaymentGateway',
'PARTNER',
'PayPal'
, 'Partner', 'Partner');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14166, 'payflowPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');




INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14201, 'payPalNvpPaymentGateway', 'name', 'PayPal NVP', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14202, 'payPalNvpPaymentGateway',
'htmlForm',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Expiration date</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
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
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card security code</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Part of html form (default)', 'Part of html form, that display when user select this gateway to pay (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14203, 'payPalNvpPaymentGateway', 'name_en', 'PayPal NVP', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14204, 'payPalNvpPaymentGateway',
'htmlForm_en',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Expiration date</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
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
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card security code</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Part of html form (EN)', 'Part of html form, that display when user select this gateway to pay (EN)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14205, 'payPalNvpPaymentGateway', 'name_ru', 'PayPal NVP',  'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14206, 'payPalNvpPaymentGateway',
'htmlForm_ru',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Срок действия</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
            <option value="01">01 - Январь</option>
            <option value="02">02 - Февраль</option>
            <option value="03">03 - Март</option>
            <option value="04">04 - Апрель</option>
            <option value="05">05 - Май</option>
            <option value="06">06 - Июнь</option>
            <option value="07">07 - Июль</option>
            <option value="08">08 - Август</option>
            <option value="09">09 - Сентябрь</option>
            <option value="10">10 - Октябрь</option>
            <option value="11">11 - Ноябрь</option>
            <option value="12">12 - Декабрь</option>
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Код безопасности</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Часть HTML формы для оплаты (RU)', 'Часть HTML формы для оплаты, которая будет показана на последнем шаге при оформлении заказа (RU)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14207, 'payPalNvpPaymentGateway', 'name_uk', 'PayPal NVP',  'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14208, 'payPalNvpPaymentGateway',
'htmlForm_uk',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Термін дії</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
            <option value="01">01 - Січень</option>
            <option value="02">02 - Лютий</option>
            <option value="03">03 - Березень</option>
            <option value="04">04 - Квітень</option>
            <option value="05">05 - Травень</option>
            <option value="06">06 - Червень</option>
            <option value="07">07 - Липень</option>
            <option value="08">08 - Серпень</option>
            <option value="09">09 - Вересень</option>
            <option value="10">10 - Жовтень</option>
            <option value="11">11 - Листопад</option>
            <option value="12">12 - Грудень</option>
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Код безпеки</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Частина HTML форми для оплати (UK)', 'Частина HTML форми для оплати, яка буде показана на останньому кроці при оформленні замовлення (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14209, 'payPalNvpPaymentGateway', 'name_de', 'PayPal NVP',  'Gateway-Namen (DE)', 'Gateway-Namen (DE)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14210, 'payPalNvpPaymentGateway',
'htmlForm_de',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">PayCard</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Kartennummer</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Verfallsdatum</label>
        <div class="col-xs-8 col-sm-7 col-md-4">
            <select name="ccExpireMonth" class="form-control">
                <option value="01">01 - Januar</option>
                <option value="02">02 - Februar</option>
                <option value="03">03 - März</option>
                <option value="04">04 - April</option>
                <option value="05">05 - Kann</option>
                <option value="06">06 - Juni</option>
                <option value="07">07 - Juli</option>
                <option value="08">08 - August</option>
                <option value="09">09 - September</option>
                <option value="10">10 - Oktober</option>
                <option value="11">11 - November</option>
                <option value="12">12 - Dezember</option>
            </select>
        </div>
        <div class="col-xs-4 col-sm-2 col-md-2 no-padding">
            <select name="ccExpireYear" class="form-control">
            <option value="2015">2015</option>
            <option value="2016">2016</option>
            <option value="2017">2017</option>
            <option value="2018">2018</option>
            <option value="2019">2019</option>
            <option value="2020">2020</option>
            <option value="2021">2021</option>
            <option value="2022">2022</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Karten-Sicherheitscode</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>
'
, 'Ein Teil der HTML-Formular (DE)', 'Ein Teil der HTML-Formular, das Display, wenn der Benutzer wählen Sie diese Tor zu bezahlen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14250, 'payPalNvpPaymentGateway',
'API_USER_NAME',
'!!!PROVIDE VALUE!!!'
, 'Api user name', 'Api user name');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14251, 'payPalNvpPaymentGateway',
'API_USER_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14252, 'payPalNvpPaymentGateway',
'SIGNATURE',
'!!!PROVIDE VALUE!!!'
, 'Signature', 'Signature');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14253, 'payPalNvpPaymentGateway',
'ENVIRONMENT',
'sandbox'
, 'Environment', 'Environment allowed live or sandbox');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14254, 'payPalNvpPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14301, 'payPalExpressPaymentGateway', 'name', 'PayPal Express', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14302, 'payPalExpressPaymentGateway', 'name_en', 'PayPal Express', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14303, 'payPalExpressPaymentGateway', 'name_ru', 'PayPal Express', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14304, 'payPalExpressPaymentGateway', 'name_uk', 'PayPal Express', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14305, 'payPalExpressPaymentGateway', 'name_de', 'PayPal Express', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14350, 'payPalExpressPaymentGateway',
'API_USER_NAME',
'!!!PROVIDE VALUE!!!'
, 'Api user name', 'Api user name');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14351, 'payPalExpressPaymentGateway',
'API_USER_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14352, 'payPalExpressPaymentGateway',
'SIGNATURE',
'!!!PROVIDE VALUE!!!'
, 'Signature', 'Signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14353, 'payPalExpressPaymentGateway',
'RETURNURL',
'http://@domain@/yes-shop/paypallreturn'
, 'Return url', 'Return url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14354, 'payPalExpressPaymentGateway',
'CANCELURL',
'http://@domain@/yes-shop'
, 'Cancel url', 'Cancel url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14355, 'payPalExpressPaymentGateway',
'PP_EC_API_URL',
'https://api-3t.sandbox.paypal.com/nvp'
, 'Api call url', 'Cancel url');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14356, 'payPalExpressPaymentGateway',
'PP_EC_PAYPAL_URL',
'https://www.sandbox.paypal.com/cgi-bin/webscr'
, 'Paypal url to perform payment', 'Paypal url to perform payment');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14357, 'payPalExpressPaymentGateway',
'PP_SUBMIT_BTN',
'<input type="image" name="Paypal checkout" alt="Fast checkout with paypal"  src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">'
, 'Paypal submit button', 'Paypal submit button');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14358, 'payPalExpressPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

