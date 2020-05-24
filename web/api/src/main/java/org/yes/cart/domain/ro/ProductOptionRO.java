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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 12:20
 */
@Dto
@XmlRootElement(name = "product-option")
public class ProductOptionRO implements Serializable {

    private static final long serialVersionUID = 20200223L;

    @DtoField(value = "productoptionId", readOnly = true)
    private long productoptionId;

    @DtoField(value = "quantity", readOnly = true)
    private BigDecimal quantity;
    @DtoField(value = "mandatory", readOnly = true)
    private boolean mandatory;
    @DtoField(value = "skuCode", readOnly = true)
    private String skuCode;
    @DtoField(value = "rank", readOnly = true)
    private int rank;
    @DtoField(value = "attributeCode", readOnly = true)
    private String attributeCode;
    @DtoField(value = "optionSkuCodes", readOnly = true)
    private List<String> optionSkuCodes;

    public long getProductoptionId() {
        return productoptionId;
    }

    public void setProductoptionId(final long productoptionId) {
        this.productoptionId = productoptionId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(final boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public List<String> getOptionSkuCodes() {
        return optionSkuCodes;
    }

    public void setOptionSkuCodes(final List<String> optionSkuCodes) {
        this.optionSkuCodes = optionSkuCodes;
    }
}
