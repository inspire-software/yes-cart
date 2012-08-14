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
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:15 PM
 */
@Dto
public class ProdTypeAttributeViewGroupDTOImpl  implements ProdTypeAttributeViewGroupDTO {


    private static final long serialVersionUID = 20120628L;


    @DtoField(value = "prodTypeAttributeViewGroupId", readOnly = true)
    private long prodTypeAttributeViewGroupId;


    @DtoField(
            value = "producttype",
            converter = "producttypeId2ProductType",
            entityBeanKeys = "org.yes.cart.domain.entity.ProductType"
    )
    private long producttypeId;

    @DtoField(value = "attrCodeList")
    private String attrCodeList;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter")
    private Map<String, String> displayNames;

    /** {@inheritDoc} */
    public long getId() {
        return prodTypeAttributeViewGroupId;
    }

    /** {@inheritDoc} */
    public long getProdTypeAttributeViewGroupId() {
        return prodTypeAttributeViewGroupId;
    }

    /** {@inheritDoc} */
    public void setProdTypeAttributeViewGroupId(final long prodTypeAttributeViewGroupId) {
        this.prodTypeAttributeViewGroupId = prodTypeAttributeViewGroupId;
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
    public String getAttrCodeList() {
        return attrCodeList;
    }

    /** {@inheritDoc} */
    public void setAttrCodeList(final String attrCodeList) {
        this.attrCodeList = attrCodeList;
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
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    @Override
    public String toString() {
        return "ProdTypeAttributeViewGroupDTOImpl{" +
                "prodTypeAttributeViewGroupId=" + prodTypeAttributeViewGroupId +
                ", producttypeId=" + producttypeId +
                ", attrCodeList='" + attrCodeList + '\'' +
                ", rank=" + rank +
                ", name='" + name + '\'' +
                '}';
    }
}
