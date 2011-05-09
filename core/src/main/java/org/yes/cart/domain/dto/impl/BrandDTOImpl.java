package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoCollection;
import dp.lib.dto.geda.annotations.DtoField;
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

    /** {@inheritDoc}*/
    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    /** {@inheritDoc}*/
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc}*/
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    public void setDescription(String description) {
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
