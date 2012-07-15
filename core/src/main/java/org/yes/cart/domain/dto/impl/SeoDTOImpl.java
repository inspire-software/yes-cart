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
import org.yes.cart.domain.dto.SeoDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class SeoDTOImpl implements SeoDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "seoId", readOnly = true)
    private long seoId;

    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    /** {@inheritDoc}*/
    public long getId() {
        return seoId;
    }

    /** {@inheritDoc}*/
    public long getSeoId() {
        return seoId;
    }

    /** {@inheritDoc}*/
    public void setSeoId(final long seoId) {
        this.seoId = seoId;
    }

    /** {@inheritDoc}*/
    public String getUri() {
        return uri;
    }

    /** {@inheritDoc}*/
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /** {@inheritDoc}*/
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc}*/
    public void setTitle(final String title) {
        this.title = title;
    }

    /** {@inheritDoc}*/
    public String getMetakeywords() {
        return metakeywords;
    }

    /** {@inheritDoc}*/
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /** {@inheritDoc}*/
    public String getMetadescription() {
        return metadescription;
    }

    /** {@inheritDoc}*/
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }
}
