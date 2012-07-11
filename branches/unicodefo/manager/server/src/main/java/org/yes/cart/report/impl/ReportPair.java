package org.yes.cart.report.impl;

/**
 *
 * Pair object to use reports part.
 * Already used immutable Pair from misc package has not setters, but setters is need to
 * perform corrrect serialization by BlazeDS.
 *
 * See http://cornelcreanga.com/2008/07/amf-problems-when-serializing-between-java-and-actionscript/
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/7/12
 * Time: 9:37 AM
 */
public class ReportPair {

    private String lang;

    private String value;

    /**
     * Create pair.
     * @param lang language
     * @param value localized value.
     */
    public ReportPair(final String lang, final String value) {
        this.lang = lang;
        this.value = value;
    }

    /**
     * Get lanuage.
     * @return lang label
     */
    public String getLang() {
        return lang;
    }

    /**
     * Set language.
     * @param lang  lang
     */
    public void setLang(final String lang) {
        this.lang = lang;
    }

    /**
     * Get localized value.
     * @return localized value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set localized value.
     * @param value localized value.
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
