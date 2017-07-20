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

package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Guidable;
import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.util.*;

/**
 * Product DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductDTO extends Identifiable, Guidable {

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
     * @param code unique code
     */
    void setCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerCode();

    /**
     * Manufacturer non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code manufacturer code
     */
    void setManufacturerCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerPartCode();

    /**
     * Manufacturer non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code manufacturer code
     */
    void setManufacturerPartCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getSupplierCode();

    /**
     * Supplier non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code supplier code
     */
    void setSupplierCode(String code);

    /**
     * Get the non unique product catalog code.
     *
     * @return catalog code.
     */
    String getSupplierCatalogCode();

    /**
     * Supplier non unique catalog code.
     * Limitation code must not contains underscore
     *
     * @param code catalog code
     */
    void setSupplierCatalogCode(String code);

    /**
     * PIM external reference (e.g. IceCat, CNet etc)
     *
     * @return external reference
     */
    String getPimCode();

    /**
     * PIM external reference (e.g. IceCat, CNet etc)
     *
     * @param pimCode external reference
     */
    void setPimCode(String pimCode);


    /**
     * PIM updates are disabled for this product
     *
     * @return true means no PIM updates only manual
     */
    boolean getPimDisabled();

    /**
     * PIM updates are disabled for this product
     *
     * @param pimDisabled true means no PIM updates only manual
     */
    void setPimDisabled(boolean pimDisabled);


    /**
     * PIM updates are required, data is stale
     *
     * @return true means PIM data is old and needs update
     */
    boolean getPimOutdated();

    /**
     * PIM updates are required, data is stale
     *
     * @param pimOutdated true means PIM data is old and needs update
     */
    void setPimOutdated(boolean pimOutdated);


    /**
     * PIM last update timestamp
     *
     * @return last update timestamp
     */
    Date getPimUpdated();

    /**
     * PIM last update timestamp
     *
     * @param pimUpdated last update timestamp
     */
    void setPimUpdated(Date pimUpdated);


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

    /**
     * List of SKU associated with product.
     *
     * @return list of SKU
     */
    List<ProductSkuDTO> getSku();

    /**
     * List of SKU associated with product.
     *
     * @param sku SKU
     */
    void setSku(List<ProductSkuDTO> sku);

}
