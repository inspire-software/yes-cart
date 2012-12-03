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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueProductSkuDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.domain.dto.matcher.impl.AttrValueProductSkuMatcher;
import org.yes.cart.domain.dto.matcher.impl.SkuPriceMatcher;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.SkuPrice;

import java.io.Serializable;
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
public class ProductSkuDTOImpl implements ProductSkuDTO, Serializable {

    private static final long serialVersionUID = 20100602L;


    @DtoField(value = "skuId", readOnly = true)
    private long skuId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
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




    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.dto.AttrValueProductSkuDTO",
            entityGenericType = AttrValueProductSku.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = AttrValueProductSkuMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueProductSkuDTO> attributes;



    @DtoCollection(
            value = "skuPrice", readOnly = true,
            dtoBeanKey = "org.yes.cart.domain.dto.SkuPriceDTO",
            entityGenericType = SkuPrice.class,
            dtoToEntityMatcher = SkuPriceMatcher.class
    )
    private Collection<SkuPriceDTO> price;

    /**
     * {@inheritDoc}
     */
    public long getSkuId() {
        return skuId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return skuId;
    }

    /**
     * {@inheritDoc}
     */
    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /**
     * {@inheritDoc}
     */
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc}
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }


    /**
     * {@inheritDoc}
     */
    public Collection<SkuPriceDTO> getPrice() {
        return price;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrice(final Collection<SkuPriceDTO> price) {
        this.price = price;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<AttrValueProductSkuDTO> getAttributes() {
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributes(final Collection<AttrValueProductSkuDTO> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
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
                ", price=" + price +
                '}';
    }
}
