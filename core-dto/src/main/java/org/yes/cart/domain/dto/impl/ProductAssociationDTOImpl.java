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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;
import org.yes.cart.domain.dto.ProductAssociationDTO;

import java.time.Instant;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductAssociationDTOImpl implements ProductAssociationDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "productassociationId", readOnly = true)
    private long productassociationId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "association",
            /*dtoBeanKeys = "org.yes.cart.domain.dto.AssociationDTO",*/
            entityBeanKeys = "org.yes.cart.domain.entity.Association",
            converter = "associationDTO2Association"
    )
    private long associationId;


    @DtoField(
            value = "product",
            converter = "productId2Product",
            entityBeanKeys = "org.yes.cart.domain.entity.Product"
    )
    private long productId;

    @DtoField(value = "associatedSku")
    private String associatedCode;

    @DtoVirtualField(converter = "productAssociationSkuCodeToName", readOnly = true)
    private String associatedName;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

    /** {@inheritDoc} */
    @Override
    public long getProductassociationId() {
        return productassociationId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return productassociationId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProductassociationId(final long productassociationId) {
        this.productassociationId = productassociationId;
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
    public long getAssociationId() {
        return associationId;
    }

    /** {@inheritDoc} */
    @Override
    public void setAssociationId(final long associationId) {
        this.associationId = associationId;
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

    /** {@inheritDoc} */
    @Override
    public String getAssociatedCode() {
        return associatedCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setAssociatedCode(final String associatedCode) {
        this.associatedCode = associatedCode;
    }

    /** {@inheritDoc} */
    @Override
    public String getAssociatedName() {
        return associatedName;
    }

    /** {@inheritDoc} */
    @Override
    public void setAssociatedName(final String associatedName) {
        this.associatedName = associatedName;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "ProductAssociationDTOImpl{" +
                "productassociationId=" + productassociationId +
                ", rank=" + rank +
                ", associationId=" + associationId +
                ", productId=" + productId +
                ", associatedCode='" + associatedCode + '\'' +
                ", associatedName='" + associatedName + '\'' +
                '}';
    }
}
