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

import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttributeGroupDTOImpl;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.dto.DtoAttributeGroupService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeGroupServiceImpl
        extends AbstractDtoServiceImpl<AttributeGroupDTO, AttributeGroupDTOImpl, AttributeGroup>
        implements DtoAttributeGroupService {


    /**
     * Construct service
     * @param attributeGroupService {@link AttributeGroupService}
     * @param dtoFactory {@link DtoFactory}
     */
    public DtoAttributeGroupServiceImpl(
            final AttributeGroupService attributeGroupService,
            final DtoFactory dtoFactory) {
        super(dtoFactory, attributeGroupService, null);
    }


    /**
     * Get single attribute by given code.
     * @param code given code
     * @return {@link AttributeGroup} if found, otherwise null.
     * @throws UnableToCreateInstanceException in case of reflection problem
     * @throws UnmappedInterfaceException in case of configuration problem
     */
    public AttributeGroupDTO getAttributeGroupByCode(final String code)  throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final AttributeGroup attributeGroup =  getAttributeGroupService().getAttributeGroupByCode(code);
        if (attributeGroup != null) {
            final AttributeGroupDTO attributeGroupDTO = dtoFactory.getByIface(AttributeGroupDTO.class);
            assembler.assembleDto(attributeGroupDTO, attributeGroup, null, dtoFactory);
            return attributeGroupDTO;
        }
        return null;
    }

    /**
     * Persist {@link AttributeGroup}
     * @param code code
     * @param name name
     * @param description description
     * @return created dto
     * @throws UnableToCreateInstanceException in case of reflection problem
     * @throws UnmappedInterfaceException in case of configuration problem
     */
    public AttributeGroupDTO create(final String code, final String name, final String description) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final AttributeGroup attributeGroup = getEntityFactory().getByIface(AttributeGroup.class);
        attributeGroup.setCode(code);
        attributeGroup.setName(name);
        attributeGroup.setDescription(description);
        service.create(attributeGroup);
        return getAttributeGroupByCode(code);

    }

    /**
     * Update  {@link AttributeGroup} entity.
     * @param code code
     * @param name name
     * @param description description
     * @return updated entity
     */
    public AttributeGroupDTO update(final String code, final String name, final String description) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final AttributeGroup attributeGroup = getAttributeGroupService().getAttributeGroupByCode(code);
        attributeGroup.setCode(code);
        attributeGroup.setName(name);
        attributeGroup.setDescription(description);
        service.update(attributeGroup);
        return getAttributeGroupByCode(code);
    }

    /**
     * Delete  {@link AttributeGroup} by given code.
     * @param code code of {@link AttributeGroup} to delete
     */
    public void remove(final String code) {
        getAttributeGroupService().delete(code);
    }

    /** {@inheritDoc}*/
    public AttributeGroupDTO create(final AttributeGroupDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return create(instance.getCode(), instance.getName(), instance.getDescription());
    }

    /** {@inheritDoc}*/
    public AttributeGroupDTO update(final AttributeGroupDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return update(instance.getCode(), instance.getName(), instance.getDescription());
    }

    /** {@inheritDoc}*/
    public Class<AttributeGroupDTO> getDtoIFace() {
        return AttributeGroupDTO.class;
    }

    /** {@inheritDoc}*/
    public Class<AttributeGroupDTOImpl> getDtoImpl() {
        return AttributeGroupDTOImpl.class;
    }

    /** {@inheritDoc}*/
    public Class<AttributeGroup> getEntityIFace() {
        return AttributeGroup.class;
    }

    private AttributeGroupService getAttributeGroupService() {
        return (AttributeGroupService) service;
    }


}
