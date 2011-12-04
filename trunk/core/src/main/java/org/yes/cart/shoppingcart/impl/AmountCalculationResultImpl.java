package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.shoppingcart.AmountCalculationResult;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 3/12/11
 * Time: 14:44
 */
public class AmountCalculationResultImpl implements AmountCalculationResult {

    //20% vat = item*20/120

    private BigDecimal subTotal;
    private BigDecimal subTotalTax;
    private BigDecimal subTotalAmount;

    private BigDecimal delivery;
    private BigDecimal deliveryTax;
    private BigDecimal deliveryAmount;

    private BigDecimal total;
    private BigDecimal totalTax;
    private BigDecimal totalAmount;


    public AmountCalculationResultImpl() {

        subTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        subTotalTax = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        subTotalAmount = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

        delivery = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        deliveryTax = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        deliveryAmount = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

        total = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        totalTax = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        totalAmount = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * {@inheritDoc}
     */
    public void setSubTotal(final BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getSubTotalTax() {
        return subTotalTax;
    }

    /**
     * {@inheritDoc}
     */
    public void setSubTotalTax(final BigDecimal subTotalTax) {
        this.subTotalTax = subTotalTax;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getSubTotalAmount() {
        return subTotalAmount;
    }

    /**
     * {@inheritDoc}
     */
    public void setSubTotalAmount(final BigDecimal subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getDelivery() {
        return delivery;
    }

    /**
     * {@inheritDoc}
     */
    public void setDelivery(final BigDecimal delivery) {
        this.delivery = delivery;
    }

    public BigDecimal getDeliveryTax() {
        return deliveryTax;
    }

    /**
     * {@inheritDoc}
     */
    public void setDeliveryTax(final BigDecimal deliveryTax) {
        this.deliveryTax = deliveryTax;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    /**
     * {@inheritDoc}
     */
    public void setDeliveryAmount(final BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * {@inheritDoc}
     */
    public void setTotal(final BigDecimal total) {
        this.total = total;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    /**
     * {@inheritDoc}
     */
    public void setTotalTax(final BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * {@inheritDoc}
     */
    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
