package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.AttrValueBrandDTOImpl")]
public class AttrValueBrandDTOImpl {
    
    
    public var attrvalueId:Number;

    public var val:String;

    public var attributeDTO:AttributeDTOImpl;

    public var brandId:Number;
    
    public function AttrValueBrandDTOImpl() {
    }


    public function toString():String {
        return "AttrValueBrandDTOImpl{attrvalueId=" + String(attrvalueId) +
               ",val=" + String(val) +
               ",attributeDTO=" + String(attributeDTO) +
               ",brandId=" + String(brandId) + "}";
    }
}

}