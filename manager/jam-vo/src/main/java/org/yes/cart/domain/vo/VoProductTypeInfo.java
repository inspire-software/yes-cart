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
public class VoProductTypeInfo {

    @DtoField(value = "producttypeId", readOnly = true)
    private long producttypeId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "guid")
    private String guid;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "uitemplate")
    private String uitemplate;

    @DtoField(value = "uisearchtemplate")
    private String uisearchtemplate;

    @DtoField(value = "service")
    private boolean service;

    @DtoField(value = "ensemble")
    private boolean ensemble;

    @DtoField(value = "shippable")
    private boolean shippable;

    @DtoField(value = "downloadable")
    private boolean downloadable;

    @DtoField(value = "digital")
    private boolean digital;

    public long getProducttypeId() {
        return producttypeId;
    }

    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUitemplate() {
        return uitemplate;
    }

    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    public String getUisearchtemplate() {
        return uisearchtemplate;
    }

    public void setUisearchtemplate(final String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    public boolean isService() {
        return service;
    }

    public void setService(final boolean service) {
        this.service = service;
    }

    public boolean isEnsemble() {
        return ensemble;
    }

    public void setEnsemble(final boolean ensemble) {
        this.ensemble = ensemble;
    }

    public boolean isShippable() {
        return shippable;
    }

    public void setShippable(final boolean shippable) {
        this.shippable = shippable;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(final boolean downloadable) {
        this.downloadable = downloadable;
    }

    public boolean isDigital() {
        return digital;
    }

    public void setDigital(final boolean digital) {
        this.digital = digital;
    }
}
