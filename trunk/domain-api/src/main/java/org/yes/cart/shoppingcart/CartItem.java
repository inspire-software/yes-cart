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

package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cart item object that hold information about a single shopping item.
 * <p/>
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:25:01 PM
 */
public interface CartItem extends Serializable {

    /**
     * @return product sku code.
     */
    String getProductSkuCode();

    /**
     * @return quantity of the above sku to be purchased
     */
    BigDecimal getQty();

    /**
     * Get the sku price price.
     *
     * @return sku price.
     */
    BigDecimal getPrice();

    /**
     * Get list / catalog price.
     * @return price
     */
    BigDecimal getListPrice();

}