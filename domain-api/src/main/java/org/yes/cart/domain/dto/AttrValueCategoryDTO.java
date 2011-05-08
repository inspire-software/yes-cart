package org.yes.cart.domain.dto;

/**
 * Category attribute value.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueCategoryDTO extends AttrValueDTO {

    /**
     * Get the category id
     *
     * @return category id.
     */
    long getCategoryId();

    /**
     * Set category id.
     *
     * @param categoryId category id.
     */
    void setCategoryId(long categoryId);

}
