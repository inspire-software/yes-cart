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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: denispavlov
 * Date: 28/10/2014
 * Time: 17:26
 */
@Dto
public class VoTaxConfig {

    @DtoField(value = "taxConfigId", readOnly = true)
    private long taxConfigId;

    @DtoField(value = "taxId")
    private long taxId;
    @DtoField(value = "productCode")
    private String productCode;
    @DtoField(value = "stateCode")
    private String stateCode;
    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "guid", readOnly = true)
    private String guid;

    public long getTaxConfigId() {
        return taxConfigId;
    }

    public void setTaxConfigId(final long taxConfigId) {
        this.taxConfigId = taxConfigId;
    }

    public long getTaxId() {
        return taxId;
    }

    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }
}
