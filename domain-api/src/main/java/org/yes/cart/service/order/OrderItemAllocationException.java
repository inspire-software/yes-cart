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

package org.yes.cart.service.order;

import java.math.BigDecimal;

/**
 * Will be thrown when items quantity can not be allocated.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 4/21/12
 * Time: 8:53 PM
 */
public class OrderItemAllocationException extends OrderException {

    private final String productSkuCode;

    private final  BigDecimal quantity;


    /**
     *
     * @param productSkuCode sku code, which can not be allocated.
     * @param quantity which can not be allocated.
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     */
    public OrderItemAllocationException(final String productSkuCode, final  BigDecimal quantity, final String message) {
        super(message);
        this.productSkuCode = productSkuCode;
        this.quantity = quantity;
    }

    /**
     *
     * @return product sku code
     */
    public String getProductSkuCode() {
        return productSkuCode;
    }

    /**
     * Get quantity which can not be allocated.
     * Two will be returned in case if shopper has 10 items in cart, but only 8 items available
     * @return quantity which can not be allocated.
     */
    public BigDecimal getQuantity() {
        return quantity;
    }
}
