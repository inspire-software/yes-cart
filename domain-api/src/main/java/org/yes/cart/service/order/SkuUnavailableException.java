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
    private final boolean onlineNow;


    public SkuUnavailableException(final String skuCode, final String skuName, final boolean onlineNow) {
        super("Sku " + skuCode + ":" + skuName + " is not available, cause: " + (onlineNow ? " out of stock" : "offline"));
        this.skuCode = skuCode;
        this.skuName = skuName;
        this.onlineNow = onlineNow;
    }

    /**
     * Sku code as stated in cart item
     *
     * @return code
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * Sku name as stated in cart item
     *
     * @return code
     */
    public String getSkuName() {
        return skuName;
    }

    /**
     * Flag to determine if the product is online at the time of throuwing exception.
     * true if online (then this is thrown due to item being out of stock), false if offline.
     * Unresolved SKU are always assumed to be online.
     *
     * @return online flag
     */
    public boolean isOnlineNow() {
        return onlineNow;
    }
}
