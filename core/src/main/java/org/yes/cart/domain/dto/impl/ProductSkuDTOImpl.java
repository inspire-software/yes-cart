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
import org.yes.cart.domain.dto.SeoDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.impl.AttrValueEntityProductSku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

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


    @DtoField(value = "skuId")
    private long skuId;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

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


    @DtoCollection(
            value="attribute",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueProductSkuDTO",
            entityGenericType = AttrValueEntityProductSku.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = AttrValueProductSkuMatcher.class,
            readOnly = true
            )
    private Collection<AttrValueProductSkuDTO> attribute;

    @DtoField(value = "seo", dtoBeanKey = "org.yes.cart.domain.dto.SeoDTO", readOnly = true)
    private SeoDTO seoDTO;


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

    /** {@inheritDoc}*/
    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }

    /** {@inheritDoc}*/
    public SeoDTO getSeoDTO() {
        return seoDTO;
    }

    /** {@inheritDoc}*/
    public void setSeoDTO(final SeoDTO seoDTO) {
        this.seoDTO = seoDTO;
    }

    /** {@inheritDoc}*/
    public Collection<SkuPriceDTO> getPrice() {
        return price;
    }

    /** {@inheritDoc}*/
    public void setPrice(final Collection<SkuPriceDTO> price) {
        this.price = price;
    }

    /** {@inheritDoc}*/
    public Collection<AttrValueProductSkuDTO> getAttribute() {
        return attribute;
    }

    /** {@inheritDoc}*/
    public void setAttribute(final Collection<AttrValueProductSkuDTO> attribute) {
        this.attribute = attribute;
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
}
