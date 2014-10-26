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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * Product.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Product extends Auditable, Attributable, Seoable, Codable, Taggable {

    /**
     * When available on warehouse.
     */
    int AVAILABILITY_STANDARD = 1;
    /**
     * For preorder.
     */
    int AVAILABILITY_PREORDER = 2;
    /**
     * Available for backorder.
     */
    int AVAILABILITY_BACKORDER = 4;
    /**
     * Always available
     */
    int AVAILABILITY_ALWAYS = 8;

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
     * @return  Availability
     */
    int getAvailability();

    /**
     * Set product availability
     *
     * @param availability product
     */
    void setAvailability(int availability);


    /**
     * Get the {@link Brand} of product.
     *
     * @return {@link Brand} of product.
     */
    Brand getBrand();

    /**
     * Set {@link Brand} of product.
     *
     * @param brand {@link Brand} of product.
     */
    void setBrand(Brand brand);

    /**
     * Get {@link ProductType}
     *
     * @return product type
     */
    ProductType getProducttype();

    /**
     * Set the {@link ProductType}
     *
     * @param producttype Get {@link ProductType}
     */
    void setProducttype(ProductType producttype);


    /**
     * Product's SKUs. SKU - Stock keeping unit or product variation. Each product has at least one sku.
     *
     * @return collection fo product skus.
     */
    Collection<ProductSku> getSku();

    /**
     * Set collection of skus.
     *
     * @param sku sku collection.
     */
    void setSku(Collection<ProductSku> sku);


    /**
     * Get all products attributes.
     *
     * @return collection of product attributes.
     */
    Set<AttrValueProduct> getAttributes();

    /**
     * Get all products attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of product attributes filtered by attribute name or empty collection if no attribute were found.
     */
    ///////////////////////////////////////////////////////////////Collection<AttrValueProduct> getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    ///////////////////////////////////////////////////////////////AttrValueProduct getAttributeByCode(String attributeCode);


    /**
     * Set collection of products attributes.
     *
     * @param attribute collection of products attributes
     */
    void setAttributes(Set<AttrValueProduct> attribute);

    /**
     * Get the assigned categories to product.
     *
     * @return assigned categories
     */
    Set<ProductCategory> getProductCategory();

    /**
     * Set assigned categories.
     *
     * @param productCategory assigned categories.
     */
    void setProductCategory(Set<ProductCategory> productCategory);

    /**
     * Get {@link ProductEnsebleOption} for product if it has enseble flag
     *
     * @return Set of {@link ProductEnsebleOption} for product.
     */
    Set<ProductEnsebleOption> getEnsebleOption();

    /**
     * Set {@link ProductEnsebleOption} for product.
     *
     * @param ensebleOption {@link ProductEnsebleOption} for product
     */
    void setEnsebleOption(Set<ProductEnsebleOption> ensebleOption);

    /**
     * Set the product {@link ProductAssociation}, like up-sell, cross-sell, etc..
     *
     * @return product {@link ProductAssociation}.
     */
    Set<ProductAssociation> getProductAssociations();

    /**
     * Set product {@link ProductAssociation}.
     *
     * @param productAssociations product {@link ProductAssociation}.
     */
    void setProductAssociations(Set<ProductAssociation> productAssociations);



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
     * display name.
     *
     * @return display name.
     */
    String getDisplayName();

    /**
     * Get display name
     *
     * @param name display name
     */
    void setDisplayName(String name);

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
     * Get description for indexing
     *
     * @return as is description
     */
    String getDescriptionAsIs();

    /**
     * Get the default sku. For single sku product it will be only one sku.
     * In case of multi sku product default sku has the same sku code as product, otherwise
     * the first will be returned.,
     *
     * @return default sku or null if not found
     */
    ProductSku getDefaultSku();

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
     * Get sku by given code.
     *
     * @param skuCode given sku code
     * @return product sku if found, otherwise null
     */
    ProductSku getSku(String skuCode);

    /**
     * Is product multisku .
     *
     * @return true if product multisku
     */
    boolean isMultiSkuProduct();


    /**
     * Get the space separated product tags. For example
     * sale specialoffer, newarrival etc.
     *
     * This tags should not be shown to customer, just for query navigation.
     *
     * @return space separated product tags
     */
    String getTag();

    /**
     * Set space separated product tags.
     *
     * @param tag space separated product tags.
     */
    void setTag(String tag);


    /**
     * Get total quantity of skus on all warehouses.
     *
     * @param warehouses warehouse to consider
     *
     * @return total quantity
     */
    Map<String, BigDecimal> getQtyOnWarehouse(Collection<Warehouse> warehouses);

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


