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

package org.yes.cart.service.domain;


import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttributeService extends GenericService<Attribute> {

    /**
     * Get all attribute codes (+ brands, price, query and tag).
     *
     * @return set of attribute codes.
     */
    Set<String> getAllAttributeCodes();

    /**
     * Get all navigatable attribute codes (+ brands, price, query and tag).
     *
     * @return set of attribute codes.
     */
    Set<String> getAllNavigatableAttributeCodes();

    /**
     * Get all single value navigatable attribute codes for product type.
     *
     * @param productTypeId product type
     *
     * @return set of attribute codes.
     */
    Map<String, Integer> getSingleNavigatableAttributeCodesByProductType(long productTypeId);

    /**
     * Get attribute names.
     *
     * @return map of attribute code - name.
     */
    Map<String, I18NModel> getAllAttributeNames();

    /**
     * Get attribute names.
     *
     * @param codes attribute codes
     * @return map of attribute code - name.
     */
    Map<String, I18NModel> getAttributeNamesByCodes(Set<String> codes);


    /**
     * Get list of attributes by given group code.
     *
     * @param attributeGroupCode given group code
     * @return list of {@link Attribute}
     */
    List<Attribute> findByAttributeGroupCode(String attributeGroupCode);

    /**
     * Get attribute by given code.
     *
     * @param attributeCode given  code
     * @return instance {@link Attribute} if fount, otherwise null
     */
    Attribute findByAttributeCode(String attributeCode);

    /**
     * Get list of available attributes within given <code>attributeGroupCode</code>, that can be assigned to business entity.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @param assignedAttributeCodes list of codes, that already assigned to entity (hence excluded)
     * @return list of available attributes to fill with values.
     */
    List<Attribute> findAvailableAttributes(String attributeGroupCode, List<String> assignedAttributeCodes);


    /**
     * Get the list of attributes, that belong to product type.
     *
     * @param productTypeId given product ype id
     * @return list of attributes
     */
    List<Attribute> getAvailableAttributesByProductTypeId(long productTypeId);

    /**
     * Get the list of image attributes , that belong to product type.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @return list of attributes
     */
    List<Attribute> getAvailableImageAttributesByGroupCode(String attributeGroupCode);


    /**
     * Find all attributes in given group , that allow to have several attributes
     *
     * @param attributeGroupCode group of attributes
     * @return list of attributes with allowed multiple values or null if no such attributes found
     */
    List<Attribute> findAttributesWithMultipleValues(String attributeGroupCode);

}
