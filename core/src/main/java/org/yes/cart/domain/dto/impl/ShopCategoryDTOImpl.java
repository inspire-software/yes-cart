package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
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
}
