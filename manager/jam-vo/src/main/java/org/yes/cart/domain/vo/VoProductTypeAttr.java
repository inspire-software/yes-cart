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
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;

/**
 * User: denispavlov
 */
@Dto
public class VoProductTypeAttr {


    @DtoField(value = "productTypeAttrId", readOnly = true)
    private long productTypeAttrId;

    @DtoField(value = "attributeDTO",
            dtoBeanKey = "VoAttribute",
            entityBeanKeys = { "org.yes.cart.domain.dto.AttributeDTO" }, readOnly = true)
    private VoAttribute attribute = new VoAttribute();

    @DtoField(value = "producttypeId")
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

    @DtoField(value = "navigationType")
    private String navigationType;

    @DtoVirtualField(converter = "ProductTypeAttrNavigationRanges")
    private VoProductTypeAttrNavigationRanges rangeNavigation;

    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

    public void setProductTypeAttrId(final long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
    }

    public VoAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(final VoAttribute attribute) {
        this.attribute = attribute;
    }

    public long getProducttypeId() {
        return producttypeId;
    }

    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public boolean isSimilarity() {
        return similarity;
    }

    public void setSimilarity(final boolean similarity) {
        this.similarity = similarity;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(final boolean store) {
        this.store = store;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(final boolean search) {
        this.search = search;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    public boolean isNavigation() {
        return navigation;
    }

    public void setNavigation(final boolean navigation) {
        this.navigation = navigation;
    }

    public String getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(final String navigationType) {
        this.navigationType = navigationType;
    }

    public VoProductTypeAttrNavigationRanges getRangeNavigation() {
        return rangeNavigation;
    }

    public void setRangeNavigation(final VoProductTypeAttrNavigationRanges rangeNavigation) {
        this.rangeNavigation = rangeNavigation;
    }
}
