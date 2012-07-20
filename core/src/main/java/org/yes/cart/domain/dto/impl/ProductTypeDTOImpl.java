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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.ProductTypeDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ProductTypeDTOImpl implements ProductTypeDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "producttypeId")
    private long producttypeId;

    @DtoField(value = "name")
    private String name;

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

    @DtoField(value = "shipable")
    private boolean shipable;

    @DtoField(value = "downloadable")
    private boolean downloadable;

    @DtoField(value = "digital")
    private boolean digital;

    /** {@inheritDoc} */
    public boolean isDownloadable() {
        return downloadable;
    }

    /** {@inheritDoc} */
    public void setDownloadable(final boolean downloadable) {
        this.downloadable = downloadable;
    }

    /** {@inheritDoc} */
    public boolean isDigital() {
        return digital;
    }

    /** {@inheritDoc} */
    public void setDigital(final boolean digital) {
        this.digital = digital;
    }

    /** {@inheritDoc} */
    public long getProducttypeId() {
        return producttypeId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return producttypeId;
    }

    /** {@inheritDoc} */
    public void setProducttypeId(final long producttypeId) {
        this.producttypeId = producttypeId;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    public String getUitemplate() {
        return uitemplate;
    }

    /** {@inheritDoc} */
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    /** {@inheritDoc} */
    public String getUisearchtemplate() {
        return uisearchtemplate;
    }

    /** {@inheritDoc} */
    public void setUisearchtemplate(final String uisearchtemplate) {
        this.uisearchtemplate = uisearchtemplate;
    }

    /** {@inheritDoc} */
    public boolean isService() {
        return service;
    }

    /** {@inheritDoc} */
    public void setService(final boolean service) {
        this.service = service;
    }

    /** {@inheritDoc} */
    public boolean isEnsemble() {
        return ensemble;
    }

    /** {@inheritDoc} */
    public void setEnsemble(final boolean ensemble) {
        this.ensemble = ensemble;
    }

    /** {@inheritDoc} */
    public boolean isShipable() {
        return shipable;
    }

    /** {@inheritDoc} */
    public void setShipable(final boolean shipable) {
        this.shipable = shipable;
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
                ", shipable=" + shipable +
                ", downloadable=" + downloadable +
                ", digital=" + digital +
                '}';
    }
}
