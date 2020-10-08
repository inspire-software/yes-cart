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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueBrand;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class BrandDTOImpl implements BrandDTO {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "brandId", readOnly = true)
    private long brandId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;


    @DtoCollection(
            value="attributes",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueBrandDTO",
            entityGenericType = AttrValueBrand.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
            )
    private Collection<AttrValueBrandDTO> attributes;


    @DtoField(readOnly = true)
    private Instant createdTimestamp;
    @DtoField(readOnly = true)
    private Instant updatedTimestamp;
    @DtoField(readOnly = true)
    private String createdBy;
    @DtoField(readOnly = true)
    private String updatedBy;

   /** {@inheritDoc}*/
    @Override
    public long getBrandId() {
        return brandId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return brandId;
    }

    /** {@inheritDoc}*/
    @Override
    public void setBrandId(final long brandId) {
        this.brandId = brandId;
    }

    /** {@inheritDoc}*/
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc}*/
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    @Override
    public Collection<AttrValueBrandDTO> getAttributes() {
        return attributes;
    }

    /** {@inheritDoc}*/
    @Override
    public void setAttributes(final Collection<AttrValueBrandDTO> attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Instant getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

}
