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
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductSkuDTOImpl")]

public class ProductSkuDTOImpl {
    
    public var skuId:Number ;

    public var code:String ;

    public var name:String ;

    public var displayNames:Object;

    public var  description:String;

    public var  productId:Number;

    public var rank:int ;

    public var  barCode:String;

    public var attributes:ArrayCollection;

    public var uri:String;

    public var title:String;

    public var metakeywords:String;

    public var metadescription:String;

    public var displayTitles:Object;

    public var displayMetakeywords:Object;

    public var displayMetadescriptions:Object;


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
                + ",attribute=" + String(attributes)
                + ",price=" + String(price) + "}";
    }
}
}