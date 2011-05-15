package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.SkuWarehouseDTOImpl")]
public class SkuWarehouseDTOImpl {
    
    public var skuWarehouseId:Number;

    public var productSkuId:Number;

    public var skuCode:String;

    public var skuName:String;

    public var warehouseId:Number;

    public var warehouseCode:String;

    public var warehouseName:String;

    public var quantity:Number;
    
    public function SkuWarehouseDTOImpl() {
    }

    public function toString():String {
        return "SkuWarehouseDTOImpl{productSkuId=" + String(productSkuId) +
               ",skuCode=" + String(skuCode) +
               ",skuName=" + String(skuName) +
               ",warehouseId=" + String(warehouseId) +
               ",warehouseCode=" + String(warehouseCode) +
               ",quantity=" + String(quantity) + "}";
    }
}
}