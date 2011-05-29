package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * DTO for {@link org.yes.cart.domain.entity.CustomerWishList}
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerWishListDTO extends Unique {


    /**
     * Primary key value.
     *
     * @return key value.
     */
    long getCustomerwishlistId();

    /**
     * Set key value
     *
     * @param customerwishlistId value to set.
     */
    void setCustomerwishlistId(long customerwishlistId);


    /**
     * @return sku primary key
     */
    long getSkuId();

    /**
     * Set primary key value.
     *
     * @param skuId primary key value.
     */
    void setSkuId(long skuId);

    /**
     * Get the sku code.
     *
     * @return sku code
     */
    String getSkuCode();

    /**
     * Stock keeping unit code.
     * Limitation code must not contains underscore
     *
     * @param skuCode sku code
     */
    void setSkuCode(String skuCode);


    /**
     * Get the sku name.
     *
     * @return sku name
     */
    String getSkuName();

    /**
     * Stock keeping unit name.
     * sku name
     *
     * @param skuName sku name
     */
    void setSkuName(String skuName);


    /**
     * Get type of wsih list item.
     *
     * @return type of wsih list item
     */
    String getWlType();

    /**
     * Set type of wsih list item
     *
     * @param wlType type of wsih list item to set.
     */
    void setWlType(final String wlType);


    /**
     * Get customer id .
     *
     * @return customer id
     */
    long getCustomerId();


    /**
     * Set customer id.
     *
     * @param customerId customer id
     */
    void setCustomerId(long customerId);


}
