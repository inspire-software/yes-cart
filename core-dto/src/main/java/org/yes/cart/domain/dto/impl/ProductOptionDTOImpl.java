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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductOptionDTO;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 13:09
 */
@Dto
public class ProductOptionDTOImpl implements ProductOptionDTO {

    private static final long serialVersionUID = 20200223L;

    @DtoField(value = "productoptionId", readOnly = true)
    private long productoptionId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(
            value = "product",
            converter = "productId2Product",
            entityBeanKeys = "org.yes.cart.domain.entity.Product"
    )
    private long productId;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    @DtoField(value = "mandatory")
    private boolean mandatory;

    @DtoField(value = "skuCode")
    private String skuCode;

    @DtoField(value = "attributeCode")
    private String attributeCode;

    @DtoField(value = "optionSkuCodes", converter = "productOptionSkuCodesToSkuCodeNamePair")
    private List<Pair<String, String>> optionSkuCodes;


    /**
     * {@inheritDoc}
     */
    @Override
    public long getProductoptionId() {
        return productoptionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProductoptionId(final long productoptionId) {
        this.productoptionId = productoptionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return productoptionId;
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
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeCode() {
        return attributeCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, String>> getOptionSkuCodes() {
        return optionSkuCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOptionSkuCodes(final List<Pair<String, String>> optionSkuCodes) {
        this.optionSkuCodes = optionSkuCodes;
    }

    @Override
    public String toString() {
        return "ProductOptionDTOImpl{" +
                "productoptionId=" + productoptionId +
                ", rank=" + rank +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", mandatory=" + mandatory +
                ", skuCode='" + skuCode + '\'' +
                ", attributeCode='" + attributeCode + '\'' +
                ", optionSkuCodes=" + optionSkuCodes +
                '}';
    }
}
