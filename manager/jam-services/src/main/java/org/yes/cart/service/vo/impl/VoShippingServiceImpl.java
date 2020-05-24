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
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.domain.dto.CarrierSlaDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.dto.DtoCarrierService;
import org.yes.cart.service.dto.DtoCarrierSlaService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoShippingService;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 21:48
 */
public class VoShippingServiceImpl implements VoShippingService {

    private final DtoShopService dtoShopService;
    private final DtoCarrierService dtoCarrierService;
    private final DtoCarrierSlaService dtoCarrierSlaService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoShippingServiceImpl(final DtoShopService dtoShopService,
                                 final DtoCarrierService dtoCarrierService,
                                 final DtoCarrierSlaService dtoCarrierSlaService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport) {
        this.dtoShopService = dtoShopService;
        this.dtoCarrierService = dtoCarrierService;
        this.dtoCarrierSlaService = dtoCarrierSlaService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public VoSearchResult<VoCarrierInfo> getFilteredCarriers(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoCarrierInfo> result = new VoSearchResult<>();
        final List<VoCarrierInfo> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final Map<String, List> all = filter.getParameters() != null ? new HashMap<>(filter.getParameters()) : new HashMap<>();
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            final Set<Long> shopIds = federationFacade.getAccessibleShopIdsByCurrentManager();
            if (CollectionUtils.isEmpty(shopIds)) {
                return result;
            }
            all.put("shopIds", new ArrayList(shopIds));
        }

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "shopIds"
        );

        final SearchResult<CarrierDTO> batch = dtoCarrierService.findCarriers(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoCarrierInfo.class, CarrierDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;
    }

    @Override
    public List<VoShopCarrier> getShopCarriers(final long shopId) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final Map<CarrierDTO, Boolean> all = dtoCarrierService.findAllByShopId(shopId);

            final List<VoShopCarrier> vos = new ArrayList<>(all.size());
            final VoAssemblySupport.VoAssembler<VoShopCarrier, CarrierDTO> asm =
                    voAssemblySupport.with(VoShopCarrier.class, CarrierDTO.class);
            for (final Map.Entry<CarrierDTO, Boolean> dto : all.entrySet()) {
                final VoShopCarrier vo = asm.assembleVo(new VoShopCarrier(), dto.getKey());

                final VoCarrierShopLink link = new VoCarrierShopLink();
                link.setShopId(shopId);
                link.setCarrierId(vo.getCarrierId());
                link.setDisabled(dto.getValue());
                vo.setCarrierShop(link);

                vos.add(vo);
            }
            return vos;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public List<VoShopCarrierAndSla> getShopCarriersAndSla(final long shopId) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final Map<CarrierDTO, Boolean> all = dtoCarrierService.findAllByShopId(shopId);

            final Set<Long> disabled = getDisabledCarrierSlaConfig(shopId);
            final Map<Long, Integer> ranks = getCarrierSlaRanksConfig(shopId);

