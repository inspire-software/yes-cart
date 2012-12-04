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

/**
 * User: denispavlov
 * Date: 12-07-26
 * Time: 6:55 PM
 */
package org.yes.cart.filter {
import org.yes.cart.impl.ShopDTOImpl;

[Bindable]
[RemoteClass(alias="org.yes.cart.service.dto.support.impl.PriceListFilterImpl")]
public class PriceListFilter {

    public var shop:ShopDTOImpl;
    public var currencyCode:String;
    public var productCode:String;
    public var productCodeExact:Boolean;
    public var tag:String;
    public var tagExact:Boolean;
    public var from:Date;
    public var to:Date;

    public function PriceListFilter() {
    }
}
}
