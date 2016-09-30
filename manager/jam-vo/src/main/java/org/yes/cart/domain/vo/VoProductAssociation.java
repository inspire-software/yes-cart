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
 */
@Dto
public class VoProductAssociation {

    @DtoField(value = "productassociationId", readOnly = true)
    private long productassociationId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "associationId")
    private long associationId;

    @DtoField(value = "productId")
    private long productId;


    @DtoField(value = "associatedProductId")
    private long associatedProductId;

    @DtoField(value = "associatedCode")
    private String associatedCode;

    @DtoField(value = "associatedName")
    private String associatedName;

    @DtoField(value = "associatedDescription")
    private String associatedDescription;

    public long getProductassociationId() {
        return productassociationId;
    }

    public void setProductassociationId(final long productassociationId) {
        this.productassociationId = productassociationId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(final long associationId) {
        this.associationId = associationId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public long getAssociatedProductId() {
        return associatedProductId;
    }

    public void setAssociatedProductId(final long associatedProductId) {
        this.associatedProductId = associatedProductId;
    }

    public String getAssociatedCode() {
        return associatedCode;
    }

    public void setAssociatedCode(final String associatedCode) {
        this.associatedCode = associatedCode;
    }

    public String getAssociatedName() {
        return associatedName;
    }

    public void setAssociatedName(final String associatedName) {
        this.associatedName = associatedName;
    }

    public String getAssociatedDescription() {
        return associatedDescription;
    }

    public void setAssociatedDescription(final String associatedDescription) {
        this.associatedDescription = associatedDescription;
    }
}
