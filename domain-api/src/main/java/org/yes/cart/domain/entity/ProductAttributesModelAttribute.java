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

package org.yes.cart.domain.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/05/2020
 * Time: 23:21
 */
public interface ProductAttributesModelAttribute extends Serializable {

    /**
     * Get attribute code.
     *
     * @return attribute code
     */
    String getCode();

    /**
     * Get the localised attribute names.
     *
     * @return localised attribute names.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get name in specific language.
     *
     * @param lang language
     *
     * @return localised attribute name
     */
    String getDisplayName(String lang);

    /**
     * Is this attribute a multi-value.
     *
     * @return multi-value flag
     */
    boolean isMultivalue();

    /**
     * Get all values for given attribute (usually single value)
     *
     * @return values
     */
    List<ProductAttributesModelValue> getValues();

}
