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

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttributeDTOImpl;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Remote attribute service.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeServiceImpl
        extends AbstractDtoServiceImpl<AttributeDTO, AttributeDTOImpl, Attribute>
        implements DtoAttributeService {

    private final EtypeService etypeService;

    private final AttributeGroupService attributeGroupService;


    /**
     * Construct remote service.
     * @param attributeService {@link AttributeService}
     * @param dtoFactory {@link DtoFactory}
     * @param etypeService {@link org.yes.cart.service.domain.EtypeService}
     * @param attributeGroupService {@link AttributeGroupService}
     */
    public DtoAttributeServiceImpl(
            final AttributeService attributeService,
            final EtypeService etypeService,
            final AttributeGroupService attributeGroupService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adapters) {
        super(dtoFactory, attributeService, adapters);
        this.etypeService = etypeService;
        this.attributeGroupService = attributeGroupService;

    }

    /** {@inheritDoc}  */
    public AttributeDTO create(final AttributeDTO dto) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Attribute attribute = getEntityFactory().getByIface(Attribute.class);
        assembler.assembleEntity(dto, attribute,  getAdaptersRepository(), dtoFactory);
        attribute.setEtype(etypeService.findById(dto.getEtypeId()));
        attribute.setAttributeGroup(attributeGroupService.findById(dto.getAttributegroupId()));
        attribute = service.create(attribute);
        return getById(attribute.getAttributeId());
    }

    /** {@inheritDoc}  */
    public AttributeDTO update(final AttributeDTO dto) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Attribute attribute = service.findById(dto.getAttributeId());
        assembler.assembleEntity(dto, attribute,  getAdaptersRepository(), dtoFactory);
        attribute.setEtype(etypeService.findById(dto.getEtypeId()));
        attribute.setAttributeGroup(attributeGroupService.findById(dto.getAttributegroupId()));
        attribute = service.update(attribute);
        return getById(attribute.getAttributeId());
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findByAttributeGroupCode(final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes =  ((AttributeService)service).findByAttributeGroupCode(attributeGroupCode);
        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAvailableAttributes(
            final String attributeGroupCode,
            final List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Attribute> attributes =  ((AttributeService)service).findAvailableAttributes(
                attributeGroupCode, assignedAttributeCodes);

        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAvailableAttributesByProductTypeId(
            final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Attribute> attributes = ((AttributeService)service).getAvailableAttributesByProductTypeId(productTypeId);
        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAvailableImageAttributesByGroupCode(
            final String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes = ((AttributeService)service).getAvailableImageAttributesByGroupCode(attributeGroupCode);
        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAvailableAttributesByGroupCodeStartsWith(
            final String attributeGroupCode,
            final String codePrefix)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attributes = ((AttributeService)service).getAvailableAttributesByGroupCodeStartsWith(attributeGroupCode, codePrefix);
        final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attributes.size());
        fillDTOs(attributes, attributesDTO);
        return attributesDTO;
    }

    /** {@inheritDoc}  */
    public List<AttributeDTO> findAttributesWithMultipleValues(
            final String attributeGroupCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Attribute> attrs = ((AttributeService)service).findAttributesWithMultipleValues(attributeGroupCode);
        if (attrs != null) {
            final List<AttributeDTO> attributesDTO = new ArrayList<AttributeDTO>(attrs.size());
            fillDTOs(attrs, attributesDTO);
            return attributesDTO;

        }
        return null;
    }


    /** {@inheritDoc}  */
    public Class<AttributeDTO> getDtoIFace() {
        return AttributeDTO.class;
    }

    /** {@inheritDoc}  */
    public Class<AttributeDTOImpl> getDtoImpl() {
        return AttributeDTOImpl.class;
    }

    /** {@inheritDoc}  */
    public Class<Attribute> getEntityIFace() {
        return Attribute.class;
    }


}
