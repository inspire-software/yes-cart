package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductAssociationDTOImpl")]
public class ProductAssociationDTOImpl {

    public var    productassociationId:Number;

    public var    rank:int;

    public var    associationId:Number;

    public var    productId:Number;

    public var    associatedProductId:Number;

    public var    associatedCode:String;

    public var    associatedName:String;

    public var    associatedDescrition:String;


    public function ProductAssociationDTOImpl() {
    }


    public function toString():String {
        return "ProductAssociationDTOImpl{productassociationId=" + String(productassociationId) +
               ",rank=" + String(rank) +
               ",associationId=" + String(associationId) +
               ",productId=" + String(productId) +
               ",associatedProductId=" + String(associatedProductId) +
               ",associatedCode=" + String(associatedCode) +
               ",associatedName=" + String(associatedName) + "}";
    }
}
}