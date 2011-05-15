package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.SkuPriceDTOImpl")]

public class SkuPriceDTOImpl {
    
    public var skuPriceId:Number = 0;

    public var regularPrice:Number;

    public var salePrice:Number;

    public var salefrom:Date;

    public var saletill:Date;

    public var minimalPrice:Number;

    public var productSkuId:Number;

    public var shopId:Number = 0;

    public var quantity:Number;

    public var currency:String;

    public var code:String;

    public var skuName:String;
    
    
    public function SkuPriceDTOImpl() {
    }


    public function toString():String {
        return "SkuPriceDTOImpl{skuPriceId=" + String(skuPriceId) +
               ",regularPrice=" + String(regularPrice) +
               ",minimalPrice=" + String(minimalPrice) +
               ",salePrice=" + String(salePrice) +
               ",salefrom=" + String(salefrom) +
               ",saletill=" + String(saletill) +
               ",productSkuId=" + String(productSkuId) +
               ",shopId=" + String(shopId) +
               ",quantity=" + String(quantity) +
               ",currency=" + String(currency) +
               ",code=" + String(code) +
               ",skuName=" + String(skuName) + "}";
    }
}
}