package org.yes.cart.domain.entity;

/**
 * Product sku attribute value.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttrValueProductSku extends AttrValue {

    /**
     * Sku.
     * @return sku.
     */
    ProductSku getProductSku();

    /**
     * Set product sku.
     * @param productSku sku
     */
    void setProductSku(ProductSku productSku);

}