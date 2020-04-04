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
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductOption;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductOptionEntity implements ProductOption, java.io.Serializable {

    private long productoptionId;
    private long version;

    private BigDecimal quantity = BigDecimal.ONE;
    private boolean mandatory;
    private Product product;
    private String skuCode;
    private int rank;
    private String attributeCode;
    private String optionSkuCodesInternal;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductOptionEntity() {
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public BigDecimal getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public void setProduct(final Product product) {
        this.product = product;
    }

    @Override
    public String getSkuCode() {
        return this.skuCode;
    }

    @Override
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String getAttributeCode() {
        return attributeCode;
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    @Override
    public List<String> getOptionSkuCodes() {
        return optionSkuCodesInternal != null ? Arrays.asList(StringUtils.split(optionSkuCodesInternal, ',')) : Collections.emptyList();
    }

    @Override
    public void setOptionSkuCodes(final List<String> skus) {
        if (skus == null || skus.isEmpty()) {
            this.optionSkuCodesInternal = null;
        } else {
            this.optionSkuCodesInternal = StringUtils.join(skus, ',');
        }
    }

    public String getOptionSkuCodesInternal() {
        return optionSkuCodesInternal;
    }

    public void setOptionSkuCodesInternal(final String optionSkuCodesInternal) {
        this.optionSkuCodesInternal = optionSkuCodesInternal;
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
    public long getProductoptionId() {
        return this.productoptionId;
    }

    @Override
    public long getId() {
        return this.productoptionId;
    }

    @Override
    public void setProductoptionId(final long productoptionId) {
        this.productoptionId = productoptionId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


