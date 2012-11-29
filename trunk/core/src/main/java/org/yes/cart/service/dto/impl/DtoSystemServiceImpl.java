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
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueSystemDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.AttrValueSystem;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoSystemService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.*;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:52 AM
 */
public class DtoSystemServiceImpl implements DtoSystemService {

    private static final long YC_ID = 100L;

    private final SystemService systemService;

    private final Assembler attrValueAssembler;
    private final DtoFactory dtoFactory;
    private final DtoAttributeService dtoAttributeService;
    private final AdaptersRepository adaptersRepository;

    public DtoSystemServiceImpl(final SystemService systemService,
                                final DtoFactory dtoFactory,
                                final DtoAttributeService dtoAttributeService,
                                final AdaptersRepository adaptersRepository) {
        this.systemService = systemService;
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueSystemDTO.class),
                systemService.getGenericDao().getEntityFactory().getImplClass(AttrValueSystem.class)
        );

        this.dtoFactory = dtoFactory;
        this.dtoAttributeService = dtoAttributeService;
        this.adaptersRepository = adaptersRepository;
    }

    private final Collection<AttrValueSystemDTO> getAttributesById() {
        final Map<String, AttrValueSystem> attrMap = systemService.getAttributeValues();
        final List<AttrValueSystemDTO> values = new ArrayList<AttrValueSystemDTO>();
        for (final AttrValueSystem attr : attrMap.values()) {
            AttrValueSystemDTO attrValueSystemDTO = dtoFactory.getByIface(AttrValueSystemDTO.class);
            this.attrValueAssembler.assembleDto(attrValueSystemDTO, attr, adaptersRepository.getAll(), dtoFactory);
            values.add(attrValueSystemDTO);
        }
        return values;
    }


    /** {@inheritDoc}*/
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<AttrValueSystemDTO> result = new ArrayList<AttrValueSystemDTO>(getAttributesById());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.SYSTEM,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueSystemDTO attrValueSystemDTO = dtoFactory.getByIface(AttrValueSystemDTO.class);
            attrValueSystemDTO.setAttributeDTO(attributeDTO);
            attrValueSystemDTO.setSystemId(YC_ID);
            result.add(attrValueSystemDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /** {@inheritDoc}*/
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        systemService.updateAttributeValue(attrValueDTO.getAttributeDTO().getCode(), attrValueDTO.getVal());
        return attrValueDTO;
    }

    /** {@inheritDoc}*/
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        Attribute atr = ((GenericService<Attribute>)dtoAttributeService.getService()).getById(attrValueDTO.getAttributeDTO().getAttributeId());
        if (atr == null) {
            return null;
        }
        systemService.updateAttributeValue(atr.getCode(), attrValueDTO.getVal());

        final AttrValueSystem val = systemService.getAttributeValues().get(atr.getCode());
        attrValueAssembler.assembleDto(attrValueDTO, val, adaptersRepository.getAll(), dtoFactory);

        return attrValueDTO;
    }

    /** {@inheritDoc}*/
    public void deleteAttributeValue(final long attributeValuePk) {
        throw new UnsupportedOperationException("System attributes cannot be deleted"); // at least for now
    }

    /**
     * Get the attribute codes. Used by business entity, that has attributes.
     * @param attrValues list of attribute values.
     * @return list of attribute codes.
     */
    protected List<String> getCodes(final List<? extends AttrValueDTO> attrValues) {
        final List<String> codes = new ArrayList<String>(attrValues.size());
        for(AttrValueDTO attrValueCategoryDTO : attrValues) {
            if (attrValueCategoryDTO != null && attrValueCategoryDTO.getAttributeDTO() != null) {
                codes.add(
                        attrValueCategoryDTO.getAttributeDTO().getCode()
                );
            }
        }
        return codes;
    }

}
