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
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoPaymentGatewayParameter {

    @DtoField(readOnly = true)
    private long paymentGatewayParameterId;
    @DtoField(readOnly = true)
    private String description;
    @DtoField(readOnly = true)
    private String label;
    @DtoField(readOnly = true)
    private String name;
    @DtoField()
    private String value;
    @DtoField(readOnly = true)
    private String pgLabel;

    public long getPaymentGatewayParameterId() {
        return paymentGatewayParameterId;
    }

    public void setPaymentGatewayParameterId(final long paymentGatewayParameterId) {
        this.paymentGatewayParameterId = paymentGatewayParameterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }
}
