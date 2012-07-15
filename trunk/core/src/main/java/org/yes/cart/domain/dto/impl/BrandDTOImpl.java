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
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;

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

    @DtoField(value = "brandId")
    private long brandId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;


    @DtoCollection(
            value="attribute",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueBrandDTO",
            entityGenericType = AttrValueEntityBrand.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = AttrValueBrandMatcher.class,
            readOnly = true
            )
    private Collection<AttrValueBrandDTO> attribute;

   /** {@inheritDoc}*/
    public long getBrandId() {
        return brandId;
    }

    /**
     * {@inheritDoc}
     */
    public long getId() {
        return brandId;
    }

    /** {@inheritDoc}*/
    public void setBrandId(final long brandId) {
        this.brandId = brandId;
    }

    /** {@inheritDoc}*/
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc}*/
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    public Collection<AttrValueBrandDTO> getAttribute() {
        return attribute;
    }

    /** {@inheritDoc}*/
    public void setAttribute(final Collection<AttrValueBrandDTO> attribute) {
        this.attribute = attribute;
    }
}
