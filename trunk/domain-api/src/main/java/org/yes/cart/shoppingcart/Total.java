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

package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * Simple DTO class to hold amount calculation result.
 * This interface follows immutable object pattern such as BigDecimal.
 * There is no reason why we should tamper with calculation result
 * the calculation should be correct.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 3-Dec-11
 * Time: 12:05
 */
public interface Total extends Serializable {

    /**
     * Get subtotal of all items list price.
     * This total shows the catalog total with no discounts.
     *
     * @return list price subtotal.
     */
    public BigDecimal getListSubTotal();

    /**
     * Get subtotal of all items sale price.
     * Sale sub total shows total of items price using special
     * sale prices before any promotions applied.
     *
     * @return sale price subtotal.
     */
    public BigDecimal getSaleSubTotal();

    /**
     * Get subtotal of all items final price.
     * Final item price total inclusive of all sale prices and
     * item level promotions.
     *
     * @return final price subtotal.
     */
    public BigDecimal getPriceSubTotal();

    /**
     * Returns true if promotions have been applied to
     * grand total.
     *
     * @return true if promotions have been applied
     */
    boolean isOrderPromoApplied();

    /**
     * Comma separated list of promotion codes that have been applied
     * for this total.
     *
     * @return comma separated
     */
    String getAppliedOrderPromo();

    /**
     * Get cart subtotal. Final sub total including getPriceSubTotal() and
     * any additional order level promotions applied.
     *
     * @return cart subtotal.
     */
    public BigDecimal getSubTotal();

     /**
     * Get sub total tax.
      *
     * @return sub total tax
     */
    public BigDecimal getSubTotalTax();

    /**
     * Get sub total amount including tax.
     *
     * @return  sub total amount including tax.
     */
    public BigDecimal getSubTotalAmount();

    /**
     * Get delivery sale cost. The cost of delivery as it
     * is calculated without any discounts applied.
     *
     * @return delivery amount.
     */
    public BigDecimal getDeliveryListCost();

    /**
     * Get delivery cost. Cost of delivery less shipping promotions.
     *
     * @return delivery amount.
     */
    public BigDecimal getDeliveryCost();

    /**
     * Returns true if promotions have been applied to this
     * delivery.
     *
     * @return true if promotions have been applied
     */
    boolean isDeliveryPromoApplied();

    /**
     * Comma separated list of promotion codes that have been applied
     * for this order.
     *
     * @return comma separated
     */
    String getAppliedDeliveryPromo();

    /**
     * Get tax for delivery.
     *
     * @return delivery tax.
     */
    public BigDecimal getDeliveryTax();

    /**
     * Get delivery amount including tax.
     *
     * @return delivery amount including tax
     */
    public BigDecimal getDeliveryCostAmount();

    /**
     * Get total = sub total + delivery cost
     *
     * @return total.
     */
    public BigDecimal getTotal();

    /**
     * Get total tax.
     *
     * @return total tax.
     */
    public BigDecimal getTotalTax();

    /**
     * Get total amount as it would be if we used
     * catalog prices with no promotions.
     *
     * total = sub total list price + delivery sale cost
     *
     * @return  total amount.
     */
    public BigDecimal getListTotalAmount();

    /**
     * Get total amount to be paid.
     *
     * @return  total amount.
     */
    public BigDecimal getTotalAmount();

    /**
     * Add current total to given one to create a new total instance
     * which is the sum of the two summands.
     *
     * @param summand total to add
     *
     * @return new instance which is the sum
     */
    Total add(Total summand);

}
