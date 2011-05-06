package org.yes.cart.domain.dto;

/**
 * Product attribute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueProductDTO  extends AttrValueDTO {

    /**
     * Get the product id
     * @return product id.
     */
    long getProductId();

    /**
     * Set product id.
     * @param productId product id.
     */
    void setProductId(long productId);


}
