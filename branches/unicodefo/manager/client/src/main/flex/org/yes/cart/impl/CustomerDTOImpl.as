package org.yes.cart.impl {
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.CustomerDTOImpl")]
public class CustomerDTOImpl {

    public var customerId:Number;

    public var email:String;

    public var firstname:String;

    public var lastname:String;

    public var middlename:String;

    public var attribute:ArrayCollection;

    public function CustomerDTOImpl() {
    }
}

}