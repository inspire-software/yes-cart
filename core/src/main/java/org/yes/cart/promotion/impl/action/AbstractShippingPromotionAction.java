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

package org.yes.cart.promotion.impl.action;

import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.shoppingcart.impl.TotalImpl;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2013
 * Time: 13:12
 */
public abstract class AbstractShippingPromotionAction extends AbstractPromotionAction {


    /**
     * Get current items total.
     *
     * @param context evaluation context
     *
     * @return total for items
     */
    protected Total getOrderTotal(final Map<String, Object> context) {
        return (Total) context.get(PromotionCondition.VAR_CART_ORDER_TOTAL);
    }

    private Total getTotal(final Map<String, Object> context) {
        return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);
    }

    private void setTotal(final Map<String, Object> context, final Total total) {
        context.put(PromotionCondition.VAR_TMP_TOTAL, total);
    }

    /**
     * Subtract amount from the sub total to reflect promotion being applied.
     *
     * @param promoCode promo code
     * @param amount amount off
     */
    protected void subtractPromotionValue(final Map<String, Object> context,
                                          final String promoCode,
                                          final BigDecimal amount) {

        final Total total = getTotal(context);
        final BigDecimal orderAmountOff;
        if (MoneyUtils.isFirstBiggerThanSecond(amount, total.getDeliveryCost())) {
            orderAmountOff = total.getDeliveryCost();
        } else {
            orderAmountOff = amount;
        }

        setTotal(
                context,
                total.add(new TotalImpl(
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        false,
                        null,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        orderAmountOff.negate(), // simple subtraction of base value
                        true,
                        promoCode,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO
                )));

    }

}
