/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.domain.dto.ShopBackdoorUrlDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 26-Aug-2013
 * Time: 14:12:54
 */
@Dto
public class ShopBackdoorUrlDTOImpl implements ShopBackdoorUrlDTO {

    private static final long serialVersionUID = 20100528L;

    @DtoField(value = "url")
    private String url;

    @DtoField(value = "shopBackdoorUrlId", readOnly = true)
    private long shopBackdoorUrlId;

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
    public long getShopBackdoorUrlId() {
        return shopBackdoorUrlId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return shopBackdoorUrlId;
    }

    /** {@inheritDoc}  */
    public void setShopBackdoorUrlId(final long shopBackdoorUrlId) {
        this.shopBackdoorUrlId = shopBackdoorUrlId;
    }

    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc}  */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "ShopBackdoorUrlDTOImpl {" +
                "url='" + url + '\'' +
                ", shopBackdoorUrlId=" + shopBackdoorUrlId +
                ", shopId=" + shopId +
                '}';
    }
}
