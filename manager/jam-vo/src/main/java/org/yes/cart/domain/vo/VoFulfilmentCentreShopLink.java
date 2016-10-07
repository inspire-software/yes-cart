/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoFulfilmentCentreShopLink {

    @DtoField(value = "warehouse.warehouseId", readOnly = true)
    private long warehouseId;
    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;
    @DtoField(value = "disabled")
    private boolean disabled;

    public long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(final long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
}
