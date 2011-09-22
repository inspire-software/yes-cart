package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ShopDTOImpl")]
public class ShopDTOImpl {

    public var code:String;

    public var name:String;

    public var description:String;

    public var fspointer:String;

    public var imageVaultFolder:String;

    public var shopId:Number;
    
    
    public function ShopDTOImpl() {
        shopId = 0;
    }


    public function toString():String {
        return "ShopDTOImpl{code=" + String(code) +
               ",name=" + String(name) +
               ",description=" + String(description) +
               ",fspointer=" + String(fspointer) +
               ",shopId=" + String(shopId) + "}";
    }
}
}