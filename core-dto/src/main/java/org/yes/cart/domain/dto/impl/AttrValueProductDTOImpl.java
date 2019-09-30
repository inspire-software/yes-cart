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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.AttributeDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class AttrValueProductDTOImpl implements AttrValueProductDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "attrvalueId", readOnly = true)
    private long attrvalueId;

    @DtoField(value = "val")
    private String val;

    @DtoField(value = "indexedVal")
    private String indexedVal;

    @DtoField(value = "displayVal", converter = "i18nModelConverter")
    private Map<String, String> displayVals;

    @DtoVirtualField(converter = "attributeDTO2Code")
    private AttributeDTO attributeDTO;

    @DtoField(value = "product.productId", readOnly = true)
    private long productId;

    /** {@inheritDoc} */
    @Override
    public long getAttrvalueId() {
        return attrvalueId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return attrvalueId;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttrvalueId(final long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    /** {@inheritDoc} */
    @Override
    public String getVal() {
        return val;
    }

    /** {@inheritDoc} */
    @Override
    public void setVal(final String val) {
        this.val = val;
    }

    /** {@inheritDoc} */
    @Override
    public String getIndexedVal() {
        return indexedVal;
    }

    /** {@inheritDoc} */
    @Override
    public void setIndexedVal(final String indexedVal) {
        this.indexedVal = indexedVal;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayVals() {
        return displayVals;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayVals(final Map<String, String> displayVals) {
        this.displayVals = displayVals;
    }

    /** {@inheritDoc} */
    @Override
    public AttributeDTO getAttributeDTO() {
        return attributeDTO;
    }

    /** {@inheritDoc} */
    @Override
    public void setAttributeDTO(final AttributeDTO attributeDTO) {
        this.attributeDTO = attributeDTO;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "AttrValueProductDTOImpl{" +
                "attrvalueId=" + attrvalueId +
                ", val='" + val + '\'' +
                ", attributeDTO=" + attributeDTO +
                ", productId=" + productId +
                '}';
    }
}
