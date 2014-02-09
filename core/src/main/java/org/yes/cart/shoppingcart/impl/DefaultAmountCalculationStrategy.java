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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
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

    private final BigDecimal tax;
    private final boolean taxIncluded;

    private final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy;
    private final PromotionContextFactory promotionContextFactory;
    private final CustomerService customerService;

    /**
     * Construct default amount calculator with included tax.
     *
     * @param tax vat value in percents
     * @param taxIncluded if true taxes assumed to be included in price, otherwise they are added to totals
     * @param deliveryCostCalculationStrategy delivery cost calculation strategy
     * @param promotionContextFactory promotion context
     * @param customerService customer service
     */
    public DefaultAmountCalculationStrategy(final BigDecimal tax,
                                            final boolean taxIncluded,
                                            final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy,
                                            final PromotionContextFactory promotionContextFactory,
                                            final CustomerService customerService) {

        this.tax = tax.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        this.taxIncluded = taxIncluded;

        this.deliveryCostCalculationStrategy = deliveryCostCalculationStrategy;
        this.promotionContextFactory = promotionContextFactory;
        this.customerService = customerService;
    }

    /** {@inheritDoc} */
    public Total calculate(final ShoppingCart cart) {

        final Customer customer;
        if (StringUtils.isNotBlank(cart.getCustomerEmail())) {
            customer = customerService.getCustomerByEmail(cart.getCustomerEmail());
        } else {
            customer = null;
        }

        final PromotionContext promoCtx = promotionContextFactory.getInstance(cart.getShoppingContext().getShopCode(), cart.getCurrencyCode());

        // 1. Apply all item level promotions as the first step
        applyItemLevelPromotions(customer, cart, promoCtx);

        // 2. Calculate current subtotal (including item promotions)
        final CartItemPrices sub = calculateSubTotal(cart.getCartItemList());

        // 3. Calculate delivery costs
        final BigDecimal deliveryListCost = deliveryCostCalculationStrategy.getDeliveryPrice(cart);
        final boolean shippingPromoApplied = false;
        final String appliedShippingPromo = null;
        final BigDecimal deliveryCost = deliveryListCost;
        final BigDecimal deliveryTax = calculateTax(deliveryCost);
        final BigDecimal deliveryCostAmount = calculateAmount(deliveryCost, deliveryTax);

        // 4. Create dummy total for items
        final boolean orderPromoApplied = false;
        final String appliedOrderPromo = null;
        final BigDecimal subTotalList = sub.listPrice;
        final BigDecimal subTotalTaxList = calculateTax(subTotalList);
        final BigDecimal subTotalAmountList = calculateAmount(subTotalList, subTotalTaxList);
        final BigDecimal subTotal = sub.finalPrice;
        final BigDecimal subTotalTax = calculateTax(subTotal);
        final BigDecimal subTotalAmount = calculateAmount(subTotal, subTotalTax);

        final BigDecimal total = subTotal.add(deliveryCost);
        final BigDecimal totalTax = calculateTax(total);
        final BigDecimal totalAmount = calculateAmount(total, totalTax);
        final BigDecimal listTotalAmount = subTotalAmountList.add(
                calculateAmount(deliveryListCost, calculateTax(deliveryListCost)));


        final Total itemTotal = new TotalImpl(
                sub.listPrice,
                sub.salePrice,
                sub.nonSalePrice,
                sub.finalPrice,
                orderPromoApplied,
                appliedOrderPromo,
                subTotal,
                subTotalTax,
                subTotalAmount,
                deliveryListCost,
                deliveryCost,
                shippingPromoApplied,
                appliedShippingPromo,
                deliveryTax,
                deliveryCostAmount,
                total,
                totalTax,
                listTotalAmount, // This can be useful for showing total savings
                totalAmount
        );

        // 5. Use current cart + dummy item total to calculate order level promotions
        final Total orderTotal = applyOrderLevelPromotions(customer, cart, itemTotal, promoCtx);


        // 6. At this stage we have reliable total for the whole order so we can
        //    calculate shipping promotions, which would be calculation for single
        //    delivery. If we have multiple we can reuse this calculation.
        final Total finalTotal = applyShippingPromotions(customer, cart, orderTotal, promoCtx);

        return finalTotal;

    }

    void applyItemLevelPromotions(final Customer customer,
                                  final ShoppingCart cart,
                                  final PromotionContext promoCtx) {

        promoCtx.applyItemPromo(customer, cart);

    }

    Total applyOrderLevelPromotions(final Customer customer,
                                    final ShoppingCart cart,
                                    final Total itemTotal,
                                    final PromotionContext promoCtx) {

        final Total tmp = promoCtx.applyOrderPromo(customer, cart, itemTotal);

        final BigDecimal subTotal = tmp.getSubTotal();
        final BigDecimal subTotalTax = calculateTax(subTotal);
        final BigDecimal subTotalAmount = calculateAmount(subTotal, subTotalTax);

        final BigDecimal total = subTotal.add(tmp.getDeliveryCost());
        final BigDecimal totalTax = calculateTax(total);
        final BigDecimal totalAmount = calculateAmount(total, totalTax);

        final BigDecimal subTotalList = tmp.getListSubTotal();
        final BigDecimal subTotalTaxList = calculateTax(subTotalList);
        final BigDecimal subTotalAmountList = calculateAmount(subTotalList, subTotalTaxList);

        final BigDecimal listTotalAmount = subTotalAmountList.add(
                calculateAmount(tmp.getDeliveryListCost(), calculateTax(tmp.getDeliveryListCost())));

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

    Total applyShippingPromotions(final Customer customer,
                                  final ShoppingCart cart,
                                  final Total orderTotal,
                                  final PromotionContext promoCtx) {

        final Total tmp = promoCtx.applyShippingPromo(customer, cart, orderTotal);

        final BigDecimal deliveryListCost = tmp.getDeliveryListCost();
        final boolean shippingPromoApplied = tmp.isDeliveryPromoApplied();
        final String appliedShippingPromo = tmp.getAppliedDeliveryPromo();
        final BigDecimal deliveryCost = tmp.getDeliveryCost();
        final BigDecimal deliveryTax = calculateTax(deliveryCost);
        final BigDecimal deliveryCostAmount = calculateAmount(deliveryCost, deliveryTax);

        final BigDecimal total = tmp.getSubTotal().add(deliveryCost);
        final BigDecimal totalTax = calculateTax(total);
        final BigDecimal totalAmount = calculateAmount(total, totalTax);

        return new TotalImpl(
                tmp.getListSubTotal(),
                tmp.getSaleSubTotal(),
                tmp.getNonSaleSubTotal(),
                tmp.getPriceSubTotal(),
                tmp.isOrderPromoApplied(),
                tmp.getAppliedOrderPromo(),
                tmp.getSubTotal(),
                tmp.getSubTotalTax(),
                tmp.getSubTotalAmount(),
                deliveryListCost,
                deliveryCost,
                shippingPromoApplied,
                appliedShippingPromo,
                deliveryTax,
                deliveryCostAmount,
                total,
                totalTax,
                tmp.getListTotalAmount(),
                totalAmount);

    }

    /** {@inheritDoc} */
    public Total calculate(final CustomerOrder order, final CustomerOrderDelivery orderDelivery) {
        return calculate(orderDelivery);
    }

    /** {@inheritDoc} */
    public Total calculate(final CustomerOrder order) {
        Total deliveriesTotal = new TotalImpl();
        for (final CustomerOrderDelivery delivery : order.getDelivery()) {

            final Total deliveryTotal = calculate(delivery);
            deliveriesTotal = deliveriesTotal.add(deliveryTotal);

        }

        final boolean orderPromoApplied = order.isPromoApplied();
        final String appliedOrderPromo = order.getAppliedPromo();
        final BigDecimal subTotalList = deliveriesTotal.getListSubTotal();
        final BigDecimal subTotalTaxList = calculateTax(subTotalList);
        final BigDecimal subTotalAmountList = calculateAmount(subTotalList, subTotalTaxList);
        final BigDecimal subTotal = order.getPrice();
        final BigDecimal subTotalTax = calculateTax(subTotal);
        final BigDecimal subTotalAmount = calculateAmount(subTotal, subTotalTax);

        final BigDecimal total = subTotal.add(deliveriesTotal.getDeliveryCost());
        final BigDecimal totalTax = subTotalTax.add(deliveriesTotal.getDeliveryTax());
        final BigDecimal totalAmount = subTotalAmount.add(deliveriesTotal.getDeliveryCostAmount());
        final BigDecimal listTotalAmount = subTotalAmountList.add(
                calculateAmount(deliveriesTotal.getDeliveryListCost(), calculateTax(deliveriesTotal.getDeliveryListCost())));

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
                listTotalAmount,
                totalAmount
        );

        return orderTotal;
    }

    /*
     * Calculate by adding current delivery amount to rez
     */
    Total calculate(final CustomerOrderDelivery orderDelivery) {

        final CartItemPrices sub = calculateSubTotal(new ArrayList<CartItem>(orderDelivery.getDetail()));

        final BigDecimal deliveryListCost = orderDelivery.getListPrice();
        final BigDecimal deliveryCost = orderDelivery.getPrice();
        final boolean shippingPromoApplied = orderDelivery.isPromoApplied();
        final String appliedShippingPromo = orderDelivery.getAppliedPromo();
        final BigDecimal deliveryTax = calculateTax(deliveryCost);
        final BigDecimal deliveryCostAmount = calculateAmount(deliveryCost, deliveryTax);

        final boolean orderPromoApplied = false;
        final String appliedOrderPromo = null;
        final BigDecimal subTotalList = sub.listPrice;
        final BigDecimal subTotalTaxList = calculateTax(subTotalList);
        final BigDecimal subTotalAmountList = calculateAmount(subTotalList, subTotalTaxList);
        final BigDecimal subTotal = sub.finalPrice;
        final BigDecimal subTotalTax = calculateTax(subTotal);
        final BigDecimal subTotalAmount = calculateAmount(subTotal, subTotalTax);

        final BigDecimal total = subTotal.add(deliveryCost);
        final BigDecimal totalList = subTotalList.add(deliveryListCost);
        final BigDecimal totalTax = calculateTax(total);
        final BigDecimal totalAmountList = calculateAmount(totalList, calculateTax(totalList));
        final BigDecimal totalAmount = calculateAmount(total, totalTax);

        return new TotalImpl(
                sub.listPrice,
                sub.salePrice,
                sub.nonSalePrice,
                sub.finalPrice,
                orderPromoApplied,
                appliedOrderPromo,
                subTotal,
                subTotalTax,
                subTotalAmount,
                deliveryListCost,
                deliveryCost,
                shippingPromoApplied,
                appliedShippingPromo,
                deliveryTax,
                deliveryCostAmount,
                total,
                totalTax,
                totalAmountList,
                totalAmount
        );

    }

    /**
     * Calculate sub total of cart items.
     *
     * @param items given list of cart items.
     *
     * @return cart sub total.
     */
    protected CartItemPrices calculateSubTotal(final List<CartItem> items) {
        final CartItemPrices prices = new CartItemPrices();
        if (items != null) {
            for (CartItem cartItem : items) {
                // TODO: YC-303 apply item level promotions
                final CartItemPrices price = new CartItemPrices(cartItem);
                prices.add(price);
            }
        }
        return prices;
    }

    /**
     * Calculate delivery price.
     *
     * @param orderDelivery optional order delivery
     * @return delivery price.
     */
    BigDecimal calculateDelivery(final CustomerOrderDelivery orderDelivery) {
        if (orderDelivery != null && orderDelivery.getPrice() != null) {
            return orderDelivery.getPrice().setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Calculate subtotal tax by given sub total.
     *
     * @param money to calculate tax.
     * @return tax.
     */
    BigDecimal calculateTax(final BigDecimal money) {
        if (money == null) {
            return BigDecimal.ZERO;
        }
        if (taxIncluded) {
            // vat = item * vatRate / (vat + 100)
            return money.multiply(tax)
                    .divide(tax.add(HUNDRED), Constants.DEFAULT_SCALE)
                    .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
        // tax = item * taxRate / 100
        return money.multiply(tax).divide(HUNDRED, Constants.DEFAULT_SCALE)
                .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Calculate sub total amount.
     *
     * @param money given sub total.
     * @param tax   sub total tax.
     * @return cart sub total amount.
     */
    BigDecimal calculateAmount(final BigDecimal money, final BigDecimal tax) {
        if (money == null) {
            return BigDecimal.ZERO;
        }
        if (taxIncluded || tax == null) {
            return money; // tax already included;
        }
        return money.add(tax);
    }

    public static class CartItemPrices {

        private BigDecimal listPrice = ZERO;
        private BigDecimal salePrice = ZERO;
        private BigDecimal nonSalePrice = ZERO;
        private BigDecimal finalPrice = ZERO;

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
            }
        }

        private BigDecimal multiply(final BigDecimal price, final BigDecimal qty) {
            return price.multiply(qty).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        }

        public void add(final CartItemPrices prices) {
            this.listPrice = this.listPrice.add(prices.listPrice);
            this.salePrice = this.salePrice.add(prices.salePrice);
            this.nonSalePrice = this.nonSalePrice.add(prices.nonSalePrice);
            this.finalPrice = this.finalPrice.add(prices.finalPrice);
        }

        public BigDecimal getListPrice() {
            return listPrice;
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
    }


}
