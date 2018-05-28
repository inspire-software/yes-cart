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
package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

public class ProductEntity implements org.yes.cart.domain.entity.Product, java.io.Serializable {

    private ProductSku defaultProductSku = null;
    private long productId;
    private long version;

    private String code;
    private String manufacturerCode;
    private String manufacturerPartCode;
    private String supplierCode;
    private String supplierCatalogCode;

    private String pimCode;
    private boolean pimDisabled;
    private boolean pimOutdated;
    private Instant pimUpdated;

    private boolean disabled;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private String name;
    private String displayName;
    private String description;
    private String tag;
    private Brand brand;
    private ProductType producttype;
    private int availability;
    private Set<AttrValueProduct> attributes = new HashSet<>(0);
    private Set<ProductCategory> productCategory = new HashSet<>(0);
    private Collection<ProductSku> sku = new ArrayList<>(0);
    private Set<ProductEnsembleOption> ensebleOption = new HashSet<>(0);
    private Set<ProductAssociation> productAssociations = new HashSet<>(0);
    private Boolean featured;
    private SeoEntity seoInternal;
    private BigDecimal minOrderQuantity;
    private BigDecimal maxOrderQuantity;
    private BigDecimal stepOrderQuantity;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductEntity() {
    }



    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    @Override
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    @Override
    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    @Override
    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    @Override
    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    @Override
    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    @Override
    public String getPimCode() {
        return pimCode;
    }

    @Override
    public void setPimCode(final String pimCode) {
        this.pimCode = pimCode;
    }

    @Override
    public boolean getPimDisabled() {
        return pimDisabled;
    }

    @Override
    public void setPimDisabled(final boolean pimDisabled) {
        this.pimDisabled = pimDisabled;
    }

    @Override
    public boolean getPimOutdated() {
        return pimOutdated;
    }

    @Override
    public void setPimOutdated(final boolean pimOutdated) {
        this.pimOutdated = pimOutdated;
    }

    @Override
    public Instant getPimUpdated() {
        return pimUpdated;
    }

    @Override
    public void setPimUpdated(final Instant pimUpdated) {
        this.pimUpdated = pimUpdated;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return this.availablefrom;
    }

    @Override
    public void setAvailablefrom(LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getAvailableto() {
        return this.availableto;
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableto(LocalDateTime availableto) {
        this.availableto = availableto;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescriptionAsIs() {
        final StringBuilder builder = new StringBuilder();
        for (AttrValue attr : attributes) {
            if (attr.getAttributeCode() != null &&
                    attr.getAttributeCode().startsWith(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX)) {
                builder.append(getLocale(attr.getAttributeCode()));
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

    public String getDescriptionStem() {
        final String localisedDescription = getDescriptionAsIs();
        final String description = getDescription();
        if (StringUtils.isNotBlank(localisedDescription)) {
            if (StringUtils.isNotBlank(description)) {
                return localisedDescription.replace(StringI18NModel.SEPARATOR, " ").concat(" ").concat(description);
            }
            return localisedDescription.replace(StringI18NModel.SEPARATOR, " ");
        }
        return description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public Brand getBrand() {
        return this.brand;
    }

    @Override
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getProducttypeId() {
        return String.valueOf(getProducttype().getProducttypeId());
    }

    @Override
    public ProductType getProducttype() {
        return this.producttype;
    }

    @Override
    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
    }

    @Override
    public int getAvailability() {
        return this.availability;
    }

    @Override
    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @Override
    public Set<AttrValueProduct> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Set<AttrValueProduct> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    @Override
    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public Collection<ProductSku> getSku() {
        return this.sku;
    }

    @Override
    public void setSku(Collection<ProductSku> sku) {
        this.sku = sku;
    }

    @Override
    public Set<ProductEnsembleOption> getEnsembleOption() {
        return this.ensebleOption;
    }

    @Override
    public void setEnsembleOption(Set<ProductEnsembleOption> ensembleOption) {
        this.ensebleOption = ensembleOption;
    }

    @Override
    public Set<ProductAssociation> getProductAssociations() {
        return this.productAssociations;
    }

    @Override
    public void setProductAssociations(Set<ProductAssociation> productAssociations) {
        this.productAssociations = productAssociations;
    }

    @Override
    public Boolean getFeatured() {
        return this.featured;
    }

    @Override
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getProductId() {
        return this.productId;
    }

    @Override
    public long getId() {
        return this.productId;
    }


    @Override
    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    /**
     * Get default image, which is stored into lucene index, to reduce db hit.
     * @return default product image if found, otherwise no image constant.
     */
    public String getDefaultImage() {
        final String attr = getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (StringUtils.isBlank(attr)) {

            return Constants.NO_IMAGE;

        }
        return attr;
    }

    public String getDefaultSkuCode() {
        final ProductSku sku = getDefaultSku();
        if (sku != null) {
            return sku.getCode();
        }
        return null;
    }

    @Override
    public ProductSku getDefaultSku() {
        if (defaultProductSku == null) {
            if (this.getSku() != null && !this.getSku().isEmpty()) {
                if (isMultiSkuProduct()) { //multisku
                    int rank = Integer.MIN_VALUE;
                    for (ProductSku productSku : this.getSku()) {
                        if (productSku.getRank() > rank) {
                            defaultProductSku = productSku;
                            rank = productSku.getRank();
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

    @Override
    public Collection<AttrValueProduct> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProduct> result = new ArrayList<>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueProduct attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttributeCode() != null) {
                    rez.put(attrValue.getAttributeCode(), attrValue);
                }
            }
        }
        return rez;
    }

    @Override
    public AttrValueProduct getAttributeByCode(final String attributeCode) {
        if (attributeCode != null) {
            if (this.attributes != null) {
                for (AttrValueProduct attrValue : this.attributes) {
                    if (attributeCode.equals(attrValue.getAttributeCode())) {
                        return attrValue;
                    }
                }
            }
        }
        return null;
    }



    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes);
    }

    @Override
    public ProductSku getSku(final String skuCode) {
        return getDefaultSku();
    }

    @Override
    public boolean isMultiSkuProduct() {
        return sku.size() > 1;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getProductId();
    }


    /** {@inheritDoc} */
    @Override
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    /** {@inheritDoc} */
    @Override
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    @Override
    public BigDecimal getMinOrderQuantity() {
        return minOrderQuantity;
    }

    @Override
    public void setMinOrderQuantity(final BigDecimal minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    @Override
    public BigDecimal getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    @Override
    public void setMaxOrderQuantity(final BigDecimal maxOrderQuantity) {
        this.maxOrderQuantity = maxOrderQuantity;
    }

    @Override
    public BigDecimal getStepOrderQuantity() {
        return stepOrderQuantity;
    }

    @Override
    public void setStepOrderQuantity(final BigDecimal stepOrderQuantity) {
        this.stepOrderQuantity = stepOrderQuantity;
    }

}


