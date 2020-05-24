/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;
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

    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private String tag;
    private Brand brand;
    private ProductType producttype;

    private boolean notSoldSeparately;

    private Set<AttrValueProduct> attributes = new HashSet<>(0);
    private Set<ProductCategory> productCategory = new HashSet<>(0);
    private Collection<ProductSku> sku = new ArrayList<>(0);
    private ProductOptionsEntity optionsInternal = new ProductOptionsEntity();
    private Set<ProductAssociation> productAssociations = new HashSet<>(0);
    private SeoEntity seoInternal;
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
    public void setCode(final String code) {
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

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    /** {@inheritDoc} */
    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
    }

    /** {@inheritDoc} */
    @Override
    public I18NModel getDisplayDescription() {
        final StringI18NModel model = new StringI18NModel();
        for (AttrValue attr : attributes) {
            if (attr.getAttributeCode() != null &&
                    attr.getAttributeCode().startsWith(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX)) {
                model.putValue(getLocale(attr.getAttributeCode()), attr.getVal());
            }
        }
        return model;
    }

    String getLocale(final String attrCode) {
        return attrCode.substring(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX.length());
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public Brand getBrand() {
        return this.brand;
    }

    @Override
    public void setBrand(final Brand brand) {
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
    public void setProducttype(final ProductType producttype) {
        this.producttype = producttype;
    }

    @Override
    public boolean getNotSoldSeparately() {
        return notSoldSeparately;
    }

    @Override
    public void setNotSoldSeparately(final boolean notSoldSeparately) {
        this.notSoldSeparately = notSoldSeparately;
    }

    @Override
    public Set<AttrValueProduct> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Set<AttrValueProduct> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    @Override
    public void setProductCategory(final Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public Collection<ProductSku> getSku() {
        return this.sku;
    }

    @Override
    public void setSku(final Collection<ProductSku> sku) {
        this.sku = sku;
    }

    @Override
    public Set<ProductAssociation> getProductAssociations() {
        return this.productAssociations;
    }

    @Override
    public void setProductAssociations(final Set<ProductAssociation> productAssociations) {
        this.productAssociations = productAssociations;
    }

    public ProductOptionsEntity getOptionsInternal() {
        return optionsInternal;
    }

    public void setOptionsInternal(final ProductOptionsEntity optionsInternal) {
        this.optionsInternal = optionsInternal;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(final SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
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
    public void setProductId(final long productId) {
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
                    int rank = Integer.MAX_VALUE;
                    for (ProductSku productSku : this.getSku()) {
                        if (productSku.getRank() < rank) {
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
        if (getSku() != null) {
            for (final ProductSku sku : getSku()) {
                if (sku.getCode().equals(skuCode)) {
                    return sku;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isMultiSkuProduct() {
        return sku.size() > 1;
    }


    @Override
    public ProductOptions getOptions() {
        ProductOptionsEntity options = getOptionsInternal();
        if (options == null) {
            options = new ProductOptionsEntity();
            options.setProduct(this);
            this.setOptionsInternal(options);
        }
        return options;
    }

    @Override
    public void setOptions(final ProductOptions options) {
        this.setOptionsInternal((ProductOptionsEntity) options);
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
    public String toString() {
        return this.getClass().getName() + this.getProductId();
    }

}


