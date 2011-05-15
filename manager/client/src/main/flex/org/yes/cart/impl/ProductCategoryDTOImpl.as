package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.impl.ProductCategoryDTOImpl")]

public class ProductCategoryDTOImpl {

    public var productCategoryId:Number;

    public var productId:Number;

    public var categoryId:Number;

    public var categoryName:String;

    public var rank:Number;


    public function ProductCategoryDTOImpl() {
    }


    public function toString():String {
        return "ProductCategoryDTOImpl{productCategoryId=" + String(productCategoryId)
                + ",productId=" + String(productId)
                + ",categoryId=" + String(categoryId)
                + ",rank=" + String(rank) + "}";
    }
}
}