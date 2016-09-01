package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: Denis Lozenko
 * Date: 8/23/2016.
 */
@Dto
public class VoManagerShop {

    @DtoField(value = "manager.managerId", readOnly = true)
    private long managerId;
    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    public long getManagerId() {
        return managerId;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }
}
