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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.BrandDTOImpl;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoBrandService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImpl
        extends AbstractDtoServiceImpl<BrandDTO, BrandDTOImpl, Brand>
        implements DtoBrandService {

    private final DTOAssembler attrValueAssembler;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao;
    private final ImageService imageService;


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param brandGenericService       {@link org.yes.cart.service.domain.GenericService}
     * @param dtoAttributeService dto attribute service
     * @param attrValueEntityBrandDao attribute value service
     * @param imageService {@link ImageService} to manipulate  related images.
     */
    public DtoBrandServiceImpl(final GenericService<Brand> brandGenericService,
                               final DtoFactory dtoFactory,
                               final DtoAttributeService dtoAttributeService,
                               final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao,
                               final ImageService imageService
                               ) {
        super(dtoFactory, brandGenericService, null);
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueBrandDTO.class),
                brandGenericService.getGenericDao().getEntityFactory().getImplClass(AttrValueBrand.class)
        );
        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityBrandDao = attrValueEntityBrandDao;
        this.imageService = imageService;

    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<BrandDTO> getDtoIFace() {
        return BrandDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<BrandDTOImpl> getDtoImpl() {
        return BrandDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Brand> getEntityIFace() {
        return Brand.class;
    }

    /** {@inheritDoc}*/
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueBrandDTO> result = new ArrayList<AttrValueBrandDTO>(getById(entityPk).getAttribute());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.BRAND,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueBrandDTO attrValueCategoryDTO = getDtoFactory().getByIface(AttrValueBrandDTO.class);
            attrValueCategoryDTO.setAttributeDTO(attributeDTO);
            attrValueCategoryDTO.setBrandId(entityPk);
            result.add(attrValueCategoryDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /** {@inheritDoc}*/
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityBrand valueEntityBrand = attrValueEntityBrandDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, null, dtoFactory);
        attrValueEntityBrandDao.update(valueEntityBrand);
        return attrValueDTO;
        
    }

    /** {@inheritDoc}*/
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueBrand valueEntityBrand = getEntityFactory().getByIface(AttrValueBrand.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, null, dtoFactory);
        Attribute atr = ((GenericService<Attribute>)dtoAttributeService.getService()).getById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntityBrand.setAttribute(atr);
        valueEntityBrand.setBrand(service.getById(((AttrValueBrandDTO) attrValueDTO).getBrandId()));
        valueEntityBrand = attrValueEntityBrandDao.create((AttrValueEntityBrand) valueEntityBrand);
        attrValueDTO.setAttrvalueId(valueEntityBrand.getAttrvalueId());
        return attrValueDTO;
        

    }

    /** {@inheritDoc}*/
    public void deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityBrand valueEntityCategory = attrValueEntityBrandDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(valueEntityCategory.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(valueEntityCategory.getVal());
        }
        attrValueEntityBrandDao.delete(valueEntityCategory);
    }
}
