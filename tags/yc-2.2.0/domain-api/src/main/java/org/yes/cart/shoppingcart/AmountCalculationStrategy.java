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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

/**
 *
 * This strategy responsible for following kind of calculation:
 *
 * Cart sub total
 * Shipping amount
 * Cart Total
 * Taxes
 *
 * Calculation may varying in different countries and counties because of
 * different regulation policy.
 *
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 27/11/11
 * Time: 18:57
 */
public interface AmountCalculationStrategy {

    /**
     * Calculate shopping cart amount.
     *
     * @param cart shopping cart
     *
     * @return {@link Total}
     */
    Total calculate(MutableShoppingCart cart);

    /**
     * Calculate single delivery amount.
     *
     * @param order order
     * @param orderDelivery  {@link CustomerOrderDelivery}
     *
     * @return {@link Total}
     */
    Total calculate(CustomerOrder order, CustomerOrderDelivery orderDelivery);

    /**
     * Calculate order amount.
     *
     * @param order order
     *
     * @return {@link Total}
     */
    Total calculate(CustomerOrder order);

}
