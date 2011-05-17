package org.yes.cart.service;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:47:19 PM
 */
public interface CurrencySymbolService {

    /**
     * Get the currency symbol(s) in html unicode format for given currency code.
     * @param currencyCode currency code
     * @return currency symbol(s)
     */
    String getCurrencySymbol(String currencyCode);

}
