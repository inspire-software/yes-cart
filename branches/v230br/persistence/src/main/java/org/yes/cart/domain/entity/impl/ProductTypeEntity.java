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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductTypeEntity implements org.yes.cart.domain.entity.ProductType, java.io.Serializable {

    private long producttypeId;
    private long version;

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



    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<ProdTypeAttributeViewGroup> getAttributeViewGroup() {
        return this.attributeViewGroup;
    }

    public void setAttributeViewGroup(Collection<ProdTypeAttributeViewGroup> attributeViewGroup) {
        this.attributeViewGroup = attributeViewGroup;
    }

    public Collection<ProductTypeAttr> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<ProductTypeAttr> attributes) {
        this.attributes = attributes;
    }

    public String getUitemplate() {
        return this.uitemplate;
    }

    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    public String getUisearchtemplate() {
        return this.uisearchtemplate;
    }

    public void setUisearchtemplate(String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    public boolean isService() {
        return this.service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public boolean isEnsemble() {
        return this.ensemble;
    }

    public void setEnsemble(boolean ensemble) {
        this.ensemble = ensemble;
    }

    public boolean isShippable() {
        return this.shippable;
    }

    public void setShippable(boolean shippable) {
        this.shippable = shippable;
    }

    public boolean isDigital() {
        return this.digital;
    }

    public void setDigital(boolean digital) {
        this.digital = digital;
    }

    public boolean isDownloadable() {
        return this.downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getProducttypeId() {
        return this.producttypeId;
    }

    public long getId() {
        return this.producttypeId;
    }

    public void setProducttypeId(long producttypeId) {
        this.producttypeId = producttypeId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

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

}


