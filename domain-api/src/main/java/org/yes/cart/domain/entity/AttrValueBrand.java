package org.yes.cart.domain.entity;

/**
 *
 * Brans attribute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueBrand  extends AttrValue {

    Brand getBrand();

    void setBrand(Brand brand);
    
}
