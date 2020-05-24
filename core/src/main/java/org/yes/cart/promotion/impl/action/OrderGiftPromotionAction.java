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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.OrderSplittingStrategy;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.log.Markers;

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

    private static final Logger LOG = LoggerFactory.getLogger(OrderGiftPromotionAction.class);

    // Ratio allows to specify what 1 unit of context equates to items quantity
    // i.e. Gift "ABC : 2" - 1 gift for every two quantity of items
    private static final Pattern RATIO_PATTERN = Pattern.compile("(.*)(( (=|~) )((\\d*)\\.?(\\d*)))");

    private final PriceResolver priceResolver;
    private final InventoryResolver inventoryResolver;
    private final ShopService shopService;
    private final WarehouseService warehouseService;
    private final ProductService productService;
    private final OrderSplittingStrategy orderSplittingStrategy;

    public OrderGiftPromotionAction(final PriceResolver priceResolver,
                                    final InventoryResolver inventoryResolver,
                                    final ShopService shopService,
                                    final WarehouseService warehouseService,
                                    final ProductService productService,
                                    final OrderSplittingStrategy orderSplittingStrategy) {
        this.priceResolver = priceResolver;
        this.inventoryResolver = inventoryResolver;
        this.shopService = shopService;
        this.warehouseService = warehouseService;
        this.productService = productService;
        this.orderSplittingStrategy = orderSplittingStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final ItemPromotionActionContext ctx = getPromotionActionContext(context);
        final Total itemTotal = getItemTotal(context);
        final BigDecimal giftQty = BigDecimal.ONE.multiply(ctx.getMultiplier(itemTotal.getPriceSubTotal())).setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        if (MoneyUtils.isPositive(giftQty)) {
            final BigDecimal giftValue = getAmountValue(context, giftQty);
            if (MoneyUtils.isPositive(giftValue) && MoneyUtils.isPositive(itemTotal.getPriceSubTotal())) {
                return giftValue.divide(itemTotal.getPriceSubTotal(), RoundingMode.HALF_UP);
            }
            return BigDecimal.ONE; // Assume 100% as we have no price to compare
        }
        return BigDecimal.ZERO; // no gift
    }

    private BigDecimal getAmountValue(final Map<String, Object> context, final BigDecimal giftQty) {

        final ItemPromotionActionContext ctx = getPromotionActionContext(context);
        final Total itemTotal = getItemTotal(context);
        final ShoppingCart cart = getShoppingCart(context);

        final BigDecimal multiplier = ctx.getMultiplier(itemTotal.getPriceSubTotal());
        if (MoneyUtils.isPositive(multiplier)) {

            final String supplier = getGiftSupplier(ctx.getSubject(), cart, giftQty);
            if (supplier == null) {
                LOG.warn(Markers.alert(), "Gift item {} is now out of stock", ctx.getSubject());
                return BigDecimal.ZERO;
            }

            final SkuPrice giftValue = getGiftPrices(ctx.getSubject(), cart, supplier);
            if (giftValue == null) {
                return BigDecimal.ZERO;
            }

            final BigDecimal minimal = MoneyUtils.minPositive(giftValue.getSalePriceForCalculation());
            if (MoneyUtils.isPositive(minimal)) {
                if (MoneyUtils.isFirstEqualToSecond(multiplier, BigDecimal.ONE)) {
                    return minimal;
                }
                return minimal.divide(multiplier, 2, RoundingMode.HALF_UP);
            }

        }
        return BigDecimal.ZERO;
    }

    private String getGiftSupplier(final String sku, final ShoppingCart cart, final BigDecimal qty) {

        final long shopId = cart.getShoppingContext().getCustomerShopId();
        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);

        for (final Warehouse warehouse : warehouses.values()) {
            final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, sku);
            if (inventory != null && inventory.isAvailableToSell(qty, true)) {
                return warehouse.getCode();
            }
        }

        return null;
    }

    private SkuPrice getGiftPrices(final String sku, final ShoppingCart cart, final String supplier) {
        try {
            final long customerShopId = cart.getShoppingContext().getCustomerShopId();
            final long masterShopId = cart.getShoppingContext().getShopId();
            // Fallback only if we have a B2B non-strict mode
            final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;

            return priceResolver.getMinimalPrice(
                    null,
                    sku,
                    customerShopId,
                    fallbackShopId,
                    cart.getCurrencyCode(),
                    BigDecimal.ONE, false, null, supplier);
        } catch (Exception exp) {
            LOG.error("Unable to find price for gift for promotion action context: {}", sku);
        }
        return null;
    }

    private String getSkuName(final String code, final String lang) {

        final ProductSku sku = this.productService.getProductSkuByCode(code);

        if (sku == null) {
            return code;
        }

        return new FailoverStringI18NModel(
                sku.getDisplayName(),
                sku.getName()
        ).getValue(lang);

    }

    /** {@inheritDoc} */
    @Override
    public void perform(final Map<String, Object> context) {

        final ItemPromotionActionContext ctx = getPromotionActionContext(context);
        final Total itemTotal = getItemTotal(context);
        final MutableShoppingCart cart = getShoppingCart(context);
        final BigDecimal giftQty = BigDecimal.ONE.multiply(ctx.getMultiplier(itemTotal.getPriceSubTotal())).setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

        final String supplier = getGiftSupplier(ctx.getSubject(), cart, giftQty);
        if (supplier == null) {
            LOG.warn(Markers.alert(), "Gift item {} is now out of stock", ctx.getSubject());
        } else {

            final SkuPrice giftValue = getGiftPrices(ctx.getSubject(), cart, supplier);
            if (giftValue != null) {
                // add gift and set its price, we assume gift are in whole units
                cart.addGiftToCart(supplier, ctx.getSubject(), getSkuName(ctx.getSubject(), cart.getCurrentLocale()), giftQty, getPromotionCode(context));

                final Pair<BigDecimal, BigDecimal> listAndSale = giftValue.getSalePriceForCalculation();
                final BigDecimal list = listAndSale.getFirst();
                final BigDecimal sale = listAndSale.getSecond();

                cart.setGiftPrice(supplier, ctx.getSubject(), sale != null ? sale : list, list);

                addListValue(context, MoneyUtils.notNull(list).multiply(giftQty).setScale(2, RoundingMode.HALF_UP));

                for (final CartItem item : cart.getCartItemList()) {
                    if (item.isGift() && ctx.getSubject().equals(item.getProductSkuCode())) {
                        final DeliveryBucket bucket = this.orderSplittingStrategy.determineDeliveryBucket(item, cart);
                        cart.setGiftDeliveryBucket(ctx.getSubject(), bucket);
                    }
                }
            }
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
