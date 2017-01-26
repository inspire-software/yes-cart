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

import org.apache.commons.collections.MapUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.impl.InventoryDTOImpl;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.dto.DtoInventoryService;
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
    private final DtoInventoryService dtoInventoryService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoFulfilmentServiceImpl(final DtoWarehouseService dtoWarehouseService,
                                   final DtoInventoryService dtoInventoryService,
                                   final FederationFacade federationFacade,
                                   final VoAssemblySupport voAssemblySupport) {
        this.dtoWarehouseService = dtoWarehouseService;
        this.dtoInventoryService = dtoInventoryService;
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
    public void fillShopSummaryDetails(final VoShopSummary summary, final long shopId, final String lang) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(summary.getShopId())) {
            final Map<WarehouseDTO, Boolean> all = dtoWarehouseService.findAllByShopId(shopId);

            for (final Map.Entry<WarehouseDTO, Boolean> dto : all.entrySet()) {

                String name = dto.getKey().getName();
                if (MapUtils.isNotEmpty(dto.getKey().getDisplayNames()) && dto.getKey().getDisplayNames().get(lang) != null) {
                    name = dto.getKey().getDisplayNames().get(lang);
                }
                summary.getFulfilmentCentres().add(MutablePair.of(name, dto.getValue()));

            }

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

    @Override
    public List<VoInventory> getFilteredInventory(final long centreId, final String filter, final int max) throws Exception {

        if (federationFacade.isManageable(centreId, WarehouseDTO.class)) {

            final List<InventoryDTO> dtos = dtoInventoryService.findBy(centreId, filter, 0, max);

            return voAssemblySupport.assembleVos(VoInventory.class, InventoryDTO.class, dtos);

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoInventory getInventoryById(final long id) throws Exception {

        final InventoryDTO dto = dtoInventoryService.getInventory(id);

        if (dto != null && federationFacade.isManageable(dto.getWarehouseCode(), WarehouseDTO.class)) {

            return voAssemblySupport.assembleVo(VoInventory.class, InventoryDTO.class, new VoInventory(), dto);

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoInventory updateInventory(final VoInventory vo) throws Exception {

        final InventoryDTO dto = dtoInventoryService.getInventory(vo.getSkuWarehouseId());

        if (dto != null && federationFacade.isManageable(vo.getWarehouseCode(), WarehouseDTO.class)) {

            final InventoryDTO updated = voAssemblySupport.assembleDto(InventoryDTO.class, VoInventory.class, dto, vo);

            dtoInventoryService.updateInventory(updated);

            return getInventoryById(updated.getSkuWarehouseId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoInventory createInventory(final VoInventory vo) throws Exception {

        if (federationFacade.isManageable(vo.getWarehouseCode(), WarehouseDTO.class)) {

            InventoryDTO created = voAssemblySupport.assembleDto(InventoryDTO.class, VoInventory.class, new InventoryDTOImpl(), vo);

            created = dtoInventoryService.createInventory(created);

            return getInventoryById(created.getSkuWarehouseId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public void removeInventory(final long id) throws Exception {

        // check access
        getInventoryById(id);
        dtoInventoryService.removeInventory(id);

    }
}
