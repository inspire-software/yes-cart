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
import org.yes.cart.domain.dto.SeoImageDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class SeoImageDTOImpl implements SeoImageDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "seoImageId")
    private long seoImageId;

    @DtoField(value = "imageName")
    private String imageName;

    @DtoField(value = "alt")
    private String alt;

    @DtoField(value = "title")
    private String title;

    /** {@inheritDoc}*/
    public long getSeoImageId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    public void setSeoImageId(final long seoImageId) {
        this.seoImageId = seoImageId;
    }

    /** {@inheritDoc}*/
    public String getImageName() {
        return imageName;
    }

    /** {@inheritDoc}*/
    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    /** {@inheritDoc}*/
    public String getAlt() {
        return alt;
    }

    /** {@inheritDoc}*/
    public void setAlt(final String alt) {
        this.alt = alt;
    }

    /** {@inheritDoc}*/
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc}*/
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SeoImageDTOImpl{" +
                "seoImageId=" + seoImageId +
                ", imageName='" + imageName + '\'' +
                ", alt='" + alt + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
