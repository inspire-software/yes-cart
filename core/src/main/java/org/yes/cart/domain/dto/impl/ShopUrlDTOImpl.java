/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

    @DtoField(value = "storeUrlId", readOnly = true)
    private long storeUrlId;

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    /** {@inheritDoc}  */
    public String getUrl() {
        return url;
    }

    /** {@inheritDoc}  */
    public void setUrl(final String url) {
        this.url = url;
    }

    /** {@inheritDoc}  */
    public long getStoreUrlId() {
        return storeUrlId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return storeUrlId;
    }

    /** {@inheritDoc}  */
    public void setStoreUrlId(final long storeUrlId) {
        this.storeUrlId = storeUrlId;
    }

    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc}  */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }
}
