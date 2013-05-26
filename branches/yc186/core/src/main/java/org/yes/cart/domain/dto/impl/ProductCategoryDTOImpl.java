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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductCategoryDTO;

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

    @DtoField(value = "category.name", readOnly = true)
    private String categoryName;

    @DtoField(value = "rank")
    private int rank;

    /** {@inheritDoc} */
    public String getCategoryName() {
        return categoryName;
    }

    /** {@inheritDoc} */
    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    /** {@inheritDoc} */
    public long getProductCategoryId() {
        return productCategoryId;
    }

/**
     * {@inheritDoc}
     */
    public long getId() {
        return productCategoryId;
    }


    /** {@inheritDoc} */
    public void setProductCategoryId(final long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    /** {@inheritDoc} */
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    public long getCategoryId() {
        return categoryId;
    }

    /** {@inheritDoc} */
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
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
