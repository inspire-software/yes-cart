/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.promotion;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

/**
 * Coupons are very specific and hence require separate context which work "backwards"
 * by looking up the coupon first and then determining if promotion applied.
 *
 * This is reverse of the process that promotion engine uses which gets all active
 * promotions and then evaluates them.
 *
 * User: denispavlov
 * Date: 05/06/2014
 * Time: 15:26
 */
public interface PromotionCouponContext {

    /**
     * Apply coupon promotion.
     *
     * @param customer customer
     * @param cart cart
     * @param orderTotal current total after order and shipping
     *
     * @return  order total (does not include shipping promotions)
     */
    Total applyCouponPromo(Customer customer, ShoppingCart cart, Total orderTotal);

}
