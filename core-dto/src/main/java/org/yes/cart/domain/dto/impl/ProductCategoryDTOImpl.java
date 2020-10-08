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
import org.yes.cart.domain.dto.ProductCategoryDTO;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductCategoryDTOImpl implements ProductCategoryDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productCategoryId", readOnly = true)
    private long productCategoryId;

    @DtoField(value = "product.productId", readOnly = true)
    private long productId;

    @DtoField(value = "category.categoryId", readOnly = true)
    private long categoryId;

    @DtoField(value = "category.guid", readOnly = true)
    private String categoryCode;

    @DtoField(value = "category.name", readOnly = true)
    private String categoryName;

    @DtoField(value = "rank")
    private int rank;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /** {@inheritDoc} */
    @Override
    public String getCategoryCode() {
        return categoryCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setCategoryCode(final String categoryCode) {
        this.categoryCode = categoryCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    /** {@inheritDoc} */
    @Override
    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductCategoryId() {
        return productCategoryId;
    }

/**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return productCategoryId;
    }


    /** {@inheritDoc} */
    @Override
    public void setProductCategoryId(final long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    @Override
    public long getCategoryId() {
        return categoryId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /** {@inheritDoc} */
    @Override
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
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
    public String toString() {
        return "ProductCategoryDTOImpl{" +
                "productCategoryId=" + productCategoryId +
                ", productId=" + productId +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", rank=" + rank +
                '}';
    }
}
