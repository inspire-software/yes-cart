package org.yes.cart.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 01/10/2015
 * Time: 18:41
 */
public interface ProductPriceModel extends Serializable {

    /**
     * Get price reference.
     *
     * @return reference (e.g. SKU code, label)
     */
    String getRef();

    /**
     * Get currency code.
     *
     * @return currency code
     */
    String getCurrency();

    /**
     * Get price quantity.
     *
     * @return quantity
     */
    BigDecimal getQuantity();

    /**
     * Get regular/list price.
     *
     * @return regular price.
     */
    BigDecimal getRegularPrice();

    /**
     * Get sale price.
     *
     * @return sale price.
     */
    BigDecimal getSalePrice();

    /**
     * Get flag to indicate if tax info is enabled.
     *
     * @return true if tax info is enabled
     */
    boolean isTaxInfoEnabled();

    /**
     * Get flag to indicate to use net prices to display.
     *
     * @return true if net prices to be displayed
     */
    boolean isTaxInfoUseNet();

    /**
     * Get flag to indicate to display amount of tax.
     *
     * @return true if tax amount to be displayed
     */
    boolean isTaxInfoShowAmount();

    /**
     * Tax amount on sale price
     *
     * @return tax
     */
    BigDecimal getPriceTax();

    /**
     * Tax rate used.
     *
     * @return tax rate
     */
    BigDecimal getPriceTaxRate();

    /**
     * Tax code for tax applied.
     *
     * @return tax code
     */
    String getPriceTaxCode();

    /**
     * Flag to indicate if tax is exclusive of price.
     *
     * @return tax exclusive of price
     */
    boolean isPriceTaxExclusive();

}
