package org.yes.cart.domain.entity;


import java.math.BigDecimal;

/**
 * Class represent the quantity of lod products in particular shop.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/4/11
 * Time: 11:18 PM
 */
public interface ShopTopSeller   extends Auditable {

    /**
     * @return {@link Shop}
     */
    Shop getShop();

    /**
     * @param shop {@link Shop} to set.
     */
    void setShop(Shop shop);


    /**
     *
     * @return  {@link Product}
     */
    Product getProduct();

    /**
     *
     * @param product set {@link Product}
     */
    void setProduct(Product product);

    /**
     * GEt quantity of sold items during specified period.
     * @return   quantity of sold
     */
    BigDecimal getCounter();

    /**
     * Set quantity of sold items.
     * @param counter qty of sold items
     */
    void setCounter(BigDecimal counter);

    /**
     * Get pk value.
     * @return pk value.
     */
    long getShopTopsellerId();

    /**
     * Set pk value.
     * @param shopTopsellerId pk value.
     */
    void setShopTopsellerId(long shopTopsellerId);
}
