package org.yes.cart.domain.query;

import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-May-2011
 * Time: 09:48:01
 */
public interface PriceNavigation {
    /**
     * Expected value in following format CUR-LOW-HIGH format
     *
     * @param val value rfom request
     * @return filled price tier node.
     */
    Pair<String, Pair<BigDecimal, BigDecimal>> decomposePriceRequestParams(String val);

    /**
     * Compose price tier into string representation.
     *
     * @param currency   currency
     * @param lowBorder  low price
     * @param highBorder high price
     * @return string representation in CUR-LOW-HIGH format
     */
    String composePriceRequestParams(String currency, BigDecimal lowBorder, BigDecimal highBorder);

    /**
     * Compose price tier into string representation.
     *
     * @param currency           currency
     * @param lowBorder          low price
     * @param highBorder         high price
     * @param currencyDelimitter delimitter between currency and prices
     * @param priceDelimitter    price delimitter
     * @return string representation in CUR-LOW-HIGH format
     */
    String composePriceRequestParams(String currency,
                                     BigDecimal lowBorder,
                                     BigDecimal highBorder,
                                     String currencyDelimitter,
                                     String priceDelimitter);
}
