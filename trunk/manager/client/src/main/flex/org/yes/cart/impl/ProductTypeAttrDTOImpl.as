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
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductTypeAttrDTOImpl")]
public class ProductTypeAttrDTOImpl {
    
    public var productTypeAttrId:Number;

    public var attributeDTO:AttributeDTOImpl;

    public var producttypeId:Number;

    public var rank:int;

    public var visible:Boolean;

    public var simulariry:Boolean;

    public var navigation:Boolean;

    public var navigationType:String;

    public var rangeNavigation:String;
    
    
    public function ProductTypeAttrDTOImpl() {
    }


    public function toString():String {
        return "ProductTypeAttrDTOImpl{productTypeAttrId=" + String(productTypeAttrId) +
               ",attributeDTO=" + String(attributeDTO) +
               ",producttypeId=" + String(producttypeId) +
               ",rank=" + String(rank) +
               ",visible=" + String(visible) +
               ",simulariry=" + String(simulariry) +
               ",navigation=" + String(navigation) +
               ",navigationType=" + String(navigationType) + "}";
    }
}
}