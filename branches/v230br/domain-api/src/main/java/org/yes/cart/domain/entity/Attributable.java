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

package org.yes.cart.domain.entity;

import java.util.Collection;
import java.util.Map;

/**
 * Mark objects as attributable.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Attributable extends Identifiable {

    /**
     * Get all attribute values.
     *
     * @return all attr values.
     */
    Collection<AttrValue> getAllAttributes();

    /**
     * Get all attribute values.
     *
     * @return all attr values.
     */
    Map<String, AttrValue> getAllAttributesAsMap();


    /**
     * Get all  attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of  attributes filtered by attribute name or empty collection if no attribute were found.
     */
    Collection/*<AttrValue>*/ getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    AttrValue getAttributeByCode(String attributeCode);


    /**
     * Get category name.
     *
     * @return category name.
     */
    String getName();

    /**
     * Set category name.
     *
     * @param name category name.
     */
    void setName(String name);

    /**
     * Get category description.
     *
     * @return category description.
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description description
     */
    void setDescription(String description);


}
