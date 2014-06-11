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
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
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
public class ItemNonSaleDiscountPromotionAction extends AbstractItemPromotionAction implements PromotionAction {

    /** {@inheritDoc} */
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        return getDiscountValue(getRawPromotionActionContext(context));
    }

    private BigDecimal getDiscountValue(final String ctx) {
        try {
            return new BigDecimal(ctx).movePointLeft(2);
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error("Unable top parse discount for promotion action context: {}", ctx);
        }
        return BigDecimal.ZERO;
    }

    /** {@inheritDoc} */
    public void perform(final Map<String, Object> context) {
        final BigDecimal discount = getDiscountValue(getRawPromotionActionContext(context));
        if (MoneyUtils.isFirstBiggerThanSecond(discount, BigDecimal.ZERO)) {
            final Promotion promotion = getPromotion(context);
            final ShoppingCart cart = getShoppingCart(context);
            final CartItem cartItem = getShoppingCartItem(context);

            // calculate discount relative to sale price
            final BigDecimal saleDiscount = cartItem.getListPrice().multiply(discount);
            final BigDecimal salePrice = cartItem.getListPrice().subtract(saleDiscount).setScale(2, RoundingMode.HALF_UP);
            final BigDecimal promoPrice;
            if (MoneyUtils.isFirstBiggerThanSecond(cartItem.getPrice(), salePrice)) {
                // we may have compound discounts so need to use final price
                promoPrice = salePrice;
            } else {
                promoPrice = cartItem.getPrice();
            }

            cart.setProductSkuPromotion(cartItem.getProductSkuCode(), promoPrice, getPromotionCode(context));

        }
    }
}
