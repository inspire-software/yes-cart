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

}
