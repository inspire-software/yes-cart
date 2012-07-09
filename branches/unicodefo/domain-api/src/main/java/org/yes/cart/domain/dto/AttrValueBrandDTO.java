package org.yes.cart.domain.dto;

/**
 * Brand attribute value.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueBrandDTO extends AttrValueDTO {

    /**
     * Get the brand id
     *
     * @return brand id.
     */
    long getBrandId();

    /**
     * Set brand id.
     *
     * @param brandId brand id.
     */
    void setBrandId(long brandId);


}
