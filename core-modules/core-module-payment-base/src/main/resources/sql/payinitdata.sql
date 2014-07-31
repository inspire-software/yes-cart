INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (50, 'testPaymentGateway', 'name', 'Test Payment Gateway', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (51, 'testPaymentGateway', 'htmlForm',
'<table>
    <tr>
        <td>Name on card</td>
        <td><input type="text" class="paymentlongfield" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>'
, 'Part of html form (default)', 'Part of html form, that display when user select this gateway to pay');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (52, 'testPaymentGateway', 'name_en', 'Test Payment Gateway', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (53, 'testPaymentGateway', 'htmlForm_en',
'<table>
    <tr>
        <td>Name on card</td>
        <td><input type="text" class="paymentlongfield" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Card security code</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>'
, 'Part of html form (EN)', 'Part of html form, that display when user select this gateway to pay');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (54, 'testPaymentGateway', 'name_ru', 'Тестовый Платежный Шлюз', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (55, 'testPaymentGateway', 'htmlForm_ru',
'<table>
    <tr>
        <td>Имя на карте</td>
        <td><input type="text" class="paymentlongfield" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
    </tr>
    <tr>
        <td>Номер карты</td>
        <td><input type="text" class="paymentlongfield" name="ccNumber"  maxlength="16"/></td>
    </tr>
    <tr>
        <td>Срок действия</td>
        <td><select name="ccExpireMonth" class="paymentnormalfield">
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
        </select> <select name="ccExpireYear" class="paymentshortfield">
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
        </select></td>
    </tr>
    <tr>
        <td>Код безопасности</td>
        <td><input type="text" class="paymentshortfield" name="ccSecCode" maxlength="3"/></td>
    </tr>
</table>'
, 'Часть HTML формы для оплаты (RU)', 'Часть HTML формы для оплаты, которая будет показана на последнем шаге при оформлении заказа (RU)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (60, 'courierPaymentGateway', 'name', 'Payment via courier', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (61, 'courierPaymentGateway', 'name_en', 'Payment via courier', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (62, 'courierPaymentGateway', 'name_ru', 'Оплата курьеру', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');
