package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy {

    private final BigDecimal tax;

    /**
     * Construct default amount calculator with included tax.
     * @param tax vat value in percents
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


    /** {@inheritDoc} */
    public AmountCalculationResult calculate(final Shop shop,
                                             final List<? extends CartItem> items,
                                             final CarrierSla carrierSla) {
        final AmountCalculationResult rez = new AmountCalculationResultImpl();


        return rez;
    }


}
