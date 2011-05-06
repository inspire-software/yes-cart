package org.yes.cart.domain.entity;

/**
 *
 * Category attribute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueCategory extends AttrValue {

    /**
     * Get category.
     * @return category.
     */
    Category getCategory();

    /**
     * Set category.
     * @param category category.
     */
    void setCategory(Category category);

}
