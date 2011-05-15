package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.impl.CarrierDTOImpl")]
public class CarrierDTOImpl {

    public var carrierId:Number;

    public var name:String;

    public var  description:String;

    public var worldwide:Boolean;

    public var country:Boolean;

    public var state:Boolean;

    public var local:Boolean;


    public function CarrierDTOImpl() {
    }


    public function toString():String {
        return "CarrierDTOImpl{carrierId=" + String(carrierId) + ",name=" + String(name) + "}";
    }
}
}