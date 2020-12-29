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
import org.yes.cart.domain.dto.ShopUrlDTO;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ShopUrlDTOImpl implements ShopUrlDTO {

    private static final long serialVersionUID = 20100528L;

    @DtoField(value = "url")
    private String url;

    @DtoField(value = "themeChain")
    private String themeChain;

    @DtoField(value = "primary")
    private boolean primary;

    @DtoField(value = "storeUrlId", readOnly = true)
    private long storeUrlId;

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
    public String getUrl() {
        return url;
    }

    /** {@inheritDoc}  */
    @Override
    public void setUrl(final String url) {
        this.url = url;
    }

    /** {@inheritDoc}  */
    @Override
    public String getThemeChain() {
        return themeChain;
    }

    /** {@inheritDoc}  */
    @Override
    public void setThemeChain(final String themeChain) {
        this.themeChain = themeChain;
    }

    /** {@inheritDoc}  */
    @Override
    public boolean isPrimary() {
        return primary;
    }

    /** {@inheritDoc}  */
    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    /** {@inheritDoc}  */
    @Override
    public long getStoreUrlId() {
        return storeUrlId;
    }

    /** {@inheritDoc}*/
    @Override
    public long getId() {
        return storeUrlId;
    }

    /** {@inheritDoc}  */
    @Override
    public void setStoreUrlId(final long storeUrlId) {
        this.storeUrlId = storeUrlId;
    }

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

    @Override
    public String toString() {
        return "ShopUrlDTOImpl{" +
                "url='" + url + '\'' +
                ", themeChain='" + themeChain + '\'' +
                ", storeUrlId=" + storeUrlId +
                ", shopId=" + shopId +
                '}';
    }
}
