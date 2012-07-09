package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.WarehouseDTOImpl")]
public class WarehouseDTOImpl {

    public var warehouseId:Number;

    public var code:String;

    public var name:String;

    public var description: String;

    public var countryCode:String;

    public var stateCode:String;

    public var city:String;

    public var postcode:String;



    public function WarehouseDTOImpl() {
    }


    public function toString():String {
        return "WarehouseDTOImpl{warehouseId=" + String(warehouseId) +
               ",code=" + String(code) +
               ",name=" + String(name) +
               ",description=" + String(description) + "}";
    }
}
}