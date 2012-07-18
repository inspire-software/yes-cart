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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Attribute type DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductTypeAttrDTO extends Identifiable {

    /**
     * Get the primary key.
     *
     * @return primary key.
     */
    long getProductTypeAttrId();

    /**
     * Set pk value.
     *
     * @param productTypeAttrId product type pk value.
     */
    void setProductTypeAttrId(long productTypeAttrId);

    /**
     * Get the attribute.
     *
     * @return {@link org.yes.cart.domain.entity.Attribute}
     */
    AttributeDTO getAttributeDTO();

    /**
     * Set attribute.
     *
     * @param attribute attribute.
     */
    void setAttributeDTO(AttributeDTO attribute);


    /**
     * Get default product type if any.
     *
     * @return default product type.
     */
    long getProducttypeId();

    /**
     * Set product type id
     *
     * @param producttypeId Product Type id
     */
    void setProducttypeId(long producttypeId);

    /**
     * Candidate to deprecation.
     *
     * @return rank
     * @deprecated do not use it
     */
    int getRank();


    /**
     * Candidate to deprecation.
     *
     * @param rank to set.
     * @deprecated do not use it
     */
    void setRank(int rank);

    /**
     * Is this attribute visible on storefront ?
     *
     * @return true if attribute is vesible.
     */
    boolean isVisible();

    /**
     * Set attribute visible to storefront.
     *
     * @param visible is attribute visible to storefront.
     */
    void setVisible(boolean visible);

    /**
     * Is this attribute will be taken to count product similarity.
     *
     * @return true if this attribute will be taken to count product similarity.
     */
    boolean isSimilarity();

    /**
     * Set flag to use similarity on this product.
     *
     * @param similarity use similarity on this product.
     */
    void setSimilarity(boolean similarity);

    /**
     * Use for attribute navigation.
     *
     * @return true if attribute used for attribute navigation.
     */
    boolean isNavigation();

    /**
     * Set to true if attribute will be used for filtered navigation.
     *
     * @param navigation true if attribute will be used for filtered navigation.
     */
    void setNavigation(boolean navigation);

    /**
     * Get the filtered navigation type: S - single,  R - range.
     * Xml range list must be defined for for range navigation.
     *
     * @return navigation type.
     */
    String getNavigationType();

    /**
     * Navigation type.
     *
     * @param navigationType
     */
    void setNavigationType(String navigationType);

    /**
     * Get the xml description of value ranges.
     *
     * @return xml description of value ranges.
     */
    String getRangeNavigation();

    /**
     * Set the description of value ranges in xml
     *
     * @param rangeNavigation description of value ranges in xml.
     */
    void setRangeNavigation(String rangeNavigation);


}
