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
import org.yes.cart.domain.misc.MutablePair;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 14:05
 */
@Dto
public class VoProductOption {

    @DtoField(value = "productoptionId", readOnly = true)
    private long productoptionId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "productId")
    private long productId;

    @DtoField(value = "quantity")
    private BigDecimal quantity;

    @DtoField(value = "mandatory")
    private boolean mandatory;

    @DtoField(value = "skuCode")
    private String skuCode;

    @DtoField(value = "attributeCode")
    private String attributeCode;

    @DtoField(value = "optionSkuCodes", converter = "ListPairsToListMPairs")
    private List<MutablePair<String, String>> optionSkuCodes;

    public long getProductoptionId() {
        return productoptionId;
    }

    public void setProductoptionId(final long productoptionId) {
        this.productoptionId = productoptionId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
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

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public List<MutablePair<String, String>> getOptionSkuCodes() {
        return optionSkuCodes;
    }

    public void setOptionSkuCodes(final List<MutablePair<String, String>> optionSkuCodes) {
        this.optionSkuCodes = optionSkuCodes;
    }
}
