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

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy implements AmountCalculationStrategy {

    private static final BigDecimal HUNDRED = new BigDecimal("100.00");

    private final BigDecimal tax;
    private final boolean taxIncluded;

    /**
     * Construct default amount calculator with included tax.
     *
     * @param tax vat value in percents
     */
    public DefaultAmountCalculationStrategy(final BigDecimal tax, final boolean taxIncluded) {
        this.tax = tax.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        this.taxIncluded = taxIncluded;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getTax() {
        return tax;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaxIncluded() {
        return taxIncluded;
    }

    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final Collection<CustomerOrderDelivery> orderDelivery) {
        return calculate(shoppingContext, orderDelivery, false);
    }

        /**
        * {@inheritDoc}
        */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final Collection<CustomerOrderDelivery> orderDelivery,
                                             final boolean useListPrice) {
        final AmountCalculationResult rez = new AmountCalculationResultImpl();
        for (CustomerOrderDelivery delivery : orderDelivery) {

            calculate(shoppingContext, delivery, useListPrice, rez);

        }
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final CustomerOrderDelivery orderDelivery) {
        return calculate(shoppingContext, orderDelivery, false);
    }

        /**
        * {@inheritDoc}
        */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final CustomerOrderDelivery orderDelivery,
                                             final boolean useListPrice) {

        final AmountCalculationResult rez = new AmountCalculationResultImpl();

        calculate(shoppingContext, orderDelivery, useListPrice, rez);

        return rez;
    }

    /*
     * Calculate by adding current delivery amount to rez
     */
    void calculate(final ShoppingContext shoppingContext,
                   final CustomerOrderDelivery orderDelivery,
                   final boolean useListPrice,
                   final AmountCalculationResult rez) {

        rez.setSubTotal(rez.getSubTotal().add(calculateSubTotal(new ArrayList<CartItem>(orderDelivery.getDetail()), useListPrice)));
        rez.setSubTotalTax(calculateTax(rez.getSubTotal()));
        rez.setSubTotalAmount(calculateAmount(rez.getSubTotal(), rez.getSubTotalTax()));

        rez.setDelivery(rez.getDelivery().add(calculateDelivery(orderDelivery)));
        rez.setDeliveryTax(calculateTax(rez.getDelivery()));
        rez.setDeliveryAmount(calculateAmount(rez.getDelivery(), rez.getDeliveryTax()));

        rez.setTotal(rez.getSubTotal().add(rez.getDelivery()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
        rez.setTotalTax(calculateTax(rez.getTotal()));
        rez.setTotalAmount(calculateAmount(rez.getTotal(), rez.getTotalTax()));
    }

    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @return cart sub total.
     */
    public BigDecimal calculateSubTotal(final List<CartItem> items) {
        return calculateSubTotal(items, false);
    }

    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @param useListPrice if true then this calculation should be done using list price of items,
     *                     otherwise sale price should be used.
     * @return cart sub total.
     */
    public BigDecimal calculateSubTotal(final List<CartItem> items, final boolean useListPrice) {
        BigDecimal cartSubTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        if (items != null) {
            for (CartItem cartItem : items) {
                final BigDecimal price = useListPrice ? cartItem.getListPrice() : cartItem.getPrice();
                if (price != null && cartItem.getQty() != null) {
                    cartSubTotal = cartSubTotal.add(price.multiply(cartItem.getQty()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        return cartSubTotal;
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
     * @param money to claculate tax.
     * @return tax.
     */
    BigDecimal calculateTax(final BigDecimal money) {
        if (money == null) {
            return BigDecimal.ZERO;
        }
        if (taxIncluded) {
            return money.multiply(tax)
                    .divide(tax.add(HUNDRED), Constants.DEFAULT_SCALE)
                    .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
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


}
