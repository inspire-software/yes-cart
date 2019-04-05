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


import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.xml.ProductTypeRangeListXStreamProvider;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.stream.xml.XStreamProvider;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductTypeAttrEntity implements org.yes.cart.domain.entity.ProductTypeAttr, java.io.Serializable {

    private long productTypeAttrId;
    private long version;

    private Attribute attribute;
    private ProductType producttype;
    private int rank;
    private boolean visible;
    private boolean similarity;
    private String navigationTemplate;
    private String navigationType;
    private String rangeNavigation;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductTypeAttrEntity() {
    }


    @Override
    public Attribute getAttribute() {
        return this.attribute;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public ProductType getProducttype() {
        return this.producttype;
    }

    @Override
    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
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
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isSimilarity() {
        return this.similarity;
    }

    @Override
    public void setSimilarity(boolean similarity) {
        this.similarity = similarity;
    }

    @Override
    public boolean isStore() {
        return attribute.isStore();
    }

    @Override
    public void setStore(final boolean store) {
        throw new UnsupportedOperationException("Set this attribute on AttributeEntity");
    }

    @Override
    public boolean isSearch() {
        return attribute.isSearch();
    }

    @Override
    public void setSearch(final boolean search) {
        throw new UnsupportedOperationException("Set this attribute on AttributeEntity");
    }

    @Override
    public boolean isPrimary() {
        return attribute.isPrimary();
    }

    @Override
    public void setPrimary(final boolean primary) {
        throw new UnsupportedOperationException("Set this attribute on AttributeEntity");
    }

    @Override
    public boolean isNavigation() {
        return attribute.isNavigation();
    }

    @Override
    public void setNavigation(boolean navigation) {
        throw new UnsupportedOperationException("Set this attribute on AttributeEntity");
    }

    @Override
    public String getNavigationTemplate() {
        return navigationTemplate;
    }

    @Override
    public void setNavigationTemplate(final String navigationTemplate) {
        this.navigationTemplate = navigationTemplate;
    }

    @Override
    public String getNavigationType() {
        return this.navigationType;
    }

    @Override
    public void setNavigationType(String navigationType) {
        this.navigationType = navigationType;
    }

    @Override
    public String getRangeNavigation() {
        return this.rangeNavigation;
    }

    @Override
    public void setRangeNavigation(String rangeNavigation) {
        this.rangeNavigation = rangeNavigation;
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
    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

    @Override
    public long getId() {
        return this.productTypeAttrId;
    }


    @Override
    public void setProductTypeAttrId(long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    private static final XStreamProvider xStreamProvider = new ProductTypeRangeListXStreamProvider();

    private RangeList rangeListCache = null;

    @Override
    public RangeList getRangeList() {
        if (rangeListCache == null && StringUtils.isNotBlank(getRangeNavigation())) {
            rangeListCache = (RangeList) xStreamProvider.fromXML(getRangeNavigation());
        }
        return rangeListCache;
    }

    @Override
    public void setRangeList(final RangeList rangeList) {
        setRangeNavigation(xStreamProvider.toXML(rangeList));
        this.rangeListCache = rangeList;
    }

}


