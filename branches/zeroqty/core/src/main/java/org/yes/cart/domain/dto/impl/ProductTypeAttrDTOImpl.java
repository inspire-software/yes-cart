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
import com.inspiresoftware.lib.dto.geda.annotations.DtoParent;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;

/**
 */
@Dto
public class ProductTypeAttrDTOImpl implements ProductTypeAttrDTO {


    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productTypeAttrId")
    private long productTypeAttrId;




    @DtoField(value = "attribute",
            dtoBeanKey = "org.yes.cart.domain.dto.AttributeDTO",
            entityBeanKeys = "org.yes.cart.domain.entity.Attribute")
    @DtoParent(value = "attributeId", retriever = "attributeDTO2Attribute")
    private AttributeDTO attributeDTO;

    @DtoField(
            value = "producttype",
            converter = "producttypeId2ProductType",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType"
    )
    private long producttypeId;


    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "visible")
    private boolean visible;

    @DtoField(value = "similarity")
    private boolean similarity;

    @DtoField(value = "navigation")
    private boolean navigation;

    @DtoField(value = "navigationType")
    private String navigationType;

    @DtoField(value = "rangeNavigation")
    private String rangeNavigation;


    /** {@inheritDoc} */
    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

     /**
     * {@inheritDoc}
     */
    public long getId() {
        return productTypeAttrId;
    }

    /** {@inheritDoc} */
    public void setProductTypeAttrId(final long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
    }

    /** {@inheritDoc} */
    public AttributeDTO getAttributeDTO() {
        return attributeDTO;
    }

    /** {@inheritDoc} */
    public void setAttributeDTO(final AttributeDTO attributeDTO) {
        this.attributeDTO = attributeDTO;
    }

    /** {@inheritDoc} */
    public long getProducttypeId() {
        return producttypeId;
    }

    /** {@inheritDoc} */
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    /** {@inheritDoc} */
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    public boolean isVisible() {
        return visible;
    }

    /** {@inheritDoc} */
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    public boolean isSimilarity() {
        return similarity;
    }

    /** {@inheritDoc} */
    public void setSimilarity(final boolean similarity) {
        this.similarity = similarity;
    }

    /** {@inheritDoc} */
    public boolean isNavigation() {
        return navigation;
    }

    /** {@inheritDoc} */
    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

    /** {@inheritDoc} */
    public String getNavigationType() {
        return navigationType;
    }

    /** {@inheritDoc} */
    public void setNavigationType(final String navigationType) {
        this.navigationType = navigationType;
    }

    /** {@inheritDoc} */
    public String getRangeNavigation() {
        return rangeNavigation;
    }

    /** {@inheritDoc} */
    public void setRangeNavigation(final String rangeNavigation) {
        this.rangeNavigation = rangeNavigation;
    }

    @Override
    public String toString() {
        return "ProductTypeAttrDTOImpl{" +
                "productTypeAttrId=" + productTypeAttrId +
                ", attributeDTO=" + attributeDTO +
                ", producttypeId=" + producttypeId +
                ", rank=" + rank +
                ", visible=" + visible +
                ", similarity=" + similarity +
                ", navigation=" + navigation +
                ", navigationType='" + navigationType + '\'' +
                ", rangeNavigation='" + rangeNavigation + '\'' +
                '}';
    }
}
