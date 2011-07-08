package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.AttrValueCustomerDTOImpl")]
public class AttrValueCustomerDTOImpl {

    public var attrvalueId:Number;

    public var val:String ;

    public var attributeDTO:AttributeDTOImpl ;

    public var customerId:Number;

    public function AttrValueCustomerDTOImpl() {
    }


    public function toString():String {
        return "AttrValueCustomerDTOImpl{attrvalueId=" + String(attrvalueId)
                + ",val=" + String(val)
                + ",attributeDTO=" + String(attributeDTO)
                + ",customerId=" + String(customerId) + "}";
    }
}
}