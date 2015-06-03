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
import org.yes.cart.domain.dto.ShopCategoryDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ShopCategoryDTOImpl  implements ShopCategoryDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "shopCategoryId", readOnly = true)
    private long shopCategoryId;

    @DtoField(value = "rank", readOnly = true)
    private int rank;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "category.categoryId", readOnly = true)
    private long categoryId;

    /** {@inheritDoc} */
    public long getShopCategoryId() {
        return shopCategoryId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return shopCategoryId;
    }

    /** {@inheritDoc} */
    public void setShopCategoryId(final long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
    }

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public long getCategoryId() {
        return categoryId;
    }

    /** {@inheritDoc} */
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ShopCategoryDTOImpl{" +
                "shopCategoryId=" + shopCategoryId +
                ", rank=" + rank +
                ", shopId=" + shopId +
                ", categoryId=" + categoryId +
                '}';
    }
}
