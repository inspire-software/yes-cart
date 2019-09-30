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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductTypeDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductTypeDTOImpl implements ProductTypeDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "producttypeId", readOnly = true)
    private long producttypeId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

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

    /** {@inheritDoc} */
    @Override
    public boolean isDownloadable() {
        return downloadable;
    }

    /** {@inheritDoc} */
    @Override
    public void setDownloadable(final boolean downloadable) {
        this.downloadable = downloadable;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDigital() {
        return digital;
    }

    /** {@inheritDoc} */
    @Override
    public void setDigital(final boolean digital) {
        this.digital = digital;
    }

    /** {@inheritDoc} */
    @Override
    public long getProducttypeId() {
        return producttypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return producttypeId;
    }

    /** {@inheritDoc} */
    @Override
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }


    /** {@inheritDoc} */
    @Override
    public String getGuid() {
        return guid;
    }

    /** {@inheritDoc} */
    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getUitemplate() {
        return uitemplate;
    }

    /** {@inheritDoc} */
    @Override
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    /** {@inheritDoc} */
    @Override
    public String getUisearchtemplate() {
        return uisearchtemplate;
    }

    /** {@inheritDoc} */
    @Override
    public void setUisearchtemplate(final String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isService() {
        return service;
    }

    /** {@inheritDoc} */
    @Override
    public void setService(final boolean service) {
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnsemble() {
        return ensemble;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnsemble(final boolean ensemble) {
        this.ensemble = ensemble;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShippable() {
        return shippable;
    }

    /** {@inheritDoc} */
    @Override
    public void setShippable(final boolean shippable) {
        this.shippable = shippable;
    }

    @Override
    public String toString() {
        return "ProductTypeDTOImpl{" +
                "producttypeId=" + producttypeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uitemplate='" + uitemplate + '\'' +
                ", uisearchtemplate='" + uisearchtemplate + '\'' +
                ", service=" + service +
                ", ensemble=" + ensemble +
                ", shippable=" + shippable +
                ", downloadable=" + downloadable +
                ", digital=" + digital +
                '}';
    }
}
