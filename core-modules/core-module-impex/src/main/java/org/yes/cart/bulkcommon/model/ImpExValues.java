/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.bulkcommon.model;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:35
 */
public interface ImpExValues {

    /**
     * String value (default for all import data).
     */
    String STRING = "STRING";
    /**
     * Boolean value (e.g. for flags).
     */
    String BOOLEAN = "BOOLEAN";
    /**
     * Long value (e.g. for PK's).
     */
    String LONG = "LONG";
    /**
     * Integer value.
     */
    String INT = "INT";
    /**
     * BigDecimal value.
     */
    String DECIMAL = "DECIMAL";
    /**
     * Date value. Format: "yyyy-MM-dd" {@link org.yes.cart.util.DateUtils#ldParseSDT(String)}
     */
    String DATE = "DATE";
    /**
     * Date value. Format: "yyyy-MM-dd HH:mm:ss" {@link org.yes.cart.util.DateUtils#ldtParseSDT(String)}
     */
    String DATETIME = "DATETIME";
    /**
     * Date value. Format: "yyyy-MM-dd HH:mm:ss" {@link org.yes.cart.util.DateUtils#zdtParseSDT(String)}
     */
    String ZONEDTIME = "ZONEDTIME";
    /**
     * Date value. Format: "yyyy-MM-dd HH:mm:ss" {@link org.yes.cart.util.DateUtils#iParseSDT(String)}
     */
    String INSTANT = "INSTANT";

}
