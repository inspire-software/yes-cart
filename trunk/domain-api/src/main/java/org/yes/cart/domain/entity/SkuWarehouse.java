package org.yes.cart.domain.entity;

import java.math.BigDecimal;

/**
 * Hold the quantity of {@link ProductSku} on each {@link Warehouse}.
 * The sku quantity became reserved when order paid or payment confirmed.
 * Quantity changed when reserved quantity delivered to customer.
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SkuWarehouse extends Auditable {

    /**
     * @return primary key
     */
    long getSkuWarehouseId();

    /**
     * Set primary key
     *
     * @param skuWarehouseId primary key
     */
    void setSkuWarehouseId(long skuWarehouseId);


    /**
     * Get the {@link Warehouse}.
     *
     * @return {@link Warehouse}
     */
    Warehouse getWarehouse();

    /**
     * Set {@link Warehouse}
     *
     * @param warehouse {@link Warehouse}
     */
    void setWarehouse(Warehouse warehouse);

    /**
     * Set {@link ProductSku}.
     *
     * @return {@link ProductSku}
     */
    ProductSku getSku();

    /**
     * Set {@link ProductSku}.
     *
     * @param sku {@link ProductSku}
     */
    void setSku(ProductSku sku);

    /**
     * Get the available quantity.
     *
     * @return available quantity.
     */
    BigDecimal getQuantity();

    /**
     * Set available quantity.
     *
     * @param quantity available quantity.
     */
    void setQuantity(BigDecimal quantity);

    /**
     * Get reserved quantity during payment transaction.
     *
     * @return reserved quantity during payment transaction.
     */
    BigDecimal getReserved();

    /**
     * Set reserved quantity during payment transaction.
     *
     * @param reserved reserved quantity during payment transaction.
     */
    void setReserved(BigDecimal reserved);

}