            final List<VoShopCarrierAndSla> vos = new ArrayList<>(all.size());
            final VoAssemblySupport.VoAssembler<VoShopCarrierAndSla, CarrierDTO> asmC =
                    voAssemblySupport.with(VoShopCarrierAndSla.class, CarrierDTO.class);
            final VoAssemblySupport.VoAssembler<VoShopCarrierSla, CarrierSlaDTO> asmS =
                    voAssemblySupport.with(VoShopCarrierSla.class, CarrierSlaDTO.class);
            for (final Map.Entry<CarrierDTO, Boolean> dto : all.entrySet()) {
                final VoShopCarrierAndSla vo = asmC.assembleVo(new VoShopCarrierAndSla(), dto.getKey());

                final VoCarrierShopLink link = new VoCarrierShopLink();
                link.setShopId(shopId);
                link.setCarrierId(vo.getCarrierId());
                link.setDisabled(dto.getValue());
                vo.setCarrierShop(link);

                for (final CarrierSlaDTO sla : dtoCarrierSlaService.findByCarrier(dto.getKey().getCarrierId())) {
                    final VoShopCarrierSla slaVo = asmS.assembleVo(new VoShopCarrierSla(), sla);
                    slaVo.setDisabled(disabled.contains(slaVo.getCarrierslaId()));
                    final Integer rank = ranks.get(slaVo.getCarrierslaId());
                    slaVo.setRank(rank != null ? rank : 0);
                    vo.getCarrierSlas().add(slaVo);
                }

                vos.add(vo);
            }
            return vos;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private Map<Long, Integer> getCarrierSlaRanksConfig(final long shopId) {
        final String pkAndRanks = dtoShopService.getSupportedCarrierSlaRanks(shopId);
        final Map<Long, Integer> pks = new HashMap<>();
        if (StringUtils.isNotBlank(pkAndRanks)) {
            try {
                final Properties props = new Properties();
                props.load(new StringReader(pkAndRanks));
                for (final String key : props.stringPropertyNames()) {
                    pks.put(NumberUtils.toLong(key), NumberUtils.toInt(props.getProperty(key)));
                }
            } catch (Exception exp) {
                // do nothing
            }
        }
        return pks;
    }

    private Set<Long> getDisabledCarrierSlaConfig(final long shopId) {
        final String disabled = dtoShopService.getDisabledCarrierSla(shopId);
        final Set<Long> pks = new HashSet<>();
        if (StringUtils.isNotBlank(disabled)) {
            for (final String pk : StringUtils.split(disabled, ',')) {
                final long id = NumberUtils.toLong(pk.trim());
                if (id > 0) {
                    pks.add(id);
                }
            }
        }
        return pks;
    }

