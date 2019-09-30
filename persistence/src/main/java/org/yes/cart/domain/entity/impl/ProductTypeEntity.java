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


import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductTypeEntity implements org.yes.cart.domain.entity.ProductType, java.io.Serializable {

    private long producttypeId;
    private long version;

    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private Collection<ProdTypeAttributeViewGroup> attributeViewGroup = new ArrayList<>(0);
    private Collection<ProductTypeAttr> attributes = new ArrayList<>(0);
    private String uitemplate;
    private String uisearchtemplate;
    private boolean service;
    private boolean ensemble;
    private boolean shippable = true; // New types are shippable by default
    private boolean digital;
    private boolean downloadable;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductTypeEntity() {
    }



    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public Collection<ProdTypeAttributeViewGroup> getAttributeViewGroup() {
        return this.attributeViewGroup;
    }

    @Override
    public void setAttributeViewGroup(final Collection<ProdTypeAttributeViewGroup> attributeViewGroup) {
        this.attributeViewGroup = attributeViewGroup;
    }

    @Override
    public Collection<ProductTypeAttr> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Collection<ProductTypeAttr> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getUitemplate() {
        return this.uitemplate;
    }

    @Override
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @Override
    public String getUisearchtemplate() {
        return this.uisearchtemplate;
    }

    @Override
    public void setUisearchtemplate(final String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    @Override
    public boolean isService() {
        return this.service;
    }

    @Override
    public void setService(final boolean service) {
        this.service = service;
    }

    @Override
    public boolean isEnsemble() {
        return this.ensemble;
    }

    @Override
    public void setEnsemble(final boolean ensemble) {
        this.ensemble = ensemble;
    }

    @Override
    public boolean isShippable() {
        return this.shippable;
    }

    @Override
    public void setShippable(final boolean shippable) {
        this.shippable = shippable;
    }

    @Override
    public boolean isDigital() {
        return this.digital;
    }

    @Override
    public void setDigital(final boolean digital) {
        this.digital = digital;
    }

    @Override
    public boolean isDownloadable() {
        return this.downloadable;
    }

    @Override
    public void setDownloadable(final boolean downloadable) {
        this.downloadable = downloadable;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getProducttypeId() {
        return this.producttypeId;
    }

    @Override
    public long getId() {
        return this.producttypeId;
    }

    @Override
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    @Override
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


