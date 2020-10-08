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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.time.Instant;
import java.util.List;

/**
 * User: denispavlov
 */
@Dto
public class VoAttrValue {

    @DtoField(value = "attrvalueId", readOnly = true)
    private long attrvalueId;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "displayVals", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayVals;


    @DtoField(value = "attributeDTO",
              dtoBeanKey = "VoAttribute",
              entityBeanKeys = { "org.yes.cart.domain.dto.AttributeDTO" }, readOnly = true)
    private VoAttribute attribute = new VoAttribute();

    private String valBase64Data;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;


    public long getAttrvalueId() {
        return attrvalueId;
    }

    public void setAttrvalueId(final long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    public String getVal() {
        return val;
    }

    public void setVal(final String val) {
        this.val = val;
    }

    public List<MutablePair<String, String>> getDisplayVals() {
        return displayVals;
    }

    public void setDisplayVals(final List<MutablePair<String, String>> displayVals) {
        this.displayVals = displayVals;
    }

    public VoAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(final VoAttribute attribute) {
        this.attribute = attribute;
    }

    public String getValBase64Data() {
        return valBase64Data;
    }

    public void setValBase64Data(final String valBase64Data) {
        this.valBase64Data = valBase64Data;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
