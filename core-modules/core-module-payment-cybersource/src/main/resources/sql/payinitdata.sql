INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12100, 'cyberSourcePaymentGateway', 'name', 'CyberSource', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12101, 'cyberSourcePaymentGateway',
'htmlForm',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
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
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" name="ccNumber" class="form-control" maxlength="16"/>
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
VALUES (12102, 'cyberSourcePaymentGateway', 'name_en', 'CyberSource', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12103, 'cyberSourcePaymentGateway',
'htmlForm_en',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
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
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" name="ccNumber" class="form-control" maxlength="16"/>
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
VALUES (12104, 'cyberSourcePaymentGateway', 'name_ru', 'CyberSource', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12105, 'cyberSourcePaymentGateway',
'htmlForm_ru',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
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
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" name="ccNumber" class="form-control" maxlength="16"/>
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
VALUES (12106, 'cyberSourcePaymentGateway', 'name_uk', 'CyberSource', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12107, 'cyberSourcePaymentGateway',
'htmlForm_uk',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
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
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" name="ccNumber" class="form-control" maxlength="16"/>
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
VALUES (12108, 'cyberSourcePaymentGateway', 'name_de', 'CyberSource', 'Gateway-Namen (DE)', 'Gateway-Namen (DE)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12109, 'cyberSourcePaymentGateway',
'htmlForm_de',
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">PayCard</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
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
            </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Kartennummer</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" name="ccNumber" class="form-control" maxlength="16"/>
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
VALUES (12150, 'cyberSourcePaymentGateway',
'merchantID',
'!!!PROVIDE VALUE!!!'
, 'Merchand Id', 'Merchand Id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12151, 'cyberSourcePaymentGateway',
'keysDirectory',
'/yescart/resources/paymentgateway/cskeys'
, 'Absolute path to directory with keys', 'Absolute path to directory with keys');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12152, 'cyberSourcePaymentGateway',
'targetAPIVersion',
'1.28'
, 'Cyber source API Version', 'Cyber source API Version');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12153, 'cyberSourcePaymentGateway',
'sendToProduction',
'false'
, 'Send to production', 'Send to production, false value for test');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12154, 'cyberSourcePaymentGateway',
'useHttpClient',
'true'
, 'Use apache http client for communication', 'Use apache http client for communication');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12155, 'cyberSourcePaymentGateway',
'enableLog',
'true'
, 'Enable log', 'Enable log');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12156, 'cyberSourcePaymentGateway',
'logDirectory',
'/yescart/log'
, 'Absolute path to log directory', 'Absolute path to log directory');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12157, 'cyberSourcePaymentGateway',
'logMaximumSize',
'10'
, 'Max size of log file. MB', 'Max size of log file. Value in MB');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12158, 'cyberSourcePaymentGateway',
'proxyHost',
''
, 'Proxy host', 'Proxy host');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12159, 'cyberSourcePaymentGateway',
'proxyPort',
''
, 'Proxy port', 'Proxy port');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12160, 'cyberSourcePaymentGateway',
'proxyUser',
''
, 'Proxy user', 'Proxy user');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12161, 'cyberSourcePaymentGateway',
'proxyPassword',
''
, 'Proxy password', 'Proxy password');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (12162, 'cyberSourcePaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');


