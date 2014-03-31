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

package org.yes.cart.icecat.transform

/**
 * User: denispavlov
 * Date: 12-07-30
 * Time: 8:54 PM
 */
class Util {

    /**
     * @param raw text
     * @return string without ; or ".
     */
    public static String escapeCSV(String raw) {
        if (raw == null || raw.length() == 0) {
            return '';
        }
        return raw.replace(";", ",").replace('"', "\\\"")
    }

    /**
     * @param raw text
     * @return string without ; or ".
     */
    public static String normalize(String raw) {
        return raw.replace("_", "-").replace("/", "A").replace(":", "Z").replace(" ", "-").replace("?", "-").replace(".", "-");
    }

    /**
     * set localised value to a property if it is applicable.
     * So if:
     *   prop = name
     *   langid = 8
     *   value = English
     *   langIdFilter = [8, 9]
     *   langFilter = [en, ru]
     * then we update:
     *   obj.name_en = "English"
     *   obj.name = "English"   // since 8 is the first id in filter, so default one
     *
     * @param obj object to update
     * @param prop property name base
     * @param langid value language id (e.g. 8 - EN)
     * @param value raw value
     * @param maxLength maximum length allowed
     * @param langIdFilter applicable ids
     * @param langFilter corresponding locale keys
     * @return
     */
    public static boolean setLocalisedValue(Object obj, String prop, String langid, String value, int maxLength,
                                            List<String> langIdFilter,
                                            List<String> langFilter) {
        int langIndex = langIdFilter.indexOf(langid);
        if (langIndex != -1 && Util.isNotBlank(value)) {
            def lang = langFilter[langIndex];
            obj."$prop".put(lang, Util.maxLength(value, maxLength));
            return true; // added
        }
        return false; // skipped
    }

    /**
     * Get localised value.
     *
     * @param obj source object
     * @param prop property
     * @param lang language required
     * @return value in required language or in default
     */
    public static String getLocalisedValue(Object obj, String prop, String lang) {
        def val = obj."$prop".get(lang);
        if (val == null) {
            val = obj."$prop".get("def");
        }
        if (val == null) {
            return '';
        }
        return val;
    }

    /**
     * @param str text
     * @return true if not blank
     */
    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    /**
     * @param str text
     * @return true if null, empty or whitespace only
     */
    public static boolean isBlank(final String str) {

        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;

    }

    /**
     * @param str text
     * @param maxChars max chars
     * @return truncated string if it is larger than max chars length
     */
    public static String maxLength(final String str, final int maxChars) {
        if (str == null || str.length() < maxChars) {
            return str;
        }
        return str.substring(0, maxChars);
    }


}
