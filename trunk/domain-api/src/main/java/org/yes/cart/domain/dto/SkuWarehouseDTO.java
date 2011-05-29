package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represent sku quantity on particular warehouse.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SkuWarehouseDTO extends Unique {

    /**
     * Primary key.
     *
     * @return ph value.
     */
    public long getSkuWarehouseId();

    /**
     * Set pk value.
     *
     * @param skuWarehouseId value to set
     */
    public void setSkuWarehouseId(long skuWarehouseId);

    /**
     * The sku pk.
     *
     * @return sku pk.
     */
    public long getProductSkuId();

    /**
     * Set sku pk.
     *
     * @param productSkuId sku pk value.
     */
    public void setProductSkuId(long productSkuId);

    /**
     * Get sku code.
     *
     * @return sku code.
     */
    public String getSkuCode();

    /**
     * Set sku code.
     *
     * @param skuCode sku code.
     */
    public void setSkuCode(String skuCode);

    /**
     * Get sku name.
     *
     * @return sku name.
     */
    public String getSkuName();

    /**
     * Set sku name
     *
     * @param skuName sku name.
     */
    public void setSkuName(String skuName);

    /**
     * Get the warehouse id.
     *
     * @return warehouse id
     */
    public long getWarehouseId();

    /**
     * Set warehouse id.
     *
     * @param warehouseId warehouse id
     */
    public void setWarehouseId(long warehouseId);

    /**
     * Get warehouse code.
     *
     * @return warehouse code.
     */
    public String getWarehouseCode();

    /**
     * Set warehouse code.
     *
     * @param warehouseCode warehouse code.
     */
    public void setWarehouseCode(String warehouseCode);

    /**
     * Get warehouse name.
     *
     * @return warehouse name.
     */
    public String getWarehouseName();

    /**
     * Set warehouse name.
     *
     * @param warehouseName warehouse name.
     */
    public void setWarehouseName(String warehouseName);

    /**
     * Sku quantity.
     *
     * @return sku quantity.
     */
    public BigDecimal getQuantity();

    /**
     * Set sku quantity
     *
     * @param quantity ku quantity.
     */
    public void setQuantity(BigDecimal quantity);

}
