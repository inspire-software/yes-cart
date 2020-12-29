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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueProductSkuDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueProductSku;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Lightweigt product sku DTO.
 * <p/>
 * User: dogma
 * Date: Jan 23, 2011
 * Time: 12:13:40 AM
 */
@Dto
public class ProductSkuDTOImpl implements ProductSkuDTO {

    private static final long serialVersionUID = 20100602L;


    @DtoField(value = "skuId", readOnly = true)
    private long skuId;

    @DtoField(value = "guid")
    private String guid;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "manufacturerCode")
    private String manufacturerCode;

    @DtoField(value = "manufacturerPartCode")
    private String manufacturerPartCode;

    @DtoField(value = "supplierCode")
    private String supplierCode;

    @DtoField(value = "supplierCatalogCode")
    private String supplierCatalogCode;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "description")
    private String description;

    @DtoField(
            value = "product",
            converter = "productId2Product",
            entityBeanKeys = "org.yes.cart.domain.entity.Product"
    )
    private long productId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "barCode")
    private String barCode;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nModelConverter")
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nModelConverter")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nModelConverter")
    private Map<String, String> displayMetadescriptions;




    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.dto.AttrValueProductSkuDTO",
            entityGenericType = AttrValueProductSku.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueProductSkuDTO> attributes;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;


    /**
     * {@inheritDoc}
     */
    @Override
    public long getSkuId() {
        return skuId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return skuId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuid() {
        return guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBarCode() {
        return barCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AttrValueProductSkuDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributes(final Collection<AttrValueProductSkuDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }

        final ProductSkuDTOImpl that = (ProductSkuDTOImpl) otherObject;

        if (rank != that.rank) {
            return false;
        }
        if (skuId != that.skuId) {
            return false;
        }
        if (barCode != null ? !barCode.equals(that.barCode) : that.barCode != null) {
            return false;
        }
        if (!code.equals(that.code)) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (skuId ^ (skuId >>> 32));
        result = 31 * result + code.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (productId ^ (productId >>> 32));
        result = 31 * result + rank;
        result = 31 * result + (barCode != null ? barCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductSkuDTOImpl{" +
                "skuId=" + skuId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", productId=" + productId +
                ", rank=" + rank +
                ", barCode='" + barCode + '\'' +
                ", attribute=" + attributes +
                '}';
    }
}
