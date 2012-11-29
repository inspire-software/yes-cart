/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
