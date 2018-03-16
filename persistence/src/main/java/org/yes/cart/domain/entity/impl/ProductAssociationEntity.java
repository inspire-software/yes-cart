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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Association;
import org.yes.cart.domain.entity.Product;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductAssociationEntity implements org.yes.cart.domain.entity.ProductAssociation, java.io.Serializable {

    private long productassociationId;
    private long version;

    private int rank;
    private Association association;
    private Product product;
    private String associatedSku;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductAssociationEntity() {
    }


    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public Association getAssociation() {
        return this.association;
    }

    @Override
    public void setAssociation(Association association) {
        this.association = association;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String getAssociatedSku() {
        return associatedSku;
    }

    @Override
    public void setAssociatedSku(final String associatedSku) {
        this.associatedSku = associatedSku;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getProductassociationId() {
        return this.productassociationId;
    }

    @Override
    public long getId() {
        return this.productassociationId;
    }


    @Override
    public void setProductassociationId(long productassociationId) {
        this.productassociationId = productassociationId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


