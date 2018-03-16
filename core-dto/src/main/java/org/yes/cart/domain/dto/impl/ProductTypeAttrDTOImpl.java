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
import com.inspiresoftware.lib.dto.geda.annotations.DtoParent;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;

/**
 */
@Dto
public class ProductTypeAttrDTOImpl implements ProductTypeAttrDTO {


    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productTypeAttrId", readOnly = true)
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

    @DtoField(value = "store", readOnly = true)
    private boolean store;

    @DtoField(value = "search", readOnly = true)
    private boolean search;

    @DtoField(value = "primary", readOnly = true)
    private boolean primary;

    @DtoField(value = "navigation", readOnly = true)
    private boolean navigation;

    @DtoField(value = "navigationTemplate")
    private String navigationTemplate;

    @DtoField(value = "navigationType")
    private String navigationType;

    @DtoField(value = "rangeNavigation")
    private String rangeNavigation;


    /** {@inheritDoc} */
    @Override
    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

     /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return productTypeAttrId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProductTypeAttrId(final long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
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
    public long getProducttypeId() {
        return producttypeId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    /** {@inheritDoc} */
    @Override
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc} */
    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSimilarity() {
        return similarity;
    }

    /** {@inheritDoc} */
    @Override
    public void setSimilarity(final boolean similarity) {
        this.similarity = similarity;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStore() {
        return store;
    }

    /** {@inheritDoc} */
    @Override
    public void setStore(final boolean store) {
        this.store = store;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSearch() {
        return search;
    }

    /** {@inheritDoc} */
    @Override
    public void setSearch(final boolean search) {
        this.search = search;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPrimary() {
        return primary;
    }

    /** {@inheritDoc} */
    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNavigation() {
        return navigation;
    }

    /** {@inheritDoc} */
    @Override
    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

    /** {@inheritDoc} */
    @Override
    public String getNavigationTemplate() {
        return navigationTemplate;
    }

    /** {@inheritDoc} */
    @Override
    public void setNavigationTemplate(final String navigationTemplate) {
        this.navigationTemplate = navigationTemplate;
    }

    /** {@inheritDoc} */
    @Override
    public String getNavigationType() {
        return navigationType;
    }

    /** {@inheritDoc} */
    @Override
    public void setNavigationType(final String navigationType) {
        this.navigationType = navigationType;
    }

    /** {@inheritDoc} */
    @Override
    public String getRangeNavigation() {
        return rangeNavigation;
    }

    /** {@inheritDoc} */
    @Override
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
                ", store=" + store +
                ", search=" + search +
                ", primary=" + primary +
                ", navigation=" + navigation +
                ", navigationTemplate='" + navigationTemplate + '\'' +
                ", navigationType='" + navigationType + '\'' +
                ", rangeNavigation='" + rangeNavigation + '\'' +
                '}';
    }
}
