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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Default calculation strategy provides basic functionality necessary for promotion
 * calculation and taxes.
 *
 * Promotions assumed to be only applicable to shopping cart as whatever is displayed in
 * shopping cart should be what goes into the order - we deliver what we promise!
 *
 * Hence promotion engine applies all necessary item, order and shipping level promotions
 * when shopping cart is recalculated. At the point when user checks out shopping cart
 * already contains all promotions and hence they can simply be copied to the order
 * entity.
 *
 * Calculation of order does not involve any manipulation of the order or deliveries it
 * simply scans all object tree to compute totals and taxes.
 *
 * Shipping cost are special since there is no default per se and most of shipping logic is
 * either fixed price used from SLA (default strategy) or some carrier specific customisation
 * which should be implemented on per project basis.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy implements AmountCalculationStrategy {

    private static final BigDecimal ZERO = MoneyUtils.ZERO;
    private static final BigDecimal HUNDRED = MoneyUtils.HUNDRED;
    private static final Total ZERO_TOTAL = new TotalImpl();

    private final TaxProvider taxProvider;
    private final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy;
    private final PromotionContextFactory promotionContextFactory;
    private final CustomerService customerService;
    private final ShopService shopService;

    /**
     * Construct default amount calculator with included tax.
     *
     * @param taxProvider tax configuration provider
     * @param deliveryCostCalculationStrategy delivery cost calculation strategy
     * @param promotionContextFactory promotion context
     * @param customerService customer service
     * @param shopService shop service
     */
    public DefaultAmountCalculationStrategy(final TaxProvider taxProvider,
                                            final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy,
                                            final PromotionContextFactory promotionContextFactory,
                                            final CustomerService customerService,
                                            final ShopService shopService) {

        this.taxProvider = taxProvider;
        this.deliveryCostCalculationStrategy = deliveryCostCalculationStrategy;
        this.promotionContextFactory = promotionContextFactory;
        this.customerService = customerService;
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public Total calculate(final MutableShoppingCart cart) {

        final Customer customer;
        if (StringUtils.isNotBlank(cart.getCustomerEmail())) {
            final Shop shop = shopService.getById(cart.getShoppingContext().getShopId());
            if (shop != null) {
                customer = customerService.getCustomerByEmail(cart.getCustomerEmail(), shop);
            } else {
                customer = null;
            }
        } else {
            customer = null;
        }

        final PromotionContext promoCtx = promotionContextFactory.getInstance(cart.getShoppingContext().getShopCode(), cart.getCurrencyCode());

        // 1. Apply all item level promotions as the first step
        applyItemLevelPromotions(customer, cart, promoCtx);

        // 2. Calculate current subtotal (including item promotions, excluding delivery cost)
        final Total itemTotal = applyTaxToCartItemsAndCalculateItemTotal(cart);

        // 3. Calculate delivery costs (just for reference)
        final Total deliveryCostTotalNoTax = deliveryCostCalculationStrategy.calculate(cart);

        // 4. Create dummy total for items + delivery cost
        final Total draftOrderTotal = deliveryCostTotalNoTax != null ? itemTotal.add(deliveryCostTotalNoTax) : itemTotal.add(ZERO_TOTAL);

        // 5. Use current cart + dummy item total to calculate order level promotions
        final Total orderTotal = applyOrderLevelPromotions(customer, cart, draftOrderTotal, promoCtx);

        // 6. At this stage we have reliable total for the whole order so we can
        //    calculate shipping promotions
        applyShippingPromotions(customer, cart, orderTotal, promoCtx);

        // 7. Calculate final order total (including delivery cost)
        final Total finalTotal = applyTaxToShippingAndCalculateOrderTotal(cart, orderTotal);

        return finalTotal;

    }

    void applyItemLevelPromotions(final Customer customer,
                                  final MutableShoppingCart cart,
                                  final PromotionContext promoCtx) {

        promoCtx.applyItemPromo(customer, cart);

    }

    Total applyOrderLevelPromotions(final Customer customer,
                                    final MutableShoppingCart cart,
                                    final Total itemTotal,
                                    final PromotionContext promoCtx) {

        final Total tmp = promoCtx.applyOrderPromo(customer, cart, itemTotal);

        final BigDecimal orderLevelDiscountRatio = MoneyUtils.isPositive(itemTotal.getSubTotal()) ? tmp.getSubTotal().divide(itemTotal.getSubTotal(), 16, RoundingMode.HALF_UP) : Total.ZERO;

        final BigDecimal subTotal = tmp.getSubTotal();
        final BigDecimal subTotalTax = multiply(itemTotal.getSubTotalTax(), orderLevelDiscountRatio);
        final BigDecimal subTotalAmount = MoneyUtils.isFirstBiggerThanSecond(itemTotal.getSubTotalAmount(), itemTotal.getSubTotal()) ? subTotal.add(subTotalTax) : subTotal;

        final BigDecimal total = subTotal.add(tmp.getDeliveryCost());
        final BigDecimal totalTax = subTotalTax.add(tmp.getDeliveryTax());
        final BigDecimal totalAmount = subTotalAmount.add(tmp.getDeliveryCostAmount());

        final BigDecimal listTotalAmount = itemTotal.getListTotalAmount(); // list does not change

        return new TotalImpl(
                tmp.getListSubTotal(),
                tmp.getSaleSubTotal(),
                tmp.getNonSaleSubTotal(),
                tmp.getPriceSubTotal(),
                tmp.isOrderPromoApplied(),
                tmp.getAppliedOrderPromo(),
                subTotal,
                subTotalTax,
                subTotalAmount,
                tmp.getDeliveryListCost(),
                tmp.getDeliveryCost(),
                tmp.isDeliveryPromoApplied(),
                tmp.getAppliedDeliveryPromo(),
                tmp.getDeliveryTax(),
                tmp.getDeliveryCostAmount(),
                total,
                totalTax,
                listTotalAmount,
                totalAmount);

    }

    void applyShippingPromotions(final Customer customer,
                                 final MutableShoppingCart cart,
                                 final Total orderTotal,
                                 final PromotionContext promoCtx) {

        promoCtx.applyShippingPromo(customer, cart, orderTotal);

    }

    /** {@inheritDoc} */
    @Override
    public Total calculate(final CustomerOrder order, final CustomerOrderDelivery orderDelivery) {
        return calculate(orderDelivery);
    }

    /** {@inheritDoc} */
    @Override
    public Total calculate(final CustomerOrder order) {

        Total deliveriesTotal = new TotalImpl();
        if (CollectionUtils.isNotEmpty(order.getDelivery())) {
            for (final CustomerOrderDelivery delivery : order.getDelivery()) {

                final Total deliveryTotal = calculate(delivery);
                deliveriesTotal = deliveriesTotal.add(deliveryTotal);

            }
        } else {
            final Total deliveryTotal = calculateItemTotal(new ArrayList<>(order.getOrderDetail()));
            deliveriesTotal = deliveriesTotal.add(deliveryTotal);
        }

        final boolean orderPromoApplied = order.isPromoApplied();
        final String appliedOrderPromo = order.getAppliedPromo();
        final BigDecimal subTotal = order.getPrice();
        final BigDecimal subTotalTax = order.getGrossPrice().subtract(order.getNetPrice());
        final BigDecimal subTotalAmount = order.getGrossPrice();

        final BigDecimal total = subTotal.add(deliveriesTotal.getDeliveryCost());
        final BigDecimal totalTax = subTotalTax.add(deliveriesTotal.getDeliveryTax());
        final BigDecimal totalAmount = subTotalAmount.add(deliveriesTotal.getDeliveryCostAmount());

        final Total orderTotal = new TotalImpl(
                deliveriesTotal.getListSubTotal(),
                deliveriesTotal.getSaleSubTotal(),
                deliveriesTotal.getNonSaleSubTotal(),
                deliveriesTotal.getPriceSubTotal(),
                orderPromoApplied,
                appliedOrderPromo,
                subTotal,
                subTotalTax,
                subTotalAmount,
                deliveriesTotal.getDeliveryListCost(),
                deliveriesTotal.getDeliveryCost(),
                deliveriesTotal.isDeliveryPromoApplied(),
                deliveriesTotal.getAppliedDeliveryPromo(),
                deliveriesTotal.getDeliveryTax(),
                deliveriesTotal.getDeliveryCostAmount(),
                total,
                totalTax,
                deliveriesTotal.getListTotalAmount(),
                totalAmount
        );

        return orderTotal;
    }

    /*
     * Calculate by adding current delivery amount to rez
     */
    Total calculate(final CustomerOrderDelivery orderDelivery) {

        final Total itemTotal = calculateItemTotal(new ArrayList<>(orderDelivery.getDetail()));

        final BigDecimal deliveryTax = orderDelivery.getGrossPrice().subtract(orderDelivery.getNetPrice());
        final BigDecimal deliveryListAmount;
        if (orderDelivery.isTaxExclusiveOfPrice()) {
            final BigDecimal ratio = orderDelivery.getListPrice().divide(orderDelivery.getPrice(), 10, RoundingMode.HALF_UP);
            deliveryListAmount = orderDelivery.getListPrice().add(multiply(deliveryTax, ratio));
        } else {
            deliveryListAmount = orderDelivery.getListPrice();
        }

        final Total deliveryCost = new TotalImpl(
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                orderDelivery.getListPrice(),
                orderDelivery.getPrice(),
                orderDelivery.isPromoApplied(),
                orderDelivery.getAppliedPromo(),
                deliveryTax,
                orderDelivery.getGrossPrice(),
                orderDelivery.getPrice(),
                deliveryTax,
                deliveryListAmount,
                orderDelivery.getGrossPrice()
        );

        return itemTotal.add(deliveryCost);

    }

    /**
     * Calculate sub total of cart items.
     *
     * @param cart current shopping cart.
     *
     * @return cart sub total.
     */
    protected Total applyTaxToCartItemsAndCalculateItemTotal(final MutableShoppingCart cart) {

        final ShoppingContext ctx = cart.getShoppingContext();
        final String currency = cart.getCurrencyCode();

        final CartItemPrices prices = new CartItemPrices();
        final List<CartItem> items = cart.getCartItemList();

        if (items != null) {
            for (final CartItem item : cart.getCartItemList()) {
                if (!item.isGift() && !MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, item.getQty()) && item.getPrice() != null) {
                    final TaxProvider.Tax tax = taxProvider.determineTax(ctx.getShopCode(), currency, ctx.getCountryCode(), ctx.getStateCode(), item.getProductSkuCode());
                    final BigDecimal price = item.getPrice();

                    final MoneyUtils.Money money = calculateMoney(price, tax.getRate(), !tax.isExcluded());

                    final BigDecimal netPrice = money.getNet();
                    final BigDecimal grossPrice = money.getGross();

                    cart.setProductSkuTax(item.getProductSkuCode(), netPrice, grossPrice, tax.getRate(), tax.getCode(), tax.isExcluded());

                }

                prices.add(new CartItemPrices(item));

            }
        }

        return new TotalImpl(
                prices.listPrice,
                prices.salePrice,
                prices.nonSalePrice,
                prices.finalPrice,
                false,
                null,
                prices.finalPrice,
                prices.finalTax,
                prices.grossFinalPrice,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                prices.finalPrice,
                prices.finalTax,
                prices.grossListPrice, // This can be useful for showing total savings
                prices.grossFinalPrice
        );
    }


    /**
     * Calculate sub total of cart items.
     *
     * @param items items.
     *
     * @return cart sub total.
     */
    protected Total calculateItemTotal(final List<CartItem> items) {

        final CartItemPrices prices = new CartItemPrices();

        if (items != null) {
            for (final CartItem item : items) {

                prices.add(new CartItemPrices(item));

            }
        }

        return new TotalImpl(
                prices.listPrice,
                prices.salePrice,
                prices.nonSalePrice,
                prices.finalPrice,
                false,
                null,
                prices.finalPrice,
                prices.finalTax,
                prices.grossFinalPrice,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                prices.finalPrice,
                prices.finalTax,
                prices.grossListPrice, // This can be useful for showing total savings
                prices.grossFinalPrice
        );
    }


    /**
     * Calculate order total.
     *
     *
     * @param cart current shopping cart.
     * @param orderTotal running total
     *
     * @return cart order total.
     */
    protected Total applyTaxToShippingAndCalculateOrderTotal(final MutableShoppingCart cart, final Total orderTotal) {


        final ShoppingContext ctx = cart.getShoppingContext();
        final String currency = cart.getCurrencyCode();

        final CartItemPrices prices = new CartItemPrices();
        final List<CartItem> items = cart.getShippingList();
        boolean isShippingPromoApplied = false;
        final StringBuilder promoCodes = new StringBuilder();

        if (items != null) {
            for (final CartItem item : items) {

                final TaxProvider.Tax tax = taxProvider.determineTax(ctx.getShopCode(), currency, ctx.getCountryCode(), ctx.getStateCode(), item.getProductSkuCode());
                final BigDecimal price = item.getPrice();

                final MoneyUtils.Money money = calculateMoney(price, tax.getRate(), !tax.isExcluded());

                final BigDecimal netPrice = money.getNet();
                final BigDecimal grossPrice = money.getGross();

                final DeliveryBucket bucket = item.getDeliveryBucket();

                cart.setShippingTax(item.getProductSkuCode(), bucket, netPrice, grossPrice, tax.getRate(), tax.getCode(), tax.isExcluded());

                prices.add(new CartItemPrices(item));

                if (item.isPromoApplied()) {
                    isShippingPromoApplied = true;
                    if (promoCodes.length() > 0) {
                        promoCodes.append(',');
                    }
                    promoCodes.append(item.getAppliedPromo());
                }

            }
        }

        final BigDecimal salePriceRemove = prices.salePrice.negate();

        final Total draftDeliveryCostRemove = new TotalImpl(
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                salePriceRemove,
                salePriceRemove,
                false,
                null,
                Total.ZERO,
                salePriceRemove,
                salePriceRemove,
                Total.ZERO,
                salePriceRemove,
                salePriceRemove
        );

        final Total deliveryCost = new TotalImpl(
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                false,
                null,
                Total.ZERO,
                Total.ZERO,
                Total.ZERO,
                prices.listPrice,
                prices.finalPrice,
                isShippingPromoApplied,
                promoCodes.toString(),
                prices.finalTax,
                prices.grossFinalPrice,
                prices.finalPrice,
                prices.finalTax,
                prices.grossListPrice,
                prices.grossFinalPrice
        );

        final Total finalTotal = orderTotal.add(draftDeliveryCostRemove).add(deliveryCost);

        return finalTotal;
    }

    /**
     * Calculate delivery price.
     *
     * @param orderDelivery optional order delivery
     * @return delivery price.
     */
    BigDecimal calculateDelivery(final CustomerOrderDelivery orderDelivery) {
        if (orderDelivery != null && orderDelivery.getPrice() != null) {
            return orderDelivery.getPrice().setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
        }
        return Total.ZERO;
    }


    /**
     * Calculate subtotal tax by given sub total.
     *
     * @param money to calculate tax.
     * @param taxRate tax rate.
     * @param taxIncluded tax is included in price.
     *
     * @return tax.
     */
    MoneyUtils.Money calculateMoney(final BigDecimal money, final BigDecimal taxRate, final boolean taxIncluded) {

        return MoneyUtils.getMoney(money, taxRate, taxIncluded);

    }

    /**
     * Multiply with correct rounding.
     *
     * @param price price
     * @param qty quantity
     *
     * @return amount with correct scale
     */
    BigDecimal multiply(final BigDecimal price, final BigDecimal qty) {
        return price.multiply(qty).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    public class CartItemPrices {

        private BigDecimal listPrice = ZERO;
        private BigDecimal listTax = ZERO;
        private BigDecimal grossListPrice = ZERO;
        private BigDecimal salePrice = ZERO;
        private BigDecimal nonSalePrice = ZERO;
        private BigDecimal finalPrice = ZERO;
        private BigDecimal finalTax = ZERO;
        private BigDecimal netFinalPrice = ZERO;
        private BigDecimal grossFinalPrice = ZERO;

        private CartItemPrices() {
        }

        private CartItemPrices(final CartItem cartItem) {
            if (!MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, cartItem.getQty())) {
                if (cartItem.getListPrice() != null) {
                    this.listPrice = multiply(cartItem.getListPrice(), cartItem.getQty());
                    if (cartItem.getSalePrice() == null || MoneyUtils.isFirstEqualToSecond(cartItem.getListPrice(), cartItem.getSalePrice())) {
                        this.nonSalePrice = this.listPrice;
                    }
                }
                if (cartItem.getSalePrice() != null) {
                    this.salePrice = multiply(cartItem.getSalePrice(), cartItem.getQty());
                }
                if (cartItem.getPrice() != null) {
                    this.finalPrice = multiply(cartItem.getPrice(), cartItem.getQty());
                }
                if (cartItem.getNetPrice() != null && cartItem.getGrossPrice() != null) {
                    this.netFinalPrice = multiply(cartItem.getNetPrice(), cartItem.getQty());
                    this.grossFinalPrice = multiply(cartItem.getGrossPrice(), cartItem.getQty());
                    this.finalTax = this.grossFinalPrice.subtract(this.netFinalPrice);

                    final MoneyUtils.Money money = calculateMoney(this.listPrice, cartItem.getTaxRate(), !cartItem.isTaxExclusiveOfPrice());
                    this.listTax = money.getTax();
                    this.grossListPrice = money.getGross();
                }
            }
        }

        public void add(final CartItemPrices prices) {
            this.listPrice = this.listPrice.add(prices.listPrice);
            this.salePrice = this.salePrice.add(prices.salePrice);
            this.nonSalePrice = this.nonSalePrice.add(prices.nonSalePrice);
            this.finalPrice = this.finalPrice.add(prices.finalPrice);
            this.finalTax = this.finalTax.add(prices.finalTax);
            this.netFinalPrice = this.netFinalPrice.add(prices.netFinalPrice);
            this.grossFinalPrice = this.grossFinalPrice.add(prices.grossFinalPrice);
            this.grossListPrice = this.grossListPrice.add(prices.grossListPrice);
        }

        public BigDecimal getListPrice() {
            return listPrice;
        }

        public BigDecimal getListTax() {
            return listTax;
        }

        public BigDecimal getGrossListPrice() {
            return grossListPrice;
        }

        public BigDecimal getSalePrice() {
            return salePrice;
        }

        public BigDecimal getNonSalePrice() {
            return nonSalePrice;
        }

        public BigDecimal getFinalPrice() {
            return finalPrice;
        }

        public BigDecimal getFinalTax() {
            return finalTax;
        }

        public BigDecimal getNetFinalPrice() {
            return netFinalPrice;
        }

        public BigDecimal getGrossFinalPrice() {
            return grossFinalPrice;
        }
    }


}
