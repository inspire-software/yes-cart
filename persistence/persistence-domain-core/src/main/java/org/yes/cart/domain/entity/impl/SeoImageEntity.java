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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SeoImageEntity implements org.yes.cart.domain.entity.SeoImage, java.io.Serializable {

    private long seoImageId;
    private long version;

    private String imageName;
    private String alt;
    private String displayAlt;
    private String title;
    private String displayTitleInternal;
    private I18NModel displayTitle;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public SeoImageEntity() {
    }



    @Override
    public String getImageName() {
        return this.imageName;
    }

    @Override
    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String getAlt() {
        return this.alt;
    }

    @Override
    public void setAlt(final String alt) {
        this.alt = alt;
    }

    @Override
    public String getDisplayAlt() {
        return displayAlt;
    }

    @Override
    public void setDisplayAlt(final String displayAlt) {
        this.displayAlt = displayAlt;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDisplayTitleInternal() {
        return displayTitleInternal;
    }

    public void setDisplayTitleInternal(final String displayTitleInternal) {
        this.displayTitleInternal = displayTitleInternal;
        this.displayTitle = new StringI18NModel(displayTitleInternal);
    }

    @Override
    public I18NModel getDisplayTitle() {
        return displayTitle;
    }

    @Override
    public void setDisplayTitle(final I18NModel displayTitle) {
        this.displayTitle = displayTitle;
        this.displayTitleInternal = displayTitle != null ? displayTitle.toString() : null;
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
    public long getSeoImageId() {
        return this.seoImageId;
    }


    @Override
    public long getId() {
        return this.seoImageId;
    }

    @Override
    public void setSeoImageId(final long seoImageId) {
        this.seoImageId = seoImageId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


