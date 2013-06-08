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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-04-06
 * Time: 1:08 PM
 */
public interface ProductAvailabilityModel {

    /**
     * @return true if this product is purchasable at minimum quantity.
     */
    boolean isAvailable();

    /**
     * Returns true only if there is actual stock for this product.
     * For perpetual, preorder and back order items this will return
     * false.
     *
     * @return true if product is in stock.
     */
    boolean isInStock();

    /**
     * @return product that is always available (e.g. digital download or service)
     */
    boolean isPerpetual();

    /**
     * @return quantity that is available for customer to purchase.
     */
    BigDecimal getAvailableToSellQuantity();

}
