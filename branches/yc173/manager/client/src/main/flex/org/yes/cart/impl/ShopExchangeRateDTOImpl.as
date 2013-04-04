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
/**
 *
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 1:23 PM
 *
 */
public class ShopExchangeRateDTOImpl {

    public var shopexchangerateId:Number;

    public var fromCurrency : String;

    public var toCurrency : String;

    public var shopId : Number;

    public var rate : Number;


    public function ShopExchangeRateDTOImpl() {

    }


    public function toString():String {
        return "ShopExchangeRateDTOImpl{shopexchangerateId=" + String(shopexchangerateId) + ",fromCurrency=" + String(fromCurrency) + ",toCurrency=" + String(toCurrency) + ",shopId=" + String(shopId) + ",rate=" + String(rate) + "}";
    }
}
}
