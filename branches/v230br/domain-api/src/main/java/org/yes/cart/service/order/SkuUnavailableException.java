/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.order;

/**
 * User: denispavlov
 * Date: 28/05/2015
 * Time: 13:45
 */
public class SkuUnavailableException extends OrderAssemblyException {

    private final String skuCode;
    private final String skuName;


    public SkuUnavailableException(final String skuCode, final String skuName) {
        super("Sku " + skuCode + ":" + skuName + " is not available");
        this.skuCode = skuCode;
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getSkuName() {
        return skuName;
    }
}
