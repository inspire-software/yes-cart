package org.yes.cart.service.impl;

import org.yes.cart.service.CurrencySymbolService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:54:40 PM
 */
public class CurrencySymbolServiceImpl implements CurrencySymbolService {

    private Map<String, String> currencyCodeSymbol = new HashMap <String, String>();

    /**
     * Constructor. 
     * @param currencyCodeSymbol map to use
     */
    public CurrencySymbolServiceImpl(final Map<String, String> currencyCodeSymbol) {
        this.currencyCodeSymbol = currencyCodeSymbol;
    }

    /** {@inheritDoc */
    public String getCurrencySymbol(final String currencyCode) {
        final String symbols = currencyCodeSymbol.get(currencyCode);
        if (symbols == null) {
            return currencyCode;
        }
        return symbols;
    }

    /**
     * IoC. Set the
     * @param currencyCodeSymbol to use.
     */
    public void setCurrencyCodeSymbol(final Map<String, String> currencyCodeSymbol) {
        this.currencyCodeSymbol = currencyCodeSymbol;
    }
}
