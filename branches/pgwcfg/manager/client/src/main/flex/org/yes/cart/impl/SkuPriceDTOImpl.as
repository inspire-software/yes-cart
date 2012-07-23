/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.SkuPriceDTOImpl")]

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