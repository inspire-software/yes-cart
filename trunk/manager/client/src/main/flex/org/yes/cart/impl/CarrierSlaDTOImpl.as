package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CarrierSlaDTOImpl")]
public class CarrierSlaDTOImpl {


    public var carrierslaId:Number;

    public var name:String;

    public var description:String;

    public var currency:String;

    public var maxDays:Number;

    public var slaType:String;

    public var price:Number;

    public var percent:Number;

    public var script:String;

    public var priceNotLess:Number;

    public var percentNotLess:Number;

    public var costNotLess:Number;

    public var carrierId:Number;


    public function CarrierSlaDTOImpl() {
        maxDays = 1;
    }


    public function toString():String {
        return "CarrierSlaDTOImpl{carrierslaId=" + String(carrierslaId) + ",name=" + String(name) + ",carrierId=" + String(carrierId) + "}";
    }
}
}