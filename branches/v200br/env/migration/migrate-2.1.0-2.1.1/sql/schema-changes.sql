--
-- YC-392 new theme using Bootstrap v3
--


-- testPaymentGateway
--
UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
    '<div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Name on card</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Card number</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Expiration date</label>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
          </select>
        </div>
    </div>
    <div class="col-xs-12 form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Card security code</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>'
WHERE PAYMENTGATEWAYPARAMETER_ID in (51,53);

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
    '<div class="form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Имя на карте</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Номер карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Срок действия</label>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
          </select>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Код безопасности</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>'
WHERE PAYMENTGATEWAYPARAMETER_ID = 55;

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
    '<div class="form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Ім''я на картці</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccHolderName" value="@CARDHOLDERNAME@" maxlength="128"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Номер карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccNumber" maxlength="16"/>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Термін дії</label>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
          </select>
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-xs-12 col-sm-3 col-md-2">Код безпеки</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
          <input type="text" class="form-control" name="ccSecCode" maxlength="3"/>
        </div>
    </div>'
WHERE PAYMENTGATEWAYPARAMETER_ID = 57;


-- cyberSourcePaymentGateway
--
UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID in (114,116);

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 118;

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 1199;

--authorizeNetAimPaymentGateway
--
UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Card type</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID in (124,126);

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карты</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 128;

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
'
    <div class="col-xs-12 form-group">
        <label class="col-xs-12 col-sm-3 col-md-2 control-label">Тип карти</label>
        <div class="col-xs-12 col-sm-9 col-md-6">
            <select name="ccType" class="form-control">
            <option value="Visa">Visa</option>
            <option value="MasterCard">MasterCard</option>
            <option value="American Express">American Express</option>
            <option value="Discover">Discover</option>
            <option value="JCB">JCB</option>
            <option value="Enroute">Enroute</option>
            <option value="Diners Club">Diners Club</option>
            <option value="Optima">Optima</option>
            <option value="Novus">Novus</option>
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 130;

-- payflowPaymentGateway
--
UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID in (157,159);

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 161;

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 163;

--payPalNvpPaymentGateway
--
UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID in (177,179);

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 181;

UPDATE TPAYMENTGATEWAYPARAMETER SET P_VALUE =
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
            <option value="2013">2013</option>
            <option value="2014">2014</option>
            <option value="2015">2015</option>
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
WHERE PAYMENTGATEWAYPARAMETER_ID = 183;
