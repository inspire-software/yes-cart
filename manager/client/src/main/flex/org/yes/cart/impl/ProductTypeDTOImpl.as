package org.yes.cart.impl {
import mx.core.IUID;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductTypeDTOImpl")]

public class ProductTypeDTOImpl implements IUID {
    
    
    public var producttypeId:Number;

    public var name:String;

    public var description:String;

    public var uitemplate:String;

    public var uisearchtemplate:String;

    public var service:Boolean;

    public var ensemble:Boolean;

    public var shipable:Boolean;

    public var downloadable:Boolean;

    public var digital:Boolean;

    
    public function ProductTypeDTOImpl() {
    }


    public function toString():String {
        return "ProductTypeDTOImpl{producttypeId=" + String(producttypeId)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",uitemplate=" + String(uitemplate)
                + ",uisearchtemplate=" + String(uisearchtemplate)
                + ",service=" + String(service)
                + ",ensemble=" + String(ensemble) + ",shipable=" + String(shipable) + "}";
    }

    public function get uid():String {
        return "ProductTypeDTOImpl-"+String(producttypeId);
    }

    public function set uid(value:String):void {
    }

    
}
}