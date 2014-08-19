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
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 3/12/11
 * Time: 14:44
 */
public class TotalImpl implements Total {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private BigDecimal listSubTotal;
    private BigDecimal saleSubTotal;
    private BigDecimal nonSaleSubTotal;
    private BigDecimal priceSubTotal;
    private boolean orderPromoApplied;
    private String appliedOrderPromo;
    private BigDecimal subTotal;
    private BigDecimal subTotalTax;
    private BigDecimal subTotalAmount;

    private BigDecimal deliveryListCost;
    private BigDecimal deliveryCost;
    private boolean deliveryPromoApplied;
    private String appliedDeliveryPromo;
    private BigDecimal deliveryTax;
    private BigDecimal deliveryCostAmount;

    private BigDecimal total;
    private BigDecimal totalTax;
    private BigDecimal listTotalAmount;
    private BigDecimal totalAmount;


    public TotalImpl() {
        this(ZERO, ZERO, ZERO, ZERO, false, null, ZERO, ZERO, ZERO, ZERO, ZERO, false, null, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO);
    }

    public TotalImpl(final BigDecimal listSubTotal,
                     final BigDecimal saleSubTotal,
                     final BigDecimal nonSaleSubTotal,
                     final BigDecimal priceSubTotal,
                     final boolean orderPromoApplied,
                     final String appliedOrderPromo,
                     final BigDecimal subTotal,
                     final BigDecimal subTotalTax,
                     final BigDecimal subTotalAmount,
                     final BigDecimal deliveryListCost,
                     final BigDecimal deliveryCost,
                     final boolean deliveryPromoApplied,
                     final String appliedDeliveryPromo,
                     final BigDecimal deliveryTax,
                     final BigDecimal deliveryCostAmount,
                     final BigDecimal total,
                     final BigDecimal totalTax,
                     final BigDecimal listTotalAmount,
                     final BigDecimal totalAmount) {

        this.listSubTotal = listSubTotal;
        this.saleSubTotal = saleSubTotal;
        this.nonSaleSubTotal = nonSaleSubTotal;
        this.priceSubTotal = priceSubTotal;
        this.orderPromoApplied = orderPromoApplied;
        this.appliedOrderPromo = appliedOrderPromo;
        this.subTotal = subTotal;
        this.subTotalTax = subTotalTax;
        this.subTotalAmount = subTotalAmount;
        this.deliveryListCost = deliveryListCost;
        this.deliveryCost = deliveryCost;
        this.deliveryPromoApplied = deliveryPromoApplied;
        this.appliedDeliveryPromo = appliedDeliveryPromo;
        this.deliveryTax = deliveryTax;
        this.deliveryCostAmount = deliveryCostAmount;
        this.total = total;
        this.totalTax = totalTax;
        this.listTotalAmount = listTotalAmount;
        this.totalAmount = totalAmount;

    }

    /** {@inheritDoc} */
    public BigDecimal getListSubTotal() {
        return listSubTotal;
    }

    /** {@inheritDoc} */
    public BigDecimal getSaleSubTotal() {
        return saleSubTotal;
    }

    /** {@inheritDoc} */
    public BigDecimal getNonSaleSubTotal() {
        return nonSaleSubTotal;
    }

    /** {@inheritDoc} */
    public BigDecimal getPriceSubTotal() {
        return priceSubTotal;
    }

    /** {@inheritDoc} */
    public boolean isOrderPromoApplied() {
        return orderPromoApplied;
    }

    /** {@inheritDoc} */
    public String getAppliedOrderPromo() {
        return appliedOrderPromo;
    }

    /** {@inheritDoc} */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /** {@inheritDoc} */
    public BigDecimal getSubTotalTax() {
        return subTotalTax;
    }

    /** {@inheritDoc} */
    public BigDecimal getSubTotalAmount() {
        return subTotalAmount;
    }

    /** {@inheritDoc} */
    public BigDecimal getDeliveryListCost() {
        return deliveryListCost;
    }

    /** {@inheritDoc} */
    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    /** {@inheritDoc} */
    public boolean isDeliveryPromoApplied() {
        return deliveryPromoApplied;
    }

    /** {@inheritDoc} */
    public String getAppliedDeliveryPromo() {
        return appliedDeliveryPromo;
    }

    /** {@inheritDoc} */
    public BigDecimal getDeliveryTax() {
        return deliveryTax;
    }

    /** {@inheritDoc} */
    public BigDecimal getDeliveryCostAmount() {
        return deliveryCostAmount;
    }

    /** {@inheritDoc} */
    public BigDecimal getTotal() {
        return total;
    }

    /** {@inheritDoc} */
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    /** {@inheritDoc} */
    public BigDecimal getListTotalAmount() {
        return listTotalAmount;
    }

    /** {@inheritDoc} */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /** {@inheritDoc} */
    public Total add(final Total summand) {
        final TotalImpl sum = new TotalImpl(
                listSubTotal,
                saleSubTotal,
                nonSaleSubTotal,
                priceSubTotal,
                orderPromoApplied,
                appliedOrderPromo,
                subTotal,
                subTotalTax,
                subTotalAmount,
                deliveryListCost,
                deliveryCost,
                deliveryPromoApplied,
                appliedDeliveryPromo,
                deliveryTax,
                deliveryCostAmount,
                total,
                totalTax,
                listTotalAmount,
                totalAmount);

        sum.listSubTotal = sum.listSubTotal.add(summand.getListSubTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.saleSubTotal = sum.saleSubTotal.add(summand.getSaleSubTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.nonSaleSubTotal = sum.nonSaleSubTotal.add(summand.getNonSaleSubTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.priceSubTotal = sum.priceSubTotal.add(summand.getPriceSubTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.orderPromoApplied = sum.orderPromoApplied || summand.isOrderPromoApplied();
        sum.appliedOrderPromo = mergePromo(sum.appliedOrderPromo, summand.getAppliedOrderPromo());
        sum.subTotal = sum.subTotal.add(summand.getSubTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.subTotalTax = sum.subTotalTax.add(summand.getSubTotalTax()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.subTotalAmount = sum.subTotalAmount.add(summand.getSubTotalAmount()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.deliveryListCost = sum.deliveryListCost.add(summand.getDeliveryListCost()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.deliveryCost = sum.deliveryCost.add(summand.getDeliveryCost()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.deliveryPromoApplied = sum.deliveryPromoApplied || summand.isDeliveryPromoApplied();
        sum.appliedDeliveryPromo = mergePromo(sum.appliedDeliveryPromo, summand.getAppliedDeliveryPromo());
        sum.deliveryTax = sum.deliveryTax.add(summand.getDeliveryTax()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.deliveryCostAmount = sum.deliveryCostAmount.add(summand.getDeliveryCostAmount()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.total = sum.total.add(summand.getTotal()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.totalTax = sum.totalTax.add(summand.getTotalTax()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.listTotalAmount = sum.listTotalAmount.add(summand.getListTotalAmount()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        sum.totalAmount = sum.totalAmount.add(summand.getTotalAmount()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        return sum;
    }

    String mergePromo(String first, String second) {
        if (StringUtils.isBlank(first) && StringUtils.isBlank(second)) {
            return null;
        }
        final Set<String> unique = new HashSet<String>();
        if (StringUtils.isNotBlank(first)) {
            for (final String item : StringUtils.split(first, ',')) {
                if (StringUtils.isNotBlank(item)) {
                    unique.add(item);
                }
            }
        }
        if (StringUtils.isNotBlank(second)) {
            for (final String item : StringUtils.split(second, ',')) {
                if (StringUtils.isNotBlank(item)) {
                    unique.add(item);
                }
            }
        }
        return StringUtils.join(unique, ',');
    }
}
