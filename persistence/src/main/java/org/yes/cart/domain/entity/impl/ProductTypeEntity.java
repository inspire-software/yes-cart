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


import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductTypeAttr;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TPRODUCTTYPE"
)
public class ProductTypeEntity implements org.yes.cart.domain.entity.ProductType, java.io.Serializable {


    private String name;
    private String description;
    private Collection<ProdTypeAttributeViewGroup> attributeViewGroup = new ArrayList<ProdTypeAttributeViewGroup>(0);
    private Collection<ProductTypeAttr> attributes = new ArrayList<ProductTypeAttr>(0);
    private String uitemplate;
    private String uisearchtemplate;
    private boolean service;
    private boolean ensemble;
    private boolean shippable;
    private boolean digital;
    private boolean downloadable;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductTypeEntity() {
    }




    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCTTYPE_ID", nullable = false, updatable = false)
    public Collection<ProdTypeAttributeViewGroup> getAttributeViewGroup() {
        return this.attributeViewGroup;
    }

    public void setAttributeViewGroup(Collection<ProdTypeAttributeViewGroup> attributeViewGroup) {
        this.attributeViewGroup = attributeViewGroup;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "producttype")
    public Collection<ProductTypeAttr> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<ProductTypeAttr> attributes) {
        this.attributes = attributes;
    }

    @Column(name = "UITEMPLATE")
    public String getUitemplate() {
        return this.uitemplate;
    }

    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @Column(name = "UISEARCHTEMPLATE")
    public String getUisearchtemplate() {
        return this.uisearchtemplate;
    }

    public void setUisearchtemplate(String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    @Column(name = "SERVICE")
    public boolean isService() {
        return this.service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    @Column(name = "ENSEMBLE")
    public boolean isEnsemble() {
        return this.ensemble;
    }

    public void setEnsemble(boolean ensemble) {
        this.ensemble = ensemble;
    }

    @Column(name = "SHIPPABLE")
    public boolean isShippable() {
        return this.shippable;
    }

    public void setShippable(boolean shippable) {
        this.shippable = shippable;
    }

    @Column(name = "DIGITAL")
    public boolean isDigital() {
        return this.digital;
    }

    public void setDigital(boolean digital) {
        this.digital = digital;
    }

    @Column(name = "DOWNLOADABLE")
    public boolean isDownloadable() {
        return this.downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
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


    private long producttypeId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "PRODUCTTYPE_ID", nullable = false)
    public long getProducttypeId() {
        return this.producttypeId;
    }

    @Transient
    public long getId() {
        return this.producttypeId;
    }

    public void setProducttypeId(long producttypeId) {
        this.producttypeId = producttypeId;
    }

    @Transient
    public ProductTypeAttr getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (ProductTypeAttr attr : this.attributes) {
                if (attr.getAttribute() != null && attr.getAttribute().getCode() != null && attr.getAttribute().getCode().equals(attributeCode)) {
                    return attr;
                }
            }
        }
        return null;
    }


    // end of extra code specified in the hbm.xml files

}


