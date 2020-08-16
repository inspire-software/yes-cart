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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProdTypeAttributeViewGroupService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoProductTypeService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 22/08/2016
 * Time: 12:48
 */
public class VoProductTypeServiceImpl implements VoProductTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(VoProductTypeServiceImpl.class);

    private final DtoProductTypeService dtoProductTypeService;
    private final DtoProdTypeAttributeViewGroupService dtoProdTypeAttributeViewGroupService;
    private final DtoProductTypeAttrService dtoProductTypeAttrService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoProductTypeServiceImpl(final DtoProductTypeService dtoProductTypeService,
                                    final DtoProdTypeAttributeViewGroupService dtoProdTypeAttributeViewGroupService,
                                    final DtoProductTypeAttrService dtoProductTypeAttrService,
                                    final DtoAttributeService dtoAttributeService,
                                    final FederationFacade federationFacade,
                                    final VoAssemblySupport voAssemblySupport) {
        this.dtoProductTypeService = dtoProductTypeService;
        this.dtoProdTypeAttributeViewGroupService = dtoProdTypeAttributeViewGroupService;
        this.dtoProductTypeAttrService = dtoProductTypeAttrService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoSearchResult<VoProductTypeInfo> getFilteredTypes(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoProductTypeInfo> result = new VoSearchResult<>();
        final List<VoProductTypeInfo> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter"
        );


        final SearchResult<ProductTypeDTO> batch = dtoProductTypeService.findProductTypes(searchContext);
        if (!batch.getItems().isEmpty()) {
            results.addAll(voAssemblySupport.assembleVos(VoProductTypeInfo.class, ProductTypeDTO.class, batch.getItems()));
        }

        result.setTotal(batch.getTotal());

        return result;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoProductType getTypeById(final long id) throws Exception {
        final ProductTypeDTO typeDTO = dtoProductTypeService.getById(id);
        if (typeDTO != null /* && federationFacade.isCurrentUserSystemAdmin() */) {
            final VoProductType type = voAssemblySupport.assembleVo(VoProductType.class, ProductTypeDTO.class, new VoProductType(), typeDTO);
            final List<ProdTypeAttributeViewGroupDTO> groups = dtoProdTypeAttributeViewGroupService.getByProductTypeId(id);
            final List<VoProductTypeViewGroup> voGroups = voAssemblySupport.assembleVos(VoProductTypeViewGroup.class, ProdTypeAttributeViewGroupDTO.class, groups);
            type.setViewGroups(voGroups);
            return type;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoProductType updateType(final VoProductType vo) throws Exception {
        final ProductTypeDTO typeDTO = dtoProductTypeService.getById(vo.getProducttypeId());
        if (typeDTO != null && federationFacade.isCurrentUserSystemAdmin()) {
            dtoProductTypeService.update(
                    voAssemblySupport.assembleDto(ProductTypeDTO.class, VoProductType.class, typeDTO, vo)
            );

            final List<ProdTypeAttributeViewGroupDTO> groups = dtoProdTypeAttributeViewGroupService.getByProductTypeId(vo.getProducttypeId());
            final Map<Long, ProdTypeAttributeViewGroupDTO> existing = new HashMap<>();
            for (final ProdTypeAttributeViewGroupDTO group : groups) {
                existing.put(group.getProdTypeAttributeViewGroupId(), group);
            }

            if (vo.getViewGroups() != null) {
                for (final VoProductTypeViewGroup voGroup : vo.getViewGroups()) {
                    final ProdTypeAttributeViewGroupDTO dtoToUpdate = existing.get(voGroup.getProdTypeAttributeViewGroupId());
                    voGroup.setProducttypeId(vo.getProducttypeId()); // ensure we do not change the product type
                    if (dtoToUpdate != null) {
                        // update mode
                        existing.remove(dtoToUpdate.getProdTypeAttributeViewGroupId());

                        dtoProdTypeAttributeViewGroupService.update(
                            voAssemblySupport.assembleDto(ProdTypeAttributeViewGroupDTO.class, VoProductTypeViewGroup.class, dtoToUpdate, voGroup)
                        );

                    } else {
                        // insert mode

                        final ProdTypeAttributeViewGroupDTO newGroup = dtoProdTypeAttributeViewGroupService.getNew();

                        dtoProdTypeAttributeViewGroupService.create(
                                voAssemblySupport.assembleDto(ProdTypeAttributeViewGroupDTO.class, VoProductTypeViewGroup.class, newGroup, voGroup)
                        );
                    }
                }
            }

            for (final ProdTypeAttributeViewGroupDTO remove : existing.values()) {
                dtoProdTypeAttributeViewGroupService.remove(remove.getProdTypeAttributeViewGroupId());
            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getTypeById(vo.getProducttypeId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoProductType createType(final VoProductTypeInfo vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            ProductTypeDTO typeDTO = dtoProductTypeService.getNew();
            typeDTO = dtoProductTypeService.create(
                    voAssemblySupport.assembleDto(ProductTypeDTO.class, VoProductTypeInfo.class, typeDTO, vo)
            );
            return getTypeById(typeDTO.getProducttypeId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoProductType copyType(final long id, final VoProductTypeInfo vo) throws Exception {

        if (federationFacade.isCurrentUserSystemAdmin()) {
            final VoProductType existing = getTypeById(id);

            if (existing != null) {

                final VoProductTypeInfo main = vo != null ? vo : existing;
                ProductTypeDTO typeDTO = voAssemblySupport.assembleDto(ProductTypeDTO.class, VoProductTypeInfo.class, dtoProductTypeService.getNew(), main);
                if (vo == null || vo.getGuid() == null) {
                    typeDTO.setGuid(UUID.randomUUID().toString());
                }
                typeDTO = dtoProductTypeService.create(typeDTO);

                final VoAssemblySupport.VoAssembler<VoProductTypeAttr, ProductTypeAttrDTO> asm =
                        voAssemblySupport.with(VoProductTypeAttr.class, ProductTypeAttrDTO.class);

                for (final VoProductTypeAttr attrs : getTypeAttributes(existing.getProducttypeId())) {
                    // insert mode
                    final ProductTypeAttrDTO dto = dtoProductTypeAttrService.getNew();
                    asm.assembleDto(dto, attrs);
                    dto.setProducttypeId(typeDTO.getProducttypeId());
                    dto.setAttributeDTO(dtoAttributeService.getById(attrs.getAttribute().getAttributeId()));
                    this.dtoProductTypeAttrService.create(dto);
                }

                for (final VoProductTypeViewGroup group : existing.getViewGroups()) {
                    // insert mode
                    group.setProducttypeId(typeDTO.getProducttypeId());
                    final ProdTypeAttributeViewGroupDTO newGroup = dtoProdTypeAttributeViewGroupService.getNew();
                    dtoProdTypeAttributeViewGroupService.create(
                            voAssemblySupport.assembleDto(ProdTypeAttributeViewGroupDTO.class, VoProductTypeViewGroup.class, newGroup, group)
                    );
                }

                return getTypeById(typeDTO.getProducttypeId());

            }
        }
        throw new AccessDeniedException("Access is denied");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeType(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            final List<ProdTypeAttributeViewGroupDTO> groups = dtoProdTypeAttributeViewGroupService.getByProductTypeId(id);
            if (CollectionUtils.isNotEmpty(groups)) {
                for (final ProdTypeAttributeViewGroupDTO group : groups) {
                    dtoProdTypeAttributeViewGroupService.remove(group.getProdTypeAttributeViewGroupId());
                }
            }
            dtoProductTypeService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoProductTypeAttr> getTypeAttributes(final long typeId) throws Exception {

        final List<ProductTypeAttrDTO> attributes = dtoProductTypeAttrService.getByProductTypeId(typeId);

        return voAssemblySupport.assembleVos(VoProductTypeAttr.class, ProductTypeAttrDTO.class, attributes);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoProductTypeAttr> updateTypeAttributes(final List<MutablePair<VoProductTypeAttr, Boolean>> vo) throws Exception {

        long typeId = 0L;
        if (federationFacade.isCurrentUserSystemAdmin()) {

            final VoAssemblySupport.VoAssembler<VoProductTypeAttr, ProductTypeAttrDTO> asm =
                    voAssemblySupport.with(VoProductTypeAttr.class, ProductTypeAttrDTO.class);


            Map<Long, ProductTypeAttrDTO> existing = Collections.emptyMap();
            for (final MutablePair<VoProductTypeAttr, Boolean> item : vo) {
                if (typeId == 0L) {
                    typeId = item.getFirst().getProducttypeId();
                    existing = mapAvById((List) dtoProductTypeAttrService.getByProductTypeId(typeId));
                } else if (typeId != item.getFirst().getProducttypeId()) {
                    throw new AccessDeniedException("Access is denied");
                }

                if (Boolean.valueOf(item.getSecond())) {
                    if (item.getFirst().getProductTypeAttrId() > 0L) {
                        // delete mode
                        dtoProductTypeAttrService.remove(item.getFirst().getProductTypeAttrId());
                    }
                } else if (item.getFirst().getProductTypeAttrId() > 0L) {
                    // update mode
                    final ProductTypeAttrDTO dto = existing.get(item.getFirst().getProductTypeAttrId());
                    if (dto != null) {
                        asm.assembleDto(dto, item.getFirst());
                        dtoProductTypeAttrService.update(dto);
                    } else {
                        LOG.warn("Update skipped for inexistent ID {}", item.getFirst().getProductTypeAttrId());
                    }
                } else {
                    // insert mode
                    final ProductTypeAttrDTO dto = dtoProductTypeAttrService.getNew();
                    dto.setProducttypeId(typeId);
                    dto.setAttributeDTO(dtoAttributeService.getById(item.getFirst().getAttribute().getAttributeId()));
                    asm.assembleDto(dto, item.getFirst());
                    this.dtoProductTypeAttrService.create(dto);
                }

            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }

        return getTypeAttributes(typeId);
    }

    private Map<Long, ProductTypeAttrDTO> mapAvById(final List<ProductTypeAttrDTO> entityAttributes) {
        Map<Long, ProductTypeAttrDTO> map = new HashMap<>();
        for (final ProductTypeAttrDTO dto : entityAttributes) {
            map.put(dto.getProductTypeAttrId(), dto);
        }
        return map;
    }
}
