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

import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.MutableShoppingCart;
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
        final CartItem shipping = getShipping(context);
        return getDiscountValue(getRawPromotionActionContext(context), shipping.getSalePrice());
    }

    private BigDecimal getDiscountValue(final String ctx, final BigDecimal salePrice) {
        try {
            final BigDecimal amountOff = getAmountValue(ctx);
            if (amountOff.compareTo(BigDecimal.ZERO) > 0) {
                return amountOff.divide(salePrice, RoundingMode.HALF_UP);
            }
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error(
                    "Unable top parse amountOff for promotion action context: {}", ctx);
        }
        return MoneyUtils.ZERO;
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
            final MutableShoppingCart cart = getShoppingCart(context);
            final CartItem shipping = getShipping(context);

            // we may have compound discounts so need to use final price
            final BigDecimal promoPrice;
            if (MoneyUtils.isFirstBiggerThanSecond(amountOff, shipping.getPrice())) {
                promoPrice = MoneyUtils.ZERO;
            } else {
                promoPrice = shipping.getPrice().subtract(amountOff);
            }

            cart.setShippingPromotion(shipping.getProductSkuCode(), promoPrice, getPromotionCode(context));

        }
    }

}
