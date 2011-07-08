package org.yes.cart.impl {
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductSkuDTOImpl")]

public class ProductSkuDTOImpl {
    
    public var skuId:Number ;

    public var code:String ;

    public var name:String ;

    public var  description:String;

    public var  productId:Number;

    public var rank:int ;

    public var  barCode:String;

    public var attribute:ArrayCollection;

    public var  seoDTO:SeoDTOImpl;

    public var price:ArrayCollection;
    
    
    public function ProductSkuDTOImpl() {
    }


    public function toString():String {
        return "ProductSkuDTOImpl{skuId=" + String(skuId)
                + ",code=" + String(code)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",productId=" + String(productId)
                + ",rank=" + String(rank)
                + ",barCode=" + String(barCode)
                + ",attribute=" + String(attribute)
                + ",seoDTO=" + String(seoDTO)
                + ",price=" + String(price) + "}";
    }
}
}