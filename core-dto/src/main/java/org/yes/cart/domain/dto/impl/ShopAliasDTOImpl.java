/*
 * Copyright 2009 Inspire-Software.com
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

import java.time.Instant;

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


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /** {@inheritDoc}  */
    @Override
    public String getAlias() {
        return alias;
    }

    /** {@inheritDoc}  */
    @Override
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /** {@inheritDoc}  */
    @Override
    public long getStoreAliasId() {
        return storeAliasId;
    }

    /** {@inheritDoc}  */
    @Override
    public void setStoreAliasId(final long storeAliasId) {
        this.storeAliasId = storeAliasId;
    }

    /** {@inheritDoc}*/
    @Override
    public long getId() {
        return storeAliasId;
    }

    /** {@inheritDoc}  */
    @Override
    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc}  */
    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

}
