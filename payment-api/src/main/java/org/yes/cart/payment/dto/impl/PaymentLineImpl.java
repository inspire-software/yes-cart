package org.yes.cart.payment.dto.impl;


import org.yes.cart.payment.dto.PaymentLine;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public class PaymentLineImpl implements PaymentLine {

    private String skuName;
    private String skuCode;
    private BigDecimal quantity;
    private BigDecimal unitPrice;

    private BigDecimal taxAmount;

    private boolean shipment;


    /**
     * Construct payment line.
     *
     * @param skuName   product sku name
     * @param skuCode   sku code
     * @param quantity  qty
     * @param unitPrice price of one unit
     * @param taxAmount taxes if any
     */
    public PaymentLineImpl(final String skuCode,
                           final String skuName,
                           final BigDecimal quantity,
                           final BigDecimal unitPrice,
                           final BigDecimal taxAmount) {
        this.skuName = skuName;
        this.skuCode = skuCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxAmount = taxAmount;
        shipment = false;
    }

    /**
     * Construct payment line.
     *
     * @param skuName   product sku name
     * @param skuCode   sku code
     * @param quantity  qty
     * @param unitPrice price of one unit
     * @param taxAmount taxes if any
     * @param shipment  shipment flag
     */
    public PaymentLineImpl(final String skuCode,
                           final String skuName,
                           final BigDecimal quantity,
                           final BigDecimal unitPrice,
                           final BigDecimal taxAmount,
                           final boolean shipment) {
        this.skuName = skuName;
        this.skuCode = skuCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxAmount = taxAmount;
        this.shipment = shipment;
    }



    /**
     * Get sku name.
     *
     * @return sku name.
     */
    public String getSkuName() {
        return skuName;
    }

    /**
     * Set sku name.
     *
     * @param skuName sku name.
     */
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /**
     * Get sku code.
     *
     * @return sku code.
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * Set  sku code.
     *
     * @param skuCode sku code.
     */
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /**
     * Get quantity.
     *
     * @return quantity.
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Set quantity.
     *
     * @param quantity quantity.
     */
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Get tax.
     *
     * @return tax.
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * Set tax.
     *
     * @param taxAmount tax.
     */
    public void setTaxAmount(final BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * Get price.
     *
     * @return price.
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Set price.
     *
     * @param unitPrice price.
     */
    public void setUnitPrice(final BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }


    /**
     * Is item shipment.
     * @return true if item is shipment
     */
    public boolean isShipment() {
        return shipment;
    }

    /**
     * Set shpipment flag
     * @param shipment shipment flag
     */
    public void setShipment(final boolean shipment) {
        this.shipment = shipment;
    }

}
