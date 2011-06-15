package org.yes.cart.web.support.service;

import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:47:19 PM
 */
public interface CurrencySymbolService {

    /**
     * Get the currency symbol(s) in html unicode format for given currency code.
     *
     * @param currencyCode currency code
     * @return currency symbol(s)
     */
    String getCurrencySymbol(String currencyCode);

    /**
     * Get currency to display.
     * @param curensiesListString  given comma separated string of currency codes
     * @return list of currency code - currency label
     */
    List<Pair<String, String>> getCurrencyToDisplay(String curensiesListString);

}
