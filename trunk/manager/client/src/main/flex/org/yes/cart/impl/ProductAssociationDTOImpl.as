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