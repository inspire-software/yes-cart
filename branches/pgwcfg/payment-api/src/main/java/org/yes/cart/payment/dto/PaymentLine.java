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

package org.yes.cart.payment.dto;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represent item line in payment.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public interface PaymentLine extends Serializable {

    /**
     * Get sku name.
     *
     * @return sku name.
     */
    String getSkuName();

    /**
     * Set sku name.
     *
     * @param skuName sku name.
     */
    void setSkuName(String skuName);

    /**
     * Get sku code.
     *
     * @return sku code.
     */
    String getSkuCode();

    /**
     * Set  sku code.
     *
     * @param skuCode sku code.
     */
    void setSkuCode(String skuCode);

    /**
     * Get quantity.
     *
     * @return quantity.
     */
    BigDecimal getQuantity();

    /**
     * Set quantity.
     *
     * @param quantity quantity.
     */
    void setQuantity(BigDecimal quantity);

    /**
     * Get tax.
     *
     * @return tax.
     */
    BigDecimal getTaxAmount();

    /**
     * Set tax.
     *
     * @param taxAmount tax.
     */
    void setTaxAmount(BigDecimal taxAmount);

    /**
     * Get price.
     *
     * @return price.
     */
    BigDecimal getUnitPrice();

    /**
     * Set price.
     *
     * @param unitPrice price.
     */
    void setUnitPrice(BigDecimal unitPrice);


    /**
     * Is item shipment.
     * @return true if item is shipment
     */
    public boolean isShipment();

    /**
     * Set shpipment flag
     * @param shipment shipment flag
     */
    public void setShipment(boolean shipment);

}
