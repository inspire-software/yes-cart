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
import org.yes.cart.domain.dto.TaxConfigDTO;

/**
 * User: denispavlov
 * Date: 28/10/2014
 * Time: 17:26
 */
@Dto
public class TaxConfigDTOImpl implements TaxConfigDTO {

    @DtoField(value = "taxConfigId", readOnly = true)
    private long taxConfigId;

    @DtoField(value = "tax.taxId", readOnly = true)
    private long taxId;
    @DtoField(value = "productCode")
    private String productCode;
    @DtoField(value = "stateCode")
    private String stateCode;
    @DtoField(value = "countryCode")
    private String countryCode;

    @DtoField(value = "guid", readOnly = true)
    private String guid;

    /** {@inheritDoc} */
    public long getId() {
        return taxConfigId;
    }

    /** {@inheritDoc} */
    public String getGuid() {
        return guid;
    }

    /** {@inheritDoc} */
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    /** {@inheritDoc} */
    public long getTaxConfigId() {
        return taxConfigId;
    }

    /** {@inheritDoc} */
    public void setTaxConfigId(final long taxConfigId) {
        this.taxConfigId = taxConfigId;
    }

    /** {@inheritDoc} */
    public long getTaxId() {
        return taxId;
    }

    /** {@inheritDoc} */
    public void setTaxId(final long taxId) {
        this.taxId = taxId;
    }

    /** {@inheritDoc} */
    public String getProductCode() {
        return productCode;
    }

    /** {@inheritDoc} */
    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    /** {@inheritDoc} */
    public String getStateCode() {
        return stateCode;
    }

    /** {@inheritDoc} */
    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    /** {@inheritDoc} */
    public String getCountryCode() {
        return countryCode;
    }

    /** {@inheritDoc} */
    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

}
