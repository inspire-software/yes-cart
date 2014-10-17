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


import org.yes.cart.domain.entity.Guidable;
import org.yes.cart.domain.entity.Identifiable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent category as DTO object.
 * Price tiers edit perform via changing XML.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CategoryDTO extends Identifiable, Guidable {

    /**
     * Get category pk value.
     *
     * @return category pk value
     */
    long getCategoryId();

    /**
     * Set category pk value.
     *
     * @param categoryId pk value
     */
    void setCategoryId(long categoryId);

    /**
     * Get parrent pk value.
     *
     * @return parrent pk value.
     */
    long getParentId();

    /**
     * Set parrent pk value.
     *
     * @param parentId parrent pk value.
     */
    void setParentId(long parentId);

    /**
     * Get category rank inside parent category.
     *
     * @return category rank.
     */
    int getRank();

    /**
     * Set category rank.
     *
     * @param rank category rank
     */
    void setRank(int rank);

    /**
     * Default product type in category.
     * Set it, to allow filtered navigation by attributes.
     *
     * @return default product type in category
     */
    Long getProductTypeId();

    /**
     * Set default product type id
     *
     * @param productTypeId default product type.
     */
    void setProductTypeId(Long productTypeId);

    /**
     * Get default product type name
     *
     * @return default product type name if any
     */
    String getProductTypeName();

    /**
     * Set default product type name.
     *
     * @param productTypeName default product type name
     */
    void setProductTypeName(String productTypeName);


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
     * Display name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get display name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);

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

    /**
     * Get category UI template variation.
     *
     * @return category UI template variation.
     */
    String getUitemplate();

    /**
     * Set category UI template variation.
     *
     * @param uitemplate template variation.
     */
    void setUitemplate(String uitemplate);

    /**
     * Get available from date.  Null value means no start.
     *
     * @return available from date.
     */
    Date getAvailablefrom();

    /**
     * Set available from date.
     *
     * @param availablefrom available from date.
     */
    void setAvailablefrom(Date availablefrom);

    /**
     * Get available to date.  Null value means no end date.
     *
     * @return available to date.
     */
    Date getAvailableto();

    /**
     * Set available to date.
     *
     * @param availableto available to date.
     */
    void setAvailableto(Date availableto);


    /**
     * @return true if filtered navigation by attributes allowed
     */
    Boolean getNavigationByAttributes();

    /**
     * Set navigation by attribute values.
     *
     * @param navigationByAttributes navigation by attribute values flag.
     */
    void setNavigationByAttributes(Boolean navigationByAttributes);

    /**
     * @return true if filtered navigation by brand allowed
     */
    Boolean getNavigationByBrand();

    /**
     * Set navigation by brand in category.
     *
     * @param navigationByBrand flag to set
     */
    void setNavigationByBrand(Boolean navigationByBrand);

    /**
     * @return true if filtered navigation by price ranges allowed
     */
    Boolean getNavigationByPrice();

    /**
     * Set navigation by price.
     *
     * @param navigationByPrice navigation by price flag.
     */
    void setNavigationByPrice(Boolean navigationByPrice);

    /**
     * Optional price range configuration. Default shop price tiers configuration will used if empty.
     *
     * @return price range configuration for price filtered navigation
     */
    String getNavigationByPriceTiers();

    /**
     * Set price range configuration for price filtered navigation.
     *
     * @param navigationByPriceTiers price range configuration.
     */
    void setNavigationByPriceTiers(String navigationByPriceTiers);

    /**
     * Children of this category
     *
     * @return Children list.
     */
    List<CategoryDTO> getChildren();

    /**
     * Set children list.
     *
     * @param children children list.
     */
    void setChildren(List<CategoryDTO> children);


    /**
     * Get attributes.
     *
     * @return list of attributes
     */
    Set<AttrValueCategoryDTO> getAttributes();

    /**
     * Set list of attributes.
     *
     * @param attribute list of attributes
     */
    void setAttributes(Set<AttrValueCategoryDTO> attribute);


    /**
     * Get seo uri.
     * @return uri
     */
    String getUri();

    /**
     * Set seo uri;
     * @param uri  seo uri to  use
     */
    void setUri(String uri);

    /**
     * Get title.
     * @return  title
     */

    String getTitle();

    /**
     * Set seo title
     * @param title seo title to use
     */
    void setTitle(String title);

    /**
     * Display titles.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayTitles();

    /**
     * Set display titles
     *
     * @param titles localised locale => name pairs
     */
    void setDisplayTitles(Map<String, String> titles);

    /**
     * Get meta key words.
     * @return meta key words
     */

    String getMetakeywords();

    /**
     * Set meta key words to use.
      * @param metakeywords      key words
     */
    void setMetakeywords(String metakeywords);


    /**
     * Display metakeywords.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetakeywords();

    /**
     * Set display metakeywords
     *
     * @param metakeywords localised locale => name pairs
     */
    void setDisplayMetakeywords(Map<String, String> metakeywords);


    /**
     * Get seo description
     * @return seo description.
     */
    String getMetadescription();

    /**
     * Set seo description.
     * @param metadescription description to use
     */
    void setMetadescription(String metadescription);



    /**
     * Display metadescription.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetadescriptions();

    /**
     * Set display metadescription
     *
     * @param metadescription localised locale => name pairs
     */
    void setDisplayMetadescriptions(Map<String, String> metadescription);


}
