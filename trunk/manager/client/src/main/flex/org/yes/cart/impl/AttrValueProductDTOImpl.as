package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.AttrValueProductDTOImpl")]
public class AttrValueProductDTOImpl {
    
    public var attrvalueId:Number;

    public var val:String;

    public  var attributeDTO:AttributeDTOImpl;

    public var productId:Number;    
    
    public function AttrValueProductDTOImpl() {
    }


    public function toString():String {
        return "AttrValueProductDTOImpl{attrvalueId=" + String(attrvalueId) +
               ",val=" + String(val) +
               ",attributeDTO=" + String(attributeDTO) +
               ",productId=" + String(productId) + "}";
    }
}
}