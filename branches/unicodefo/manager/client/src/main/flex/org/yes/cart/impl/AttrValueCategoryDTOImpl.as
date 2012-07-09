package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.AttrValueCategoryDTOImpl")]
public class AttrValueCategoryDTOImpl {

    public var attrvalueId:Number;


    public var val:String ;


    public var attributeDTO:AttributeDTOImpl ;


    public var categoryId:Number;


    public function AttrValueCategoryDTOImpl() {
    }


    public function toString():String {
        return "AttrValueCategoryDTOImpl{attrvalueId=" + String(attrvalueId) +
               ",val=" + String(val) +
               ",attributeDTO=" + String(attributeDTO) +
               ",categoryId=" + String(categoryId) + "}";
    }
}
}