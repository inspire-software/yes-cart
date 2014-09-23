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

package org.yes.cart.web.support.i18n;

/**
 * User: denispavlov
 * Date: 12-08-17
 * Time: 6:52 PM
 */
public interface I18NObject {

    /**
     * @return raw name
     */
    String getName();

    /**
     * @param locale locale
     * @return localised name
     */
    String getName(String locale);

    /**
     * @return raw description
     */
    String getDescription();

    /**
     * @param locale locale
     * @return localised description
     */
    String getDescription(String locale);

    /**
     * @return raw value
     */
    String getAttributeValue(String attribute);

    /**
     * @param locale locale
     * @return localised value
     */
    String getAttributeValue(String locale, String attribute);

}
