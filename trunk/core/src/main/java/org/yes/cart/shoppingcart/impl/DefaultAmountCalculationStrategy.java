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

import com.rsa.certj.cert.extensions.BuiltInDomainDefinedAttributes;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingContext;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy implements AmountCalculationStrategy {

    private final BigDecimal tax;

    /**
     * Construct default amount calculator with included tax.
     *
     * @param tax vat value in percents
     */
    public DefaultAmountCalculationStrategy(final BigDecimal tax) {
        this.tax = tax.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final Collection<CustomerOrderDelivery> orderDelivery) {
        final AmountCalculationResult rez = new AmountCalculationResultImpl();
        for (CustomerOrderDelivery delivery : orderDelivery) {
            final AmountCalculationResult singleDeviveryRez = calculate(shoppingContext, delivery);

            rez.setSubTotal(rez.getSubTotal().add(singleDeviveryRez.getSubTotal()));
            rez.setSubTotalTax(rez.getSubTotalTax().add(singleDeviveryRez.getSubTotalTax()));
            rez.setSubTotalAmount(rez.getSubTotalAmount().add(singleDeviveryRez.getSubTotalAmount()));

            rez.setDelivery(rez.getDelivery().add(singleDeviveryRez.getDelivery()));
            rez.setDeliveryTax(rez.getDeliveryTax().add(singleDeviveryRez.getDeliveryTax()));
            rez.setDeliveryAmount(rez.getDeliveryAmount().add(singleDeviveryRez.getDeliveryAmount()));

            rez.setTotal(rez.getTotal().add(singleDeviveryRez.getTotal()));
            rez.setTotalTax(rez.getTotalTax().add(singleDeviveryRez.getTotalTax()));
            rez.setTotalAmount(rez.getTotalAmount().add(singleDeviveryRez.getTotalAmount()));

        }
        return rez;
    }


    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final CustomerOrderDelivery orderDelivery) {

        final AmountCalculationResult rez = new AmountCalculationResultImpl();



        rez.setSubTotal(calculateSubTotal(orderDelivery.getDetail()));
        rez.setSubTotalTax(calculateTax(rez.getSubTotal()));
        rez.setSubTotalAmount(calculateAmount(rez.getSubTotal(), rez.getSubTotalTax()));

        rez.setDelivery(calculateDelievery(orderDelivery));
        rez.setDeliveryTax(calculateTax(rez.getDelivery()));
        rez.setDeliveryAmount(calculateAmount(rez.getDelivery(), rez.getDeliveryTax()));

        rez.setTotal(rez.getSubTotal().add(rez.getDelivery()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
        rez.setTotalTax(calculateTax(rez.getTotal()));
        rez.setTotalAmount(calculateAmount(rez.getTotal(), rez.getTotalTax()));

        return rez;
    }

    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @return cart sub total.
     */
     BigDecimal calculateSubTotal(final Collection<CustomerOrderDeliveryDet> items) {
        BigDecimal cartSubTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        if (items != null) {
            for (CustomerOrderDeliveryDet cartItem : items) {
                final BigDecimal price = cartItem.getPrice();
                cartSubTotal = cartSubTotal.add(price.multiply(cartItem.getQty()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
            }
        }
        return cartSubTotal;
    }

    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @return cart sub total.
     */
    public BigDecimal calculateSubTotal(final List<CartItem> items) {
        BigDecimal cartSubTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        if (items != null) {
            for (CartItem cartItem : items) {
                final BigDecimal price = cartItem.getPrice();
                cartSubTotal = cartSubTotal.add(price.multiply(cartItem.getQty()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
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
    BigDecimal calculateDelievery(final CustomerOrderDelivery orderDelivery) {
        if (orderDelivery != null) {
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
        return money.multiply(tax)
                .divide(tax.add(new BigDecimal("100.00")), Constants.DEFAULT_SCALE)
                .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Calulate sub total amount.
     *
     * @param money given sub total.
     * @param tax   sub total tax.
     * @return cart sub total amount.
     */
    BigDecimal calculateAmount(final BigDecimal money, final BigDecimal tax) {
        return money; // tax already included;
    }


}
