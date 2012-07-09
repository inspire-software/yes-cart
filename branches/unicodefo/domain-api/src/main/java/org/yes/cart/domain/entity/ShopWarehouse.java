package org.yes.cart.domain.entity;

/**
 * Relation between shop and warehouse.
 * TODO add usage priority in next releases
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopWarehouse extends Auditable {

    /**
     * @return primary key
     */
    long getShopWarehouseId();

    /**
     * Set primary key.
     *
     * @param shopWarehouseId primary key value.
     */
    void setShopWarehouseId(long shopWarehouseId);

    /**
     * @return {@link Shop}
     */
    Shop getShop();

    /**
     * @param shop {@link Shop} to set.
     */
    void setShop(Shop shop);

    /**
     * @return {@link Warehouse}
     */
    Warehouse getWarehouse();

    /**
     * Set {@link Warehouse}.
     *
     * @param warehouse {@link Warehouse} to use.
     */
    void setWarehouse(Warehouse warehouse);

    /**
     * Get the rank of warehouse usage in shop.
     * @return    rank of warehouse usage
     */
    int getRank();


    /**
     * Set the rank of warehouse usage.
     * @param rank of warehouse usage.
     */
    void setRank(int rank);


}
