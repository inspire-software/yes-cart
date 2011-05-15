package org.yes.cart.impl {
import mx.core.IUID;

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.AvailabilityDTOImpl")]

public class AvailabilityDTOImpl implements IUID {

    public var availabilityId:Number;


    public var name:String;


    public var description:String ;


    public function AvailabilityDTOImpl() {
    }


    public function toString():String {
        return "AvailabilityDTOImpl{availabilityId=" + String(availabilityId)
                + ",name=" + String(name)
                + ",description=" + String(description) + "}";
    }


    public function get uid():String {
        return "AvailabilityDTOImpl-"+String(availabilityId);
    }

    public function set uid(value:String):void {
    }

     


}
}