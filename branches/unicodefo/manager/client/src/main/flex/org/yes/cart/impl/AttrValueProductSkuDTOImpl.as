package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.AttrValueProductSkuDTOImpl")]
public class AttrValueProductSkuDTOImpl {

    public var attrvalueId:Number;

    public var val: String ;

    public var attributeDTO:AttributeDTOImpl;

    public var skuId:Number;



    public function AttrValueProductSkuDTOImpl() {
    }


    public function toString():String {
        return "AttrValueProductSkuDTOImpl{attrvalueId=" + String(attrvalueId) +
               ",val=" + String(val) +
               ",attributeDTO=" + String(attributeDTO) +
               ",skuId=" + String(skuId) + "}";
    }
}
}