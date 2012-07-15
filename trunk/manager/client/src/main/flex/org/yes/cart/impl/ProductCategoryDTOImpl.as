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
[RemoteClass(alias="org.yes.cart.domain.dto.impl.ProductCategoryDTOImpl")]

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