/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ShopAliasDTO;

/**
 * User: denispavlov
 * Date: 28/02/2017
 * Time: 14:51
 */
@Dto
public class ShopAliasDTOImpl implements ShopAliasDTO {

    private static final long serialVersionUID = 20170228L;

    @DtoField(value = "alias")
    private String alias;

    @DtoField(value = "storeAliasId", readOnly = true)
    private long storeAliasId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    /** {@inheritDoc}  */
    public String getAlias() {
        return alias;
    }

    /** {@inheritDoc}  */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /** {@inheritDoc}  */
    public long getStoreAliasId() {
        return storeAliasId;
    }

    /** {@inheritDoc}  */
    public void setStoreAliasId(final long storeAliasId) {
        this.storeAliasId = storeAliasId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return storeAliasId;
    }

    /** {@inheritDoc}  */
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc}  */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }
}
