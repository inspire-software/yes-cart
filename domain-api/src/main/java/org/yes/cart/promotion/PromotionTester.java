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

package org.yes.cart.promotion;

import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/04/2018
 * Time: 18:50
 */
public interface PromotionTester {


    /**
     * Test promotions.
     *
     * @param shopCode shop code
     * @param currency currency
     * @param language language
     * @param customer customer (optional)
     * @param products products SKU and corresponding quantities
     * @param shipping shipping SLA
     * @param coupons  coupon codes
     * @param time     time now
     *
     * @return generated prices
     */
    ShoppingCart testPromotions(String shopCode,
                                String currency,
                                String language,
                                String customer,
                                Map<String, BigDecimal> products,
                                String shipping,
                                List<String> coupons,
                                Instant time);

}
