/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.domain.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/05/2020
 * Time: 23:22
 */
public interface ProductAttributesModelValue extends Serializable {

    /**
     * Get attribute code for this value.
     *
     * @return attribute code
     */
    String getCode();

    /**
     * Get the string representation of attribute value.
     *
     * @return attribute value.
     */
    String getVal();

    /**
     * Get the localised attribute values.
     *
     * @return localised attribute values.
     */
    Map<String, String> getDisplayVals();

    /**
     * Get value in specific language.
     *
     * @param lang language
     *
     * @return localised attribute value
     */
    String getDisplayVal(String lang);

}
