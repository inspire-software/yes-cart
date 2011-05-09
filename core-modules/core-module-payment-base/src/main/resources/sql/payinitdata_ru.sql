INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (50, 'testPaymentGateway', 'htmlForm',
'<table>
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
            <option value="2011">2011</option>
            <option value="2012">2012</option>
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
, 'Часть HTML формы для оплаты', 'Часть HTML формы для оплаты, которая будет показана на последнем шаге при оформлении заказа');