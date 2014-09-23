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

import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-30
 * Time: 8:15 AM
 */
public class ShippingAmountOffPromotionAction extends AbstractShippingPromotionAction implements PromotionAction {

    /** {@inheritDoc} */
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final Total itemTotal = getOrderTotal(context);
        return getDiscountValue(getRawPromotionActionContext(context), itemTotal.getDeliveryListCost());
    }

    private BigDecimal getDiscountValue(final String ctx, final BigDecimal salePrice) {
        try {
            final BigDecimal amountOff = getAmountValue(ctx);
            if (amountOff.compareTo(BigDecimal.ZERO) > 0) {
                return amountOff.divide(salePrice, RoundingMode.HALF_UP);
            }
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error("Unable top parse discount for promotion action context: {}", ctx);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getAmountValue(final String ctx) {
        try {
            return new BigDecimal(ctx).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception exp) {
            return MoneyUtils.ZERO;
        }
    }


    /** {@inheritDoc} */
    public void perform(final Map<String, Object> context) {
        final BigDecimal amountOff = getAmountValue(getRawPromotionActionContext(context));
        if (MoneyUtils.isFirstBiggerThanSecond(amountOff, BigDecimal.ZERO)) {

            subtractPromotionValue(context, amountOff);

        }
    }

}
