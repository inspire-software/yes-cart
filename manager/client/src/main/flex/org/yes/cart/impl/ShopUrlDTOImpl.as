package org.yes.cart.impl {
[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ShopUrlDTOImpl")]
public class ShopUrlDTOImpl {

    public var  url:String;

    public var  storeUrlId:Number ;

    public var  shopId:Number;

    public function ShopUrlDTOImpl() {
    }


    public function toString():String {
        return "ShopUrlDTOImpl{url=" + String(url)
                + ",storeUrlId=" + String(storeUrlId)
                + ",shopId=" + String(shopId) + "}";
    }
}
}