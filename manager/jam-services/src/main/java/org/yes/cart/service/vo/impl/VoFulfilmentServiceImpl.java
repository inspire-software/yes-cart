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

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.vo.VoFulfilmentCentre;
import org.yes.cart.domain.vo.VoFulfilmentCentreInfo;
import org.yes.cart.domain.vo.VoFulfilmentCentreShopLink;
import org.yes.cart.domain.vo.VoShopFulfilmentCentre;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 10:42
 */
public class VoFulfilmentServiceImpl implements VoFulfilmentService {

    private final DtoWarehouseService dtoWarehouseService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoFulfilmentServiceImpl(final DtoWarehouseService dtoWarehouseService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.dtoWarehouseService = dtoWarehouseService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public List<VoFulfilmentCentre> getAllFulfilmentCentres() throws Exception {
        final Map<WarehouseDTO, Map<ShopDTO, Boolean>> all = dtoWarehouseService.getAllWithShops();
        federationFacade.applyFederationFilter(all.keySet(), WarehouseDTO.class);
        final VoAssemblySupport.VoAssembler<VoFulfilmentCentre, WarehouseDTO> asmC =
                voAssemblySupport.with(VoFulfilmentCentre.class, WarehouseDTO.class);
        final List<VoFulfilmentCentre> vos = new ArrayList<>(all.size());
        for (final Map.Entry<WarehouseDTO, Map<ShopDTO, Boolean>> dto : all.entrySet()) {
            final VoFulfilmentCentre vo = asmC.assembleVo(new VoFulfilmentCentre(), dto.getKey());
            for (final Map.Entry<ShopDTO, Boolean> assign : dto.getValue().entrySet()) {
                final VoFulfilmentCentreShopLink link = new VoFulfilmentCentreShopLink();
                link.setWarehouseId(vo.getWarehouseId());
                link.setShopId(assign.getKey().getShopId());
                link.setDisabled(assign.getValue());
                vo.getFulfilmentShops().add(link);
            }
            vos.add(vo);
        }
        return vos;

    }

    @Override
    public List<VoShopFulfilmentCentre> getShopFulfilmentCentres(final long shopId) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final Map<WarehouseDTO, Boolean> all = dtoWarehouseService.findAllByShopId(shopId);

            final List<VoShopFulfilmentCentre> vos = new ArrayList<>(all.size());
            final VoAssemblySupport.VoAssembler<VoShopFulfilmentCentre, WarehouseDTO> asm =
                    voAssemblySupport.with(VoShopFulfilmentCentre.class, WarehouseDTO.class);
            for (final Map.Entry<WarehouseDTO, Boolean> dto : all.entrySet()) {
                final VoShopFulfilmentCentre vo = asm.assembleVo(new VoShopFulfilmentCentre(), dto.getKey());
                final VoFulfilmentCentreShopLink link = new VoFulfilmentCentreShopLink();
                link.setShopId(shopId);
                link.setWarehouseId(vo.getWarehouseId());
                link.setDisabled(dto.getValue());
                vo.setFulfilmentShop(link);
                vos.add(vo);
            }
            return vos;

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoFulfilmentCentre getFulfilmentCentreById(final long id) throws Exception {
        if (federationFacade.isManageable(id, WarehouseDTO.class)) {

            final WarehouseDTO dto = dtoWarehouseService.getById(id);
            final VoFulfilmentCentre vo = voAssemblySupport.assembleVo(VoFulfilmentCentre.class, WarehouseDTO.class, new VoFulfilmentCentre(), dto);
            final Map<ShopDTO, Boolean> links = dtoWarehouseService.getAssignedWarehouseShops(id);
            for (final Map.Entry<ShopDTO, Boolean> assign : links.entrySet()) {
                final VoFulfilmentCentreShopLink link = new VoFulfilmentCentreShopLink();
                link.setWarehouseId(vo.getWarehouseId());
                link.setShopId(assign.getKey().getShopId());
                link.setDisabled(assign.getValue());
                vo.getFulfilmentShops().add(link);
            }
            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoFulfilmentCentre createFulfilmentCentre(final VoFulfilmentCentreInfo vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {

            WarehouseDTO dto = dtoWarehouseService.getNew();
            dto = dtoWarehouseService.create(
                    voAssemblySupport.assembleDto(WarehouseDTO.class, VoFulfilmentCentreInfo.class, dto, vo)
            );
            return getFulfilmentCentreById(dto.getWarehouseId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoFulfilmentCentre createShopFulfilmentCentre(final VoFulfilmentCentreInfo vo, final long shopId) throws Exception {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {

            WarehouseDTO dto = dtoWarehouseService.getNew();
            dto = dtoWarehouseService.create(
                    voAssemblySupport.assembleDto(WarehouseDTO.class, VoFulfilmentCentreInfo.class, dto, vo)
            );
            dtoWarehouseService.assignWarehouse(dto.getWarehouseId(), shopId, false);
            return getFulfilmentCentreById(dto.getWarehouseId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoFulfilmentCentre updateFulfilmentCentre(final VoFulfilmentCentre vo) throws Exception {
        if (vo != null && federationFacade.isManageable(vo.getWarehouseId(), WarehouseDTO.class)) {

            final VoFulfilmentCentre existing = getFulfilmentCentreById(vo.getWarehouseId());

            WarehouseDTO dto = dtoWarehouseService.getById(vo.getWarehouseId());
            dto = dtoWarehouseService.update(
                    voAssemblySupport.assembleDto(WarehouseDTO.class, VoFulfilmentCentreInfo.class, dto, vo)
            );

            final Map<ShopDTO, Boolean> existingLinks = dtoWarehouseService.getAssignedWarehouseShops(vo.getWarehouseId());
            for (final ShopDTO link : existingLinks.keySet()) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    dtoWarehouseService.unassignWarehouse(vo.getWarehouseId(), link.getShopId(), false);
                } // else skip updates for inaccessible shops
            }

            for (final VoFulfilmentCentreShopLink link : vo.getFulfilmentShops()) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    dtoWarehouseService.assignWarehouse(vo.getWarehouseId(), link.getShopId(), link.isDisabled());
                } // else skip updates for inaccessible shops
            }

            return getFulfilmentCentreById(dto.getWarehouseId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(final List<VoShopFulfilmentCentre> vo) throws Exception {
        if (vo != null) {

            for (final VoShopFulfilmentCentre shopCenter : vo) {
                final VoFulfilmentCentreShopLink link = shopCenter.getFulfilmentShop();
                if (link != null && federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    if (link.isDisabled()) {
                        dtoWarehouseService.unassignWarehouse(link.getWarehouseId(), link.getShopId(), true);
                    } else {
                        dtoWarehouseService.assignWarehouse(link.getWarehouseId(), link.getShopId(), false);
                    }
                } // else skip updates for inaccessible shops
            }

            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public void removeFulfilmentCentre(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {

            dtoWarehouseService.remove(id);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
