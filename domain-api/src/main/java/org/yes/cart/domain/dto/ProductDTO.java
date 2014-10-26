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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Product DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductDTO extends Identifiable {

    /**
     * Get product pk.
     *
     * @return product pk.
     */
    long getProductId();

    /**
     * Set product pk.
     *
     * @param productId product pk.
     */
    void setProductId(long productId);

    /**
     * Get the product code.
     *
     * @return product code.
     */
    String getCode();

    /**
     * Product code.
     * Limitation code must not contains underscore
     *
     * @param code
     */
    void setCode(String code);

    /**
     * Get the {@link org.yes.cart.domain.entity.Brand} of product.
     *
     * @return {@link org.yes.cart.domain.entity.Brand} of product.
     */
    BrandDTO getBrandDTO();

    /**
     * Set {@link BrandDTO} of product.
     *
     * @param brand {@link BrandDTO} of product.
     */
    void setBrandDTO(BrandDTO brand);

    /**
     * Get {@link org.yes.cart.domain.entity.ProductType}
     *
     * @return product type
     */
    ProductTypeDTO getProductTypeDTO();

    /**
     * Set the {@link ProductTypeDTO}
     *
     * @param producttype Get {@link ProductTypeDTO}
     */
    void setProductTypeDTO(ProductTypeDTO producttype);


    /**
     * Get product name.
     *
     * @return product name.
     */
    String getName();

    /**
     * Set product name.
     *
     * @param name product name.
     */
    void setName(String name);

    /**
     * Display name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Set display name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);


    /**
     * Get product description.
     *
     * @return product description.
     */
    String getDescription();

    /**
     * Set product description.
     *
     * @param description product description.
     */
    void setDescription(String description);


    /**
     * Get start of product availability.
     * Null - product has not start date, means no limitation.
     *
     * @return start of product availability.
     */
    Date getAvailablefrom();

    /**
     * Set start of product availability.
     *
     * @param availablefrom start of product availability.
     */
    void setAvailablefrom(Date availablefrom);

    /**
     * Get end of product availability.
     * Null - product has not end date, means no limitation.
     *
     * @return end of product availability.
     */
    Date getAvailableto();

    /**
     * Set end of product availability.
     *
     * @param availableto end of product availability.
     */
    void setAvailableto(Date availableto);

    /**
     * Get product availability
     *
     * @return availability
     */
    int getAvailability();

    /**
     * Set productavailability
     *
     * @param availability product
     */
    void setAvailability(int availability);


    /**
     * Get the assigned categories to product.
     *
     * @return assigned categories
     */
    Set<ProductCategoryDTO> getProductCategoryDTOs();

    /**
     * Set assigned categories.
     *
     * @param productCategoryDTOs assigned categories.
     */
    void setProductCategoryDTOs(Set<ProductCategoryDTO> productCategoryDTOs);

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
     * Display title.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayTitles();

    /**
     * Set display title
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

    /**
     * Get the featured flag for product.
     *
     * @return set featured flag.
     */
    Boolean getFeatured();

    /**
     * Set product featured flag.
     *
     * @param featured featured flag.
     */
    void setFeatured(Boolean featured);


    /**
     * Get attributes.
     *
     * @return list of attributes
     */
    Collection<AttrValueProductDTO> getAttributes();

    /**
     * Set list of attributes.
     *
     * @param attribute list of attributes
     */
    void setAttributes(Collection<AttrValueProductDTO> attribute);

    /**
     * Get product tags.
     *
     * @return tag line.
     */
    String getTag();

    /**
     * Set product tag.
     *
     * @param tag product tag line
     */
    void setTag(String tag);

    /**
     * Get minimal quantity for order. E.g. 5.0 means customer can only buy 5 or more.
     *
     * @return minimal order quantity
     */
    BigDecimal getMinOrderQuantity();

    /**
     * @param minOrderQuantity minimal quantity for order.
     */
    void setMinOrderQuantity(BigDecimal minOrderQuantity);

    /**
     * Get maximum quantity for order. E.g. 5.0 means customer can only buy up to 5
     *
     * @return maximum order quantity
     */
    BigDecimal getMaxOrderQuantity();

    /**
     * @param maxOrderQuantity maximum quantity for order.
     */
    void setMaxOrderQuantity(BigDecimal maxOrderQuantity);

    /**
     * Get step quantity for order. E.g. 5.0 means customer can only buy in batches of 5 - 5, 10, 15
     * but not say 11.
     *
     * @return step order quantity
     */
    BigDecimal getStepOrderQuantity();

    /**
     * @param stepOrderQuantity step quantity for order.
     */
    void setStepOrderQuantity(BigDecimal stepOrderQuantity);

}
