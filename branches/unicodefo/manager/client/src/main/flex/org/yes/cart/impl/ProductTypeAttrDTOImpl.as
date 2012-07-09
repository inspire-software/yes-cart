package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductTypeAttrDTOImpl")]
public class ProductTypeAttrDTOImpl {
    
    public var productTypeAttrId:Number;

    public var attributeDTO:AttributeDTOImpl;

    public var producttypeId:Number;

    public var rank:int;

    public var visible:Boolean;

    public var simulariry:Boolean;

    public var navigation:Boolean;

    public var navigationType:String;

    public var rangeNavigation:String;
    
    
    public function ProductTypeAttrDTOImpl() {
    }


    public function toString():String {
        return "ProductTypeAttrDTOImpl{productTypeAttrId=" + String(productTypeAttrId) +
               ",attributeDTO=" + String(attributeDTO) +
               ",producttypeId=" + String(producttypeId) +
               ",rank=" + String(rank) +
               ",visible=" + String(visible) +
               ",simulariry=" + String(simulariry) +
               ",navigation=" + String(navigation) +
               ",navigationType=" + String(navigationType) + "}";
    }
}
}