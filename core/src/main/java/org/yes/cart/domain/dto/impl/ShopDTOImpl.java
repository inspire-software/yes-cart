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
import org.yes.cart.domain.dto.ShopDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ShopDTOImpl implements ShopDTO  {

    private static final long serialVersionUID = 20100528L;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "fspointer")
    private String fspointer;

    @DtoField(value = "imageVaultFolder")
    private String imageVaultFolder;

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    /** {@inheritDoc}*/
    public long getId() {
        return shopId;
    }


    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    public String getFspointer() {
        return fspointer;
    }

    /** {@inheritDoc} */
    public void setFspointer(final String fspointer) {
        this.fspointer = fspointer;
    }

    public long getShopId() {
        return shopId;
    }

    /** {@inheritDoc} */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /** {@inheritDoc} */
    public String getImageVaultFolder() {
        return imageVaultFolder;
    }

    /** {@inheritDoc} */
    public void setImageVaultFolder(final String imageVaultFolder) {
        this.imageVaultFolder = imageVaultFolder;
    }
}
