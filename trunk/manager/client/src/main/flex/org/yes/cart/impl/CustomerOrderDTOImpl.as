package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CustomerOrderDTOImpl")]
public class CustomerOrderDTOImpl {


    public var customerorderId:Number;

    public var ordernum:String;

    public var pgLabel:String;

    public var billingAddress:String;

    public var shippingAddress:String;

    public var cartGuid:String;

    public var currency:String;

    public var orderMessage:String;

    public var orderStatus:String;

    public var multipleShipmentOption:Boolean;

    public var orderTimestamp:Date;

    public var email:String;

    public var firstname:String;

    public var lastname:String;

    public var middlename:String;

    public var customerId:Number;

    public var shopId:Number;

    public var code:String;

    public var amount:Number;

    public function CustomerOrderDTOImpl() {
    }


    public function toString():String {
        return "CustomerOrderDTOImpl{customerorderId=" + String(customerorderId)
                + ",ordernum=" + String(ordernum) + ",pgLabel=" + String(pgLabel)
                + ",currency=" + String(currency) + ",firstname=" + String(firstname)
                + ",lastname=" + String(lastname) + "}";
    }
}
}