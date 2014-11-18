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
package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.bridge.ProductSkuCodeBridge;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

@Indexed(index = "luceneindex/product", interceptor = org.yes.cart.domain.interceptor.ProductEntityIndexingInterceptor.class)
@DynamicBoost(impl = ProductDynamicBoostStrategy.class)
public class ProductEntity implements org.yes.cart.domain.entity.Product, java.io.Serializable {

    private ProductSku defaultProductSku = null;
    private long productId;
    private long version;

    private String code;
    private Date availablefrom;
    private Date availableto;
    private String name;
    private String displayName;
    private String description;
    private String tag;
    private Brand brand;
    private ProductType producttype;
    private int availability;
    private Set<AttrValueProduct> attributes = new HashSet<AttrValueProduct>(0);
    private Set<ProductCategory> productCategory = new HashSet<ProductCategory>(0);
    private Collection<ProductSku> sku = new ArrayList<ProductSku>(0);
    private Set<ProductEnsebleOption> ensebleOption = new HashSet<ProductEnsebleOption>(0);
    private Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>(0);
    private Boolean featured;
    private SeoEntity seoInternal;
    private BigDecimal minOrderQuantity;
    private BigDecimal maxOrderQuantity;
    private BigDecimal stepOrderQuantity;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductEntity() {
    }



    @Fields({
        @Field(name = "code", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES),
        @Field(name = "code_stem", index = Index.YES, analyze = Analyze.YES, norms = Norms.NO, store = Store.NO)
    })
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public Date getAvailablefrom() {
        return this.availablefrom;
    }

    public void setAvailablefrom(Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public Date getAvailableto() {
        return this.availableto;
    }

    /** {@inheritDoc} */
    public void setAvailableto(Date availableto) {
        this.availableto = availableto;
    }

    /** {@inheritDoc} */
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES),
            @Field(name = "name_sort", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)})
    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Fields({
        @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES),
        @Field(name = "displayNameAsIs", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    })
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.DisplayNameBridge.class)
    public String getDisplayName() {
        return this.displayName;
    }

    /** {@inheritDoc} */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /** {@inheritDoc} */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDescriptionAsIs() {
        final StringBuilder builder = new StringBuilder();
        for (AttrValue attr : attributes) {
            if (attr.getAttribute().getCode().startsWith(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX)) {
                builder.append(getLocale(attr.getAttribute().getCode()));
                builder.append(StringI18NModel.SEPARATOR);
                builder.append(attr.getVal());
                builder.append(StringI18NModel.SEPARATOR);
            }
        }
        return builder.toString();
    }

    String getLocale(final String attrCode) {
        return attrCode.substring(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX.length());
    }

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.BrandBridge.class)
    public Brand getBrand() {
        return this.brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES,
    bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductTypeValueBridge.class))
    public ProductType getProducttype() {
        return this.producttype;
    }

    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public int getAvailability() {
        return this.availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @Field(bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.AttributeValueBridge.class))
    public Set<AttrValueProduct> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Set<AttrValueProduct> attributes) {
        this.attributes = attributes;
    }

    @Field(bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductCategoryBridge.class))
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Fields({
        @Field(bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductSkuBridge.class)),
        @Field(name = "qtyOnWarehouse", store = Store.YES, analyze = Analyze.NO,
                bridge = @FieldBridge(impl = org.yes.cart.domain.entity.bridge.SkuWarehouseBridge.class))
    })
    public Collection<ProductSku> getSku() {
        return this.sku;
    }

    public void setSku(Collection<ProductSku> sku) {
        this.sku = sku;
    }

    public Set<ProductEnsebleOption> getEnsebleOption() {
        return this.ensebleOption;
    }

    public void setEnsebleOption(Set<ProductEnsebleOption> ensebleOption) {
        this.ensebleOption = ensebleOption;
    }

    public Set<ProductAssociation> getProductAssociations() {
        return this.productAssociations;
    }

    public void setProductAssociations(Set<ProductAssociation> productAssociations) {
        this.productAssociations = productAssociations;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public Boolean getFeatured() {
        return this.featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @DateBridge(resolution = Resolution.MINUTE)
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @DocumentId
    public long getProductId() {
        return this.productId;
    }

    public long getId() {
        return this.productId;
    }


    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public Map<String, BigDecimal> getQtyOnWarehouse(final Collection<Warehouse> warehouses) {
        final Map<String, BigDecimal> qty = new HashMap<String, BigDecimal>();
        for (ProductSku sku : getSku()) {
            qty.put(sku.getCode(), sku.getQty(warehouses));
        }
        return qty;
    }

    /**
     * Get default image, which is stored into lucene index, to reduce db hit.
     * @return default product image if found, otherwise no image constant.
     */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDefaultImage() {
        final AttrValue attr = getAttributeByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (attr == null || StringUtils.isBlank(attr.getVal())) {

            return Constants.NO_IMAGE;

        }
        return attr.getVal();
    }

    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES,
            bridge = @FieldBridge(impl = ProductSkuCodeBridge.class))
    public ProductSku getDefaultSku() {
        if (defaultProductSku == null) {
            if (this.getSku() != null && !this.getSku().isEmpty()) {
                if (isMultiSkuProduct()) { //multisku
                    for (ProductSku productSku : this.getSku()) {
                        if (productSku.getCode().endsWith(this.getCode())) {
                            defaultProductSku = productSku;
                        }
                    }
                    if (defaultProductSku == null) { // if there is no matching one - then take the first one
                        defaultProductSku = this.getSku().iterator().next();
                    }
                } else { //single sku
                    defaultProductSku = this.getSku().iterator().next();
                }
            }
        }
        return defaultProductSku;
    }

    public Collection<AttrValueProduct> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProduct> result = new ArrayList<AttrValueProduct>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueProduct attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attrValue.getAttribute().getCode() != null && attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    rez.put(attrValue.getAttribute().getCode(), attrValue);
                }
            }
        }
        return rez;
    }

    public AttrValueProduct getAttributeByCode(final String attributeCode) {
        if (attributeCode != null) {
            if (this.attributes != null) {
                for (AttrValueProduct attrValue : this.attributes) {
                    if (attrValue.getAttribute() != null && attrValue.getAttribute().getCode() != null && attrValue.getAttribute().getCode().equals(attributeCode)) {
                        return attrValue;
                    }
                }
            }
        }
        return null;
    }

    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<AttrValue>(attributes);
    }

    public ProductSku getSku(final String skuCode) {
        return getDefaultSku();
    }

    public boolean isMultiSkuProduct() {
        return sku.size() > 1;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getProductId();
    }


    /** {@inheritDoc} */
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    /** {@inheritDoc} */
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

}


