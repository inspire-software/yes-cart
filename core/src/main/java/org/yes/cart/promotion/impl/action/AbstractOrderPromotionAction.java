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

package org.yes.cart.promotion.impl.action;

import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.shoppingcart.impl.TotalImpl;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2013
 * Time: 13:12
 */
public abstract class AbstractOrderPromotionAction extends AbstractPromotionAction {


    /**
     * Get current items total.
     *
     * @param context evaluation context
     *
     * @return total for items
     */
    protected Total getItemTotal(final Map<String, Object> context) {
        return (Total) context.get(PromotionCondition.VAR_CART_ITEM_TOTAL);
    }

    /**
     * Get current order total.
     *
     * @param context evaluation context
     *
     * @return order total
     */
    protected Total getTotal(final Map<String, Object> context) {
        return (Total) context.get(PromotionCondition.VAR_TMP_TOTAL);
    }

    /**
     * Set current order total
     *
     * @param context evaluation context
     *
     * @param total order total
     */
    protected void setTotal(final Map<String, Object> context, final Total total) {
        context.put(PromotionCondition.VAR_TMP_TOTAL, total);
    }

    /**
     * Subtract amount from the sub total to reflect promotion being applied.
     *
     * @param context evaluation context
     * @param amount amount off
     */
    protected void subtractPromotionValue(final Map<String, Object> context,
                                          final BigDecimal amount) {

        subtractPromotionValue(context, amount, getPromotionCode(context));

    }

    /**
     * Subtract amount from the sub total to reflect promotion being applied.
     *
     * @param context evaluation context
     * @param amount amount off
     * @param promoCode promotion code. Format: [Promotion.code][:[PromotionCoupon.code]]
     */
    protected void subtractPromotionValue(final Map<String, Object> context,
                                          final BigDecimal amount,
                                          final String promoCode) {

        final Total total = getTotal(context);
        final BigDecimal orderAmountOff;
        if (MoneyUtils.isFirstBiggerThanSecond(amount, total.getSubTotal())) {
            orderAmountOff = total.getSubTotal();
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
                        true,
                        promoCode,
                        orderAmountOff.negate(), // simple subtraction of base value
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
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO
                )));

    }

    /**
     * Add amount to the list price total to reflect promotion being applied.
     *
     * @param context evaluation context
     * @param listValue list value added by this promotion
     */
    protected void addListValue(final Map<String, Object> context,
                                final BigDecimal listValue) {
        addListValue(context, listValue, getPromotionCode(context));
    }

    /**
     * Add amount to the list price total to reflect promotion being applied.
     *
     * @param context evaluation context
     * @param listValue list value added by this promotion
     * @param promoCode promotion code. Format: [Promotion.code][:[PromotionCoupon.code]]
     */
    protected void addListValue(final Map<String, Object> context,
                                final BigDecimal listValue,
                                final String promoCode) {

        final Total total = getTotal(context);

        setTotal(
                context,
                total.add(new TotalImpl(
                        listValue,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO,
                        true,
                        promoCode,
                        MoneyUtils.ZERO,
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
                        MoneyUtils.ZERO,
                        MoneyUtils.ZERO
                )));

    }

}
