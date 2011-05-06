package org.yes.cart.domain.dto;

/**
 *
 * Product sku attribute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueProductSkuDTO  extends AttrValueDTO {

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

}
