package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerWishListDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CustomerWishListDTOImpl implements CustomerWishListDTO {

    @DtoField(value = "customerwishlistId", readOnly = true)
    private long customerwishlistId;

    @DtoField(value = "skus.skuId", readOnly = true)
    private long skuId;

    @DtoField(value = "skus.code", readOnly = true)
    private String skuCode;

    @DtoField(value = "skus.name", readOnly = true)
    private String skuName;

    @DtoField(value = "wlType")
    private String wlType;

    @DtoField(value = "customer.customerId", readOnly = true)
    private long customerId;

    /** {@inheritDoc} */
    public long getCustomerwishlistId() {
        return customerwishlistId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return customerwishlistId;
    }

    /** {@inheritDoc} */
    public void setCustomerwishlistId(final long customerwishlistId) {
        this.customerwishlistId = customerwishlistId;
    }

    /** {@inheritDoc} */
    public long getSkuId() {
        return skuId;
    }

    /** {@inheritDoc} */
    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    /** {@inheritDoc} */
    public String getSkuCode() {
        return skuCode;
    }

    /** {@inheritDoc} */
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /** {@inheritDoc} */
    public String getSkuName() {
        return skuName;
    }

    /** {@inheritDoc} */
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /** {@inheritDoc} */
    public String getWlType() {
        return wlType;
    }

    /** {@inheritDoc} */
    public void setWlType(final String wlType) {
        this.wlType = wlType;
    }

    /** {@inheritDoc} */
    public long getCustomerId() {
        return customerId;
    }

    /** {@inheritDoc} */
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }
}
