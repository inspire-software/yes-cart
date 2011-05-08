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
    public long getEnsembleOptId();

    public void setEnsembleOptId(long ensembleOptId);

    /**
     */
    public int getQty();

    public void setQty(int qty);

    /**
     */
    public Product getProduct();

    public void setProduct(Product product);

    /**
     */
    public ProductSku getSku();

    public void setSku(ProductSku sku);

}


