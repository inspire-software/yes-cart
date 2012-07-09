package org.yes.cart.web.support.service.impl;


import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:54:40 PM
 */
public class CurrencySymbolServiceImpl implements CurrencySymbolService {

    private Map<String, String> currencyCodeSymbol = new HashMap<String, String>();

    /**
     * Constructor.
     *
     * @param currencyCodeSymbol map to use
     */
    public CurrencySymbolServiceImpl(final Map<String, String> currencyCodeSymbol) {
        this.currencyCodeSymbol = currencyCodeSymbol;
    }

    /**
     * {@inheritDoc
     */
    public String getCurrencySymbol(final String currencyCode) {
        final String symbols = currencyCodeSymbol.get(currencyCode);
        if (symbols == null) {
            return currencyCode;
        }
        return symbols;
    }

    /**
     * {@inheritDoc
     */
    public List<Pair<String, String>> getCurrencyToDisplayAsList(final String curensiesListString) {
        if (StringUtils.isNotBlank(curensiesListString)) {
            final String [] currCodes = curensiesListString.split(",");
            final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(currCodes.length);
            for (String currCode : currCodes) {
                rez.add(
                  new Pair<String, String>(currCode, getCurrencySymbol(currCode))
                );
            }
            return rez;
        }
        return null;
    }

    /**
     * {@inheritDoc
     */
    public  Map<String, String> getCurrencyToDisplayAsMap(final String curensiesListString){
        if (StringUtils.isNotBlank(curensiesListString)) {
            final String [] currCodes = curensiesListString.split(",");
            final Map<String, String> rez = new LinkedHashMap<String, String>(currCodes.length);
            for (String currCode : currCodes) {
                rez.put(getCurrencySymbol(currCode), currCode);
            }
            return rez;
        }
        return null;

    }

    /**
     * IoC. Set the
     *
     * @param currencyCodeSymbol to use.
     */
    public void setCurrencyCodeSymbol(final Map<String, String> currencyCodeSymbol) {
        this.currencyCodeSymbol = currencyCodeSymbol;
    }
}
