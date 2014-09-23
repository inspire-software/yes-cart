package org.yes.cart.domain.misc.navigation.range.impl;

import org.yes.cart.domain.misc.navigation.range.DisplayValue;

/**
 * User: denispavlov
 * Date: 23/04/2014
 * Time: 15:41
 */
public class DisplayValueImpl implements DisplayValue {

    private String lang;
    private String value;

    /** {@inheritDoc} */
    public String getLang() {
        return lang;
    }

    /** {@inheritDoc} */
    public void setLang(final String lang) {
        this.lang = lang;
    }

    /** {@inheritDoc} */
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(final String value) {
        this.value = value;
    }

    public DisplayValueImpl() {

    }


    public DisplayValueImpl(final String lang, final String value) {
        this.lang = lang;
        this.value = value;
    }
}
