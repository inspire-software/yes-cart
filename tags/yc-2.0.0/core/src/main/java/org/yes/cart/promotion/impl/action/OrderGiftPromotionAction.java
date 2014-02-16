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
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 13-10-30
 * Time: 8:15 AM
 */
public class OrderGiftPromotionAction extends AbstractOrderPromotionAction implements PromotionAction {

    // Ration allows to specify what 1 unit of context equates to items quantity
    // i.e. Gift "ABC : 2" - 1 gift for every two quantity of items
    private static final Pattern RATIO_PATTERN = Pattern.compile("(.*)(( (=|~) )((\\d*)\\.?(\\d*)))");

    private final PriceService priceService;
    private final ShopService shopService;

    public OrderGiftPromotionAction(final PriceService priceService,
                                    final ShopService shopService) {
        this.priceService = priceService;
        this.shopService = shopService;
    }


    /** {@inheritDoc} */
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final Total itemTotal = getItemTotal(context);
        return getAmountValue(context).divide(itemTotal.getPriceSubTotal(), RoundingMode.HALF_UP);
    }

    private BigDecimal getAmountValue(final Map<String, Object> context) {

        final Total itemTotal = getItemTotal(context);
        final ItemPromotionActionContext ctx = getPromotionActionContext(context);
        final ShoppingCart cart = getShoppingCart(context);

        final SkuPrice giftValue = getGiftPrices(ctx.getSubject(), cart);
        if (giftValue == null) {
            return BigDecimal.ZERO;
        }
        final BigDecimal minimal = MoneyUtils.minPositive(giftValue.getSalePriceForCalculation(), giftValue.getRegularPrice());
        if (minimal == null) {
            return BigDecimal.ZERO;
        }
        final BigDecimal multiplier = ctx.getMultiplier(itemTotal.getPriceSubTotal());
        if (multiplier.compareTo(BigDecimal.ZERO) > 0) {
            if (multiplier.compareTo(BigDecimal.ONE) == 0) {
                return minimal;
            }
            return minimal.divide(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }


    private SkuPrice getGiftPrices(final String sku, final ShoppingCart cart) {
        try {
            return priceService.getMinimalRegularPrice(
                    null,
                    sku,
                    shopService.getById(cart.getShoppingContext().getShopId()),
                    cart.getCurrencyCode(),
                    BigDecimal.ONE);
        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error(
                    "Unable top find price for gift for promotion action context: {}", sku);
        }
        return null;
    }


    /** {@inheritDoc} */
    public void perform(final Map<String, Object> context) {

        final ItemPromotionActionContext ctx = getPromotionActionContext(context);
        final Total itemTotal = getItemTotal(context);
        final ShoppingCart cart = getShoppingCart(context);

        final SkuPrice giftValue = getGiftPrices(ctx.getSubject(), cart);
        if (giftValue != null) {
            final Promotion promotion = getPromotion(context);

            // add gift and set its price, we assume gift are in whole units
            final BigDecimal giftQty = BigDecimal.ONE.multiply(ctx.getMultiplier(itemTotal.getPriceSubTotal())).setScale(0, RoundingMode.HALF_UP).setScale(2);
            cart.addGiftToCart(ctx.getSubject(), giftQty, promotion.getCode());
            final BigDecimal minimal = MoneyUtils.minPositive(giftValue.getSalePriceForCalculation(), giftValue.getRegularPrice());
            cart.setGiftPrice(ctx.getSubject(), minimal, giftValue.getRegularPrice());

            addListValue(context, promotion.getCode(), giftValue.getRegularPrice().multiply(giftQty).setScale(2, RoundingMode.HALF_UP));
        }
    }


    /**
     * Promotion context with support for cart item quantity decisions.
     *
     * @param context evaluation context
     *
     * @return action context
     */
    protected ItemPromotionActionContext getPromotionActionContext(final Map<String, Object> context) {
        return new ItemPromotionActionContext(getRawPromotionActionContext(context));
    }

    protected static class ItemPromotionActionContext {

        private final String subject;
        private final boolean exact;
        private final BigDecimal ratio;

        public ItemPromotionActionContext(final String rawCtx) {
            final Matcher ratio = RATIO_PATTERN.matcher(rawCtx);
            if (ratio.matches()) {
                subject = ratio.group(1); // context
                exact = "=".equals(ratio.group(4)); // = exact, ~ scale
                this.ratio = new BigDecimal(ratio.group(5));
            } else {
                subject = rawCtx;
                exact = false;
                this.ratio = null;
            }
        }

        /**
         * The actual context: i.e. fixed amount, gift sku.
         *
         * @return subject of this context
         */
        public String getSubject() {
            return subject;
        }

        /**
         * Multiplier is the number of times this action can be applied
         * for given item line.
         *
         * @param orderAmount quantity
         *
         * @return multiplier
         */
        public BigDecimal getMultiplier(BigDecimal orderAmount) {
            if (ratio == null) {
                return BigDecimal.ONE;
            } else if (exact) {
                // exact means that you need to reach next level to get one more
                return orderAmount.divide(ratio, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
            } else {
                // else we have a scale and we get non exact ratio
                return orderAmount.divide(ratio, RoundingMode.CEILING).setScale(0, RoundingMode.CEILING);
            }
        }

    }

}
