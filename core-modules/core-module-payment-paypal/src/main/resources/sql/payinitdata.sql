

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14201, 'payPalProPaymentGateway', 'name', 'PayPal Pro', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14202, 'payPalProPaymentGateway',
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
VALUES (14203, 'payPalProPaymentGateway', 'name_en', 'PayPal Pro', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14204, 'payPalProPaymentGateway',
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
VALUES (14205, 'payPalProPaymentGateway', 'name_ru', 'PayPal Pro',  'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14206, 'payPalProPaymentGateway',
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
VALUES (14207, 'payPalProPaymentGateway', 'name_uk', 'PayPal Pro',  'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14208, 'payPalProPaymentGateway',
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
VALUES (14209, 'payPalProPaymentGateway', 'name_de', 'PayPal Pro',  'Gateway-Namen (DE)', 'Gateway-Namen (DE)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14210, 'payPalProPaymentGateway',
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
VALUES (14250, 'payPalProPaymentGateway',
'API_USER_NAME',
'!!!PROVIDE VALUE!!!'
, 'Api user name', 'Api user name');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14251, 'payPalProPaymentGateway',
'API_USER_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14252, 'payPalProPaymentGateway',
'SIGNATURE',
'!!!PROVIDE VALUE!!!'
, 'Signature', 'Signature');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14253, 'payPalProPaymentGateway',
'ENVIRONMENT',
'sandbox'
, 'Environment', 'Environment allowed live or sandbox');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14254, 'payPalProPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14255, 'payPalProPaymentGateway',
'PP_EC_API_URL',
'https://api-3t.sandbox.paypal.com/nvp'
, 'Api call url', 'Api call url
live - https://api-3t.paypal.com/nvp
sandbox - https://api-3t.sandbox.paypal.com/nvp');






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
'https://@domain@/yes-shop/paymentresult?hint=ok'
, 'Return url', 'Return url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14354, 'payPalExpressPaymentGateway',
'CANCELURL',
'https://@domain@/yes-shop/paymentresult?hint=cancel'
, 'Cancel url', 'Cancel url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14355, 'payPalExpressPaymentGateway',
'PP_EC_API_URL',
'https://api-3t.sandbox.paypal.com/nvp'
, 'Api call url', 'Api call url
live - https://api-3t.paypal.com/nvp
sandbox - https://api-3t.sandbox.paypal.com/nvp');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14356, 'payPalExpressPaymentGateway',
'PP_EC_PAYPAL_URL',
'https://www.sandbox.paypal.com/cgi-bin/webscr'
, 'Paypal url to perform payment', 'Paypal url to perform payment
live - https://www.paypal.com/cgi-bin/webscr
sandbox - https://www.sandbox.paypal.com/cgi-bin/webscr');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14357, 'payPalExpressPaymentGateway',
'PP_SUBMIT_BTN',
'<input type="image" name="Paypal checkout" alt="Fast checkout with PayPal"  src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout2.gif">'
, 'Paypal submit button', 'Paypal submit button');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14358, 'payPalExpressPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14359, 'payPalExpressPaymentGateway',
'CALLBACK',
'https://@domain@/yes-shop/paymentpaypalexpress'
, 'Callback url', 'Callback url');






INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14501, 'payPalButtonPaymentGateway', 'name', 'PayPal Button', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14502, 'payPalButtonPaymentGateway', 'name_en', 'PayPal Button', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14503, 'payPalButtonPaymentGateway', 'name_ru', 'PayPal Button', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14504, 'payPalButtonPaymentGateway', 'name_uk', 'PayPal Button', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14505, 'payPalButtonPaymentGateway', 'name_de', 'PayPal Button', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14550, 'payPalButtonPaymentGateway',
'PPB_USER',
'!!!PROVIDE VALUE!!!'
, 'Api user name', 'Your PayPal ID or an email address associated with your PayPal account. Email addresses must be confirmed');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14551, 'payPalButtonPaymentGateway',
'PPB_PASSWORD',
'!!!PROVIDE VALUE!!!'
, 'Api user password', 'Api user password');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14552, 'payPalButtonPaymentGateway',
'PPB_SIGNATURE',
'!!!PROVIDE VALUE!!!'
, 'Signature', 'Signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14553, 'payPalButtonPaymentGateway',
'PPB_RETURNURL',
'http://@domain@/yes-shop/paymentresult?hint=ok'
, 'Return URL', 'The URL to which PayPal redirects buyers'' browser after they complete their payments. ');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14554, 'payPalButtonPaymentGateway',
'CANCELURL',
'http://@domain@/yes-shop/paymentresult?hint=cancel'
, 'Cancel URL', 'A URL to which PayPal redirects the buyers'' browsers if they cancel checkout before completing their payments');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14555, 'payPalButtonPaymentGateway',
'PPB_NOTIFYURL',
'http://@domain@/yes-shop/paymentpaypalbutton'
, 'Api callback URL', 'The URL to which PayPal posts information about the payment, in the form of Instant Payment Notification messages');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14556, 'payPalButtonPaymentGateway',
'PPB_POST_URL',
'https://www.sandbox.paypal.com/cgi-bin/webscr'
, 'Paypal URL to perform payment', 'Paypal url to perform payment
live - https://www.paypal.com/cgi-bin/webscr
sandbox - https://www.sandbox.paypal.com/cgi-bin/webscr');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14557, 'payPalButtonPaymentGateway',
'PPB_BUYBUTTON',
'<input type="image" name="submit" alt="buy now with PayPal"  src="https://www.paypal.com/en_US/i/btn/btn_buynow_LG.gif">'
, 'PayPal submit button', 'PayPal submit button (Use _[locale] for different languages)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14558, 'payPalButtonPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14559, 'payPalButtonPaymentGateway', 'PPB_ENVIRONMENT', 'sandbox', 'Environment mode', 'Environment mode used by callback verification. Values can be: sandbox or live');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (14560, 'payPalButtonPaymentGateway', 'LANGUAGE_MAP', 'en=GB,de=DE,ru=RU,uk=RU', 'Language Mapping',
  'Language mapping can be used to map internal locale to PG supported locale
  See "HTML Variables for PayPal Payments Standard" lc parameter for more details');

