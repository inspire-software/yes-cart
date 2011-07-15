package org.yes.cart.domain.entity;


/**
 * Product enseble option.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface ProductEnsebleOption extends Auditable {

    /**
     */
    long getEnsembleOptId();

    void setEnsembleOptId(long ensembleOptId);

    /**
     */
    int getQty();

    void setQty(int qty);

    /**
     */
    Product getProduct();

    void setProduct(Product product);

    /**
     */
    ProductSku getSku();

    void setSku(ProductSku sku);

}


