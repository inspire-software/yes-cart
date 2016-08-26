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

import java.util.List;

/**
 * User: denispavlov
 */
@Dto
public class VoProductTypeViewGroup {


    @DtoField(value = "prodTypeAttributeViewGroupId", readOnly = true)
    private long prodTypeAttributeViewGroupId;


    @DtoField(value = "producttypeId")
    private long producttypeId;

    @DtoField(value = "attrCodeList", converter = "CSVToList")
    private List<String> attrCodeList;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    public long getProdTypeAttributeViewGroupId() {
        return prodTypeAttributeViewGroupId;
    }

    public void setProdTypeAttributeViewGroupId(final long prodTypeAttributeViewGroupId) {
        this.prodTypeAttributeViewGroupId = prodTypeAttributeViewGroupId;
    }

    public long getProducttypeId() {
        return producttypeId;
    }

    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    public List<String> getAttrCodeList() {
        return attrCodeList;
    }

    public void setAttrCodeList(final List<String> attrCodeList) {
        this.attrCodeList = attrCodeList;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }
}
