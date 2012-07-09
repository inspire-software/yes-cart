INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (50, 'testPaymentGateway', 'htmlForm',
'<table>
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
</table>'
, 'Part of html form', 'Part of html form, that display when user select this gateway to pay');