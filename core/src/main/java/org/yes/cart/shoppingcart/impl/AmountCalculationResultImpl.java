package org.yes.cart.shoppingcart.impl;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 3/12/11
 * Time: 14:44
 */
public class AmountCalculationResultImpl {

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


}
