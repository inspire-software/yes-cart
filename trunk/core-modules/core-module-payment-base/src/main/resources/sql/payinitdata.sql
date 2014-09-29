INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (50, 'testPaymentGateway', 'name', 'Test Payment Gateway', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (51, 'testPaymentGateway', 'htmlForm',
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
, 'Part of html form (default)', 'Part of html form, that display when user select this gateway to pay');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (52, 'testPaymentGateway', 'name_en', 'Test Payment Gateway', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (53, 'testPaymentGateway', 'htmlForm_en',
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
, 'Part of html form (EN)', 'Part of html form, that display when user select this gateway to pay');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (54, 'testPaymentGateway', 'name_ru', 'Тестовый Платежный Шлюз', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (55, 'testPaymentGateway', 'htmlForm_ru',
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
, 'Часть HTML формы для оплаты (RU)', 'Часть HTML формы для оплаты, которая будет показана на последнем шаге при оформлении заказа (RU)');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (56, 'testPaymentGateway', 'name_uk', 'Тестовий Платіжний Шлюз', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (57, 'testPaymentGateway', 'htmlForm_uk',
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
, 'Частина HTML форми для оплати (UK)', 'Частина HTML форми для оплати, яка буде показана на останньому кроці при оформленні замовлення (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (58, 'testPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');



INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (60, 'courierPaymentGateway', 'name', 'Payment via courier', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (61, 'courierPaymentGateway', 'name_en', 'Payment via courier', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (62, 'courierPaymentGateway', 'name_ru', 'Оплата курьеру', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (63, 'courierPaymentGateway', 'name_uk', 'Оплата кур''єру', 'Назва платіжного шлюзу (UK)', 'Назва платіжного шлюзу (UK)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (64, 'courierPaymentGateway', 'priority', '100', 'Gateway priority', 'Gateway priority');
