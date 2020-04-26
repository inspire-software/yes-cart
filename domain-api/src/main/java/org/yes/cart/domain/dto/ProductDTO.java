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

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Instant getPimUpdated();

    /**
     * PIM last update timestamp
     *
     * @param pimUpdated last update timestamp
     */
    void setPimUpdated(Instant pimUpdated);


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
     * Flag to indicate that this product cannot be sold separately (i.e. an option, part of bundle etc).
     *
     * @return true means not to be sold separately
     */
    boolean getNotSoldSeparately();

    /**
     * Flag to indicate that this product cannot be sold separately (i.e. an option, part of bundle etc).
     *
     * @param notSoldSeparately true means not to be sold separately
     */
    void setNotSoldSeparately(boolean notSoldSeparately);

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
     * Is this product type configurable.
     *
     * @return true if configurable
     */
    boolean isConfigurable();

    /**
     * Set product type to configurable
     *
     * @param configurable true if configurable
     */
    void setConfigurable(boolean configurable);

    /**
     * Get {@link ProductOptionDTO} for product if it has configurable flag
     *
     * @return Set of {@link ProductOptionDTO} for product.
     */
    List<ProductOptionDTO> getConfigurationOptions();

    /**
     * Set {@link ProductOptionDTO} for product.
     *
     * @param configurationOptions {@link ProductOptionDTO} for product
     */
    void setConfigurationOptions(List<ProductOptionDTO> configurationOptions);


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
