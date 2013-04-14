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

package org.yes.cart.domain.entity.impl;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.xml.ProductTypeRangeListXStreamProvider;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.stream.xml.XStreamProvider;

import javax.persistence.*;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TPRODUCTTYPEATTR"
)
public class ProductTypeAttrEntity implements org.yes.cart.domain.entity.ProductTypeAttr, java.io.Serializable {


    private Attribute attribute;
    private ProductType producttype;
    private int rank;
    private boolean visible;
    private boolean similarity;
    private boolean navigation;
    private String navigationType;
    private String rangeNavigation;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductTypeAttrEntity() {
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "CODE", nullable = false)
    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "PRODUCTTYPE_ID", nullable = false)
    public ProductType getProducttype() {
        return this.producttype;
    }

    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
    }

    @Column(name = "RANK")
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Column(name = "VISIBLE")
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Column(name = "SIMILARITY")
    public boolean isSimilarity() {
        return this.similarity;
    }

    public void setSimilarity(boolean similarity) {
        this.similarity = similarity;
    }

    @Column(name = "NAV")
    public boolean isNavigation() {
        return this.navigation;
    }

    public void setNavigation(boolean navigation) {
        this.navigation = navigation;
    }

    @Column(name = "NAV_TYPE", length = 1)
    public String getNavigationType() {
        return this.navigationType;
    }

    public void setNavigationType(String navigationType) {
        this.navigationType = navigationType;
    }

    @Column(name = "RANGE_NAV", length = 4000)
    public String getRangeNavigation() {
        return this.rangeNavigation;
    }

    public void setRangeNavigation(String rangeNavigation) {
        this.rangeNavigation = rangeNavigation;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long productTypeAttrId;

// @GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})

    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "PRODUCTTYPEATTR_ID", nullable = false)
    public long getProductTypeAttrId() {
        return productTypeAttrId;
    }

    @Transient
    public long getId() {
        return this.productTypeAttrId;
    }


    public void setProductTypeAttrId(long productTypeAttrId) {
        this.productTypeAttrId = productTypeAttrId;
    }

    private static final XStreamProvider xStreamProvider = new ProductTypeRangeListXStreamProvider();

    private RangeList rangeListCache = null;

    @Transient
    public RangeList getRangeList() {
        if (rangeListCache == null && getRangeNavigation() != null) {
            rangeListCache = (RangeList) xStreamProvider.fromXML(getRangeNavigation());
        }
        return rangeListCache;
    }

    public void setRangeList(final RangeList rangeList) {
        setRangeNavigation(xStreamProvider.toXML(rangeList));
        this.rangeListCache = rangeList;
    }


    // end of extra code specified in the hbm.xml files

}


