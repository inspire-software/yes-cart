package org.yes.cart.impl {
import mx.collections.ArrayCollection;
import mx.core.IUID;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.BrandDTOImpl")]

public class BrandDTOImpl implements IUID {

    public var brandId:Number;


    public var name:String;


    public var description:String;


    public var attribute:ArrayCollection;


    public function BrandDTOImpl() {
    }


    public function toString():String {
        return "BrandDTOImpl{brandId=" + String(brandId)
                + ",name=" + String(name)
                + ",description=" + String(description) + "}";
    }


    public function get uid():String {
        return "BrandDTOImpl-"+String(brandId);
    }

    public function set uid(value:String):void {
    }
}
}