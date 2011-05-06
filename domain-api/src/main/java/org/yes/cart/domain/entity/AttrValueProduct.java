package org.yes.cart.domain.entity;

/**
 *
 * Product attrbute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueProduct extends AttrValue {

    /**
     * Product.
     * @return product.
     */
    Product getProduct();

    /**
     * Set product.
     * @param product product.
     */
    void setProduct(Product product);

}
