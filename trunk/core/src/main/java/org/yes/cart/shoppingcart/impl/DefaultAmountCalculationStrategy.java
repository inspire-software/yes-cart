package org.yes.cart.shoppingcart.impl;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy {

    private final BigDecimal tax;

    /**
     * Construct default amount calculator with included tax.
     * @param tax vat value
     */
    public DefaultAmountCalculationStrategy(final BigDecimal tax) {
        this.tax = tax;
    }

    /** {@inheritDoc} */
    public BigDecimal getTax() {
        return tax;
    }

    /** {@inheritDoc} */
    public boolean isTaxIncluded() {
        return true;
    }
}
