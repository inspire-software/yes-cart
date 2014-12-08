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

import org.yes.cart.domain.misc.navigation.range.RangeList;

/**
 * Attributes of product type.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductTypeAttr extends Auditable, Rankable {

    String NAVIGATION_TYPE_SINGLE = "S";
    String NAVIGATION_TYPE_RANGE = "R";

    /**
     * Get the primary key.
     *
     * @return primary key.
     */
    long getProductTypeAttrId();

    /**
     * Set pk value.
     *
     * @param productTypeAttrId PK
     */
    void setProductTypeAttrId(long productTypeAttrId);

    /**
     * Get {@link Attribute}.
     *
     * @return {@link Attribute}
     */
    Attribute getAttribute();

    /**
     * Set {@link Attribute}
     *
     * @param attribute {@link Attribute} to use.
     */
    void setAttribute(Attribute attribute);

    /**
     * Get default product type if any.
     *
     * @return default product type.
     */
    ProductType getProducttype();

    /**
     * Set product type {@link ProductType}
     *
     * @param producttype Product Type
     */
    void setProducttype(ProductType producttype);

    /**
     * Rank of navigation record in case of range navigation.
     *
     * @return rank
     */
    int getRank();

    /**
     * Rank of navigation record in case of range navigation.
     *
     * @param rank to set.
     */
    void setRank(int rank);

    /**
     * Is this attribute visible on storefront ?
     *
     * @return true if attribute is visible.
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


    /**
     * Get the value ranges list.
     *
     * @return {@link RangeList}
     */
    RangeList getRangeList();

    /**
     * Set value ranges list.
     *
     * @param rangeList {@link RangeList}
     */
    void setRangeList(RangeList rangeList);

}
