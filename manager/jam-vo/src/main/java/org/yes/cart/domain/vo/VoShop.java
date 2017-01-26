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
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by igor on 31.12.2015.
 */
@Dto
public class VoShop {

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;


    @DtoField(value = "disabled", readOnly = true)
    private boolean disabled;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "masterId")
    private Long masterId;

    @DtoField(value = "masterCode")
    private String masterCode;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "fspointer")
    private String fspointer;

    public String getFspointer() {
        return fspointer;
    }

    public void setFspointer(String fspointer) {
        this.fspointer = fspointer;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(final Long masterId) {
        this.masterId = masterId;
    }

    public String getMasterCode() {
        return masterCode;
    }

    public void setMasterCode(final String masterCode) {
        this.masterCode = masterCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("shopId", shopId)
            .append("code", code)
            .append("name", name)
            .append("description", description)
            .append("fspointer", fspointer)
            .build();
    }
}
