/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.dto.EtypeDTO;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttribute;
import org.yes.cart.domain.vo.VoAttributeGroup;
import org.yes.cart.domain.vo.VoEtype;
import org.yes.cart.service.dto.DtoAttributeGroupService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoEtypeService;
import org.yes.cart.service.dto.DtoProductTypeService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoAttributeService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 09/08/2016
 * Time: 18:07
 */
public class VoAttributeServiceImpl implements VoAttributeService {

    private final DtoEtypeService dtoEtypeService;
    private final DtoAttributeGroupService dtoAttributeGroupService;
    private final DtoAttributeService dtoAttributeService;
    private final DtoProductTypeService dtoProductTypeService;

    private final FederationFacade federationFacade;

    private final VoAssemblySupport voAssemblySupport;

    public VoAttributeServiceImpl(final DtoEtypeService dtoEtypeService,
                                  final DtoAttributeGroupService dtoAttributeGroupService,
                                  final DtoAttributeService dtoAttributeService,
                                  final DtoProductTypeService dtoProductTypeService,
                                  final FederationFacade federationFacade,
                                  final VoAssemblySupport voAssemblySupport) {
        this.dtoEtypeService = dtoEtypeService;
        this.dtoAttributeGroupService = dtoAttributeGroupService;
        this.dtoAttributeService = dtoAttributeService;
        this.dtoProductTypeService = dtoProductTypeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    public List<VoEtype> getAllEtypes() throws Exception {
        final List<EtypeDTO> etypeDTOs = dtoEtypeService.getAll();
        return voAssemblySupport.assembleVos(VoEtype.class, EtypeDTO.class, etypeDTOs);
    }

    /** {@inheritDoc} */
    public List<VoAttributeGroup> getAllGroups() throws Exception {
        final List<AttributeGroupDTO> groupDTOs = dtoAttributeGroupService.getAll();
        return voAssemblySupport.assembleVos(VoAttributeGroup.class, AttributeGroupDTO.class, groupDTOs);
    }

    /** {@inheritDoc} */
    public List<VoAttribute> getAllAttributes(final String group) throws Exception {
        final List<AttributeDTO> attributeDTOs = dtoAttributeService.findByAttributeGroupCode(group);
        return voAssemblySupport.assembleVos(VoAttribute.class, AttributeDTO.class, attributeDTOs);
    }

    /** {@inheritDoc} */
    public List<VoAttribute> getFilteredAttributes(final String group, final String filter, final int max) throws Exception {
        final List<AttributeDTO> attributeDTOs = dtoAttributeService.findAttributesBy(group, filter, filter, filter, 0, max);
        return voAssemblySupport.assembleVos(VoAttribute.class, AttributeDTO.class, attributeDTOs);
    }

    /** {@inheritDoc} */
    public List<MutablePair<Long, String>> getProductTypesByAttributeCode(final String code) throws Exception {

        final List<ProductTypeDTO> types = dtoProductTypeService.findByAttributeCode(code);
        final List<MutablePair<Long, String>> names = new ArrayList<>(types.size());
        for (final ProductTypeDTO type : types) {
            names.add(MutablePair.of(type.getProducttypeId(), type.getName()));
        }
        return names;
    }
}
