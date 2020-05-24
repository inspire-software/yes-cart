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
public interface ProductAttributesModelGroup extends Serializable {

    /**
     * Get group code.
     *
     * @return group code
     */
    String getCode();

    /**
     * Get the localised group names.
     *
     * @return localised group names.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get group in specific language.
     *
     * @param lang language
     *
     * @return localised group name
     */
    String getDisplayName(String lang);

    /**
     * Get all attributes that belong to this group.
     *
     * @return attributes
     */
    List<ProductAttributesModelAttribute> getAttributes();

    /**
     * Get attribute code.
     *
     * @param code code
     *
     * @return attribute
     */
    ProductAttributesModelAttribute getAttribute(String code);

}