    @Override
    public void fillShopSummaryDetails(final VoShopSummary summary, final long shopId, final String lang) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(summary.getShopId())) {
            final Map<CarrierDTO, Boolean> all = dtoCarrierService.findAllByShopId(shopId);

            for (final Map.Entry<CarrierDTO, Boolean> dto : all.entrySet()) {

                String name = dto.getKey().getName();
                if (MapUtils.isNotEmpty(dto.getKey().getDisplayNames()) && dto.getKey().getDisplayNames().get(lang) != null) {
                    name = dto.getKey().getDisplayNames().get(lang);
                }
                summary.getCarriers().add(MutablePair.of(name, dto.getValue()));

            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public VoCarrier getCarrierById(final long id) throws Exception {
        if (federationFacade.isManageable(id, CarrierDTO.class)) {

            final CarrierDTO dto = dtoCarrierService.getById(id);
            final VoCarrier vo = voAssemblySupport.assembleVo(VoCarrier.class, CarrierDTO.class, new VoCarrier(), dto);
            final List<CarrierSlaDTO> slas = dtoCarrierSlaService.findByCarrier(id);
            vo.setSlas(voAssemblySupport.assembleVos(VoCarrierSlaInfo.class, CarrierSlaDTO.class, slas));
            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoCarrier createCarrier(final VoCarrier vo) throws Exception {

        final List<VoCarrierShopLink> shops = vo.getCarrierShops();
        boolean sysAdminOnly = true;
        if (CollectionUtils.isNotEmpty(shops)) {
            for (final VoCarrierShopLink link : shops) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    sysAdminOnly = false;
                } // else skip updates for inaccessible shops
            }
        }

        if (!sysAdminOnly || federationFacade.isCurrentUserSystemAdmin()) {

            CarrierDTO dto = dtoCarrierService.getNew();
            dto = dtoCarrierService.create(
                    voAssemblySupport.assembleDto(CarrierDTO.class, VoCarrierInfo.class, dto, vo)
            );

            if (CollectionUtils.isNotEmpty(shops)) {
                for (final VoCarrierShopLink link : shops) {
                    if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                        dtoCarrierService.assignToShop(dto.getCarrierId(), link.getShopId(), link.isDisabled());
                    } // else skip updates for inaccessible shops
                }
            }

            return getCarrierById(dto.getCarrierId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public VoCarrier createShopCarrier(final VoCarrierInfo vo, final long shopId) throws Exception {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {

            CarrierDTO dto = dtoCarrierService.getNew();
            dto = dtoCarrierService.create(
                    voAssemblySupport.assembleDto(CarrierDTO.class, VoCarrierInfo.class, dto, vo)
            );
            dtoCarrierService.assignToShop(dto.getCarrierId(), shopId, false);
            return getCarrierById(dto.getCarrierId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public VoCarrier updateCarrier(final VoCarrier vo) throws Exception {
        if (vo != null && federationFacade.isManageable(vo.getCarrierId(), CarrierDTO.class)) {

            final VoCarrier existing = getCarrierById(vo.getCarrierId());

            CarrierDTO dto = dtoCarrierService.getById(vo.getCarrierId());
            dto = dtoCarrierService.update(
                    voAssemblySupport.assembleDto(CarrierDTO.class, VoCarrierInfo.class, dto, vo)
            );

            final List<VoCarrierShopLink> existingLinks = existing.getCarrierShops();
            for (final VoCarrierShopLink link : existingLinks) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    dtoCarrierService.unassignFromShop(vo.getCarrierId(), link.getShopId(), false);
                } // else skip updates for inaccessible shops
            }

            for (final VoCarrierShopLink link : vo.getCarrierShops()) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    dtoCarrierService.assignToShop(vo.getCarrierId(), link.getShopId(), link.isDisabled());
                } // else skip updates for inaccessible shops
            }

            return getCarrierById(dto.getCarrierId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public List<VoShopCarrier> updateShopCarriers(final List<VoShopCarrier> vo) throws Exception {
        if (vo != null) {

            for (final VoShopCarrier shopCarrier : vo) {
                final VoCarrierShopLink link = shopCarrier.getCarrierShop();
                if (link != null && federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    if (link.isDisabled()) {
                        dtoCarrierService.unassignFromShop(link.getCarrierId(), link.getShopId(), true);
                    } else {
                        dtoCarrierService.assignToShop(link.getCarrierId(), link.getShopId(), false);
                    }
                } // else skip updates for inaccessible shops
            }

            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public List<VoShopCarrierAndSla> updateShopCarriersAndSla(final List<VoShopCarrierAndSla> vo) throws Exception {
        if (vo != null) {

            for (final VoShopCarrierAndSla shopCarrier : vo) {
                final VoCarrierShopLink link = shopCarrier.getCarrierShop();
                if (link != null && federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    if (link.isDisabled()) {
                        dtoCarrierService.unassignFromShop(link.getCarrierId(), link.getShopId(), true);
                    } else {
                        dtoCarrierService.assignToShop(link.getCarrierId(), link.getShopId(), false);
                    }

                    final List<VoShopCarrierSla> slas = shopCarrier.getCarrierSlas();
                    if (CollectionUtils.isNotEmpty(slas)) {

                        final Set<Long> disabled = getDisabledCarrierSlaConfig(link.getShopId());
                        final Map<Long, Integer> ranks = getCarrierSlaRanksConfig(link.getShopId());

                        for (final VoShopCarrierSla sla : slas) {
                            if (sla.isDisabled()) {
                                disabled.add(sla.getCarrierslaId());
                            } else {
                                disabled.remove(sla.getCarrierslaId());
                            }
                            ranks.put(sla.getCarrierslaId(), sla.getRank());
                        }

                        dtoShopService.updateDisabledCarrierSla(link.getShopId(), StringUtils.join(disabled, ','));
                        final StringBuilder props = new StringBuilder();
                        for (final Map.Entry<Long, Integer> entry : ranks.entrySet()) {
                            props.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
                        }
                        dtoShopService.updateSupportedCarrierSlaRanks(link.getShopId(), props.toString());

                    }

                } // else skip updates for inaccessible shops
            }

            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public void removeCarrier(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {

            dtoCarrierService.remove(id);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    @Override
    public List<VoCarrierSla> getCarrierSlas(final long carrierId) throws Exception {
        if (federationFacade.isManageable(carrierId, CarrierDTO.class)) {
            final List<CarrierSlaDTO> slas = dtoCarrierSlaService.findByCarrier(carrierId);
            return voAssemblySupport.assembleVos(VoCarrierSla.class, CarrierSlaDTO.class, slas);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoSearchResult<VoCarrierSlaInfo> getFilteredCarrierSlas(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoCarrierSlaInfo> result = new VoSearchResult<>();
        final List<VoCarrierSlaInfo> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final VoSearchContext allShopCarriersCtx = new VoSearchContext();
        allShopCarriersCtx.setSize(1000);
        final VoSearchResult<VoCarrierInfo> carriers = getFilteredCarriers(allShopCarriersCtx);
        if (carriers.getTotal() > 0) {

            final List<Long> carrierIds = carriers.getItems().stream().map(VoCarrierInfo::getCarrierId).collect(Collectors.toList());

            final Map<String, List> all = filter.getParameters() != null ? new HashMap<>(filter.getParameters()) : new HashMap<>();
            all.put("carrierIds", carrierIds);

            final SearchContext searchContext = new SearchContext(
                    all,
                    filter.getStart(),
                    filter.getSize(),
                    filter.getSortBy(),
                    filter.isSortDesc(),
                    "filter", "carrierIds"
            );

            final SearchResult<CarrierSlaDTO> batch = dtoCarrierSlaService.findCarrierSlas(searchContext);
            if (!batch.getItems().isEmpty()) {
                results.addAll(voAssemblySupport.assembleVos(VoCarrierSlaInfo.class, CarrierSlaDTO.class, batch.getItems()));
            }

            result.setTotal(batch.getTotal());

        }

        return result;
    }

    @Override
    public VoCarrierSla getCarrierSlaById(final long id) throws Exception {
        final CarrierSlaDTO sla = dtoCarrierSlaService.getById(id);
        if (sla != null) {
            if (federationFacade.isManageable(sla.getCarrierId(), CarrierDTO.class)) {
                return voAssemblySupport.assembleVo(VoCarrierSla.class, CarrierSlaDTO.class, new VoCarrierSla(), sla);
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    @Override
    public VoCarrierSla createCarrierSla(final VoCarrierSla vo) throws Exception {
        if (vo != null && federationFacade.isManageable(vo.getCarrierId(), CarrierDTO.class)) {
            CarrierSlaDTO sla = dtoCarrierSlaService.getNew();
            sla.setCarrierId(vo.getCarrierId());
            sla = dtoCarrierSlaService.create(
                    voAssemblySupport.assembleDto(CarrierSlaDTO.class, VoCarrierSla.class, sla, vo)
            );
            return getCarrierSlaById(sla.getCarrierslaId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public VoCarrierSla updateCarrierSla(final VoCarrierSla vo) throws Exception {
        final CarrierSlaDTO sla = dtoCarrierSlaService.getById(vo.getCarrierslaId());
        if (sla != null && federationFacade.isManageable(sla.getCarrierId(), CarrierDTO.class)) {
            dtoCarrierSlaService.update(
                    voAssemblySupport.assembleDto(CarrierSlaDTO.class, VoCarrierSla.class, sla, vo)
            );
            return getCarrierSlaById(sla.getCarrierslaId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public void removeCarrierSla(final long id) throws Exception {
        final CarrierSlaDTO sla = dtoCarrierSlaService.getById(id);
        if (sla != null && federationFacade.isManageable(sla.getCarrierId(), CarrierDTO.class)) {
            dtoCarrierSlaService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
