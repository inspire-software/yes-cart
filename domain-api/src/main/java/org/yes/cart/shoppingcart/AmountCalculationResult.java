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

import java.math.BigDecimal;

/**
 *
 * Simple DTO class to hold amount calculation result.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 3-Dec-11
 * Time: 12:05
 */
public interface AmountCalculationResult {

    /**
     * Get cart subtotal
     * @return cart subtotal.
     */
    public BigDecimal getSubTotal();


    /**
     * Set sub total.
     * @param subTotal sub total amount .
     */
    public void setSubTotal(BigDecimal subTotal);

     /**
     * Get sub total tax.
     * @return sub total tax
     */
    public BigDecimal getSubTotalTax();

     /**
     * Get sub total tax.
     * @param subTotalTax subtotal tax
     */
    public void setSubTotalTax(BigDecimal subTotalTax);

    /**
     * Get sub total amount including tax.
     * @return  sub total amount including tax.
     */
    public BigDecimal getSubTotalAmount();

    /**
     * Set sub total amount
     * @param subTotalAmount  sub total amount including tax.
     */
    public void setSubTotalAmount(BigDecimal subTotalAmount);

    /**
     * Get delivery amount.
     * @return delivery amount.
     */
    public BigDecimal getDelivery();

    /**
     * Set delivery amount.
     * @param delivery amount
     */
    public void setDelivery(BigDecimal delivery);

    /**
     * Get tax for delivery.
     * @return delivery tax.
     */
    public BigDecimal getDeliveryTax();

    /**
     *
     * @param deliveryTax set delivery tax
     */
    public void setDeliveryTax(BigDecimal deliveryTax);

    /**
     * Get delivery amount including tax.
     * @return delivery amount including tax
     */
    public BigDecimal getDeliveryAmount();

    /**
     * Set delivery amount including tax.
     * @param deliveryAmount delivery amount including tax
     */
    public void setDeliveryAmount(BigDecimal deliveryAmount);

    /**
     * Get total.
     * @return total.
     */
    public BigDecimal getTotal();

    /**
     * Set total.
     * @param total total to set.
     */
    public void setTotal(BigDecimal total);

    /**
     * Get total tax.
     * @return total tax.
     */
    public BigDecimal getTotalTax();

    /**
     * Set total tax.
     * @param totalTax tax to set.
     */
    public void setTotalTax(BigDecimal totalTax);

    /**
     * Get total amount.
     * @return  total amount.
     */
    public BigDecimal getTotalAmount();

    /**
     * Set  total amount.
     * @param totalAmount  total amount.
     */
    public void setTotalAmount(BigDecimal totalAmount);

}
