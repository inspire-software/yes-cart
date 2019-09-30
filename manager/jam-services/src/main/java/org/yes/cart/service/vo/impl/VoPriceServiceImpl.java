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
import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.impl.PriceListDTOImpl;
import org.yes.cart.domain.vo.VoPriceList;
import org.yes.cart.service.dto.DtoPriceListsService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPriceService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 13:52
 */
public class VoPriceServiceImpl implements VoPriceService {

    private final DtoPriceListsService dtoPriceListsService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;


    public VoPriceServiceImpl(final DtoPriceListsService dtoPriceListsService,
                              final FederationFacade federationFacade,
                              final VoAssemblySupport voAssemblySupport) {
        this.dtoPriceListsService = dtoPriceListsService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoPriceList> getFilteredPrices(final long shopId, final String currency, final String filter, final int max) throws Exception {

        final List<VoPriceList> list = new ArrayList<>();

        if (federationFacade.isManageable(shopId, ShopDTO.class)) {

            final List<PriceListDTO> dtos = dtoPriceListsService.findBy(shopId, currency, filter, 0, max);
            return voAssemblySupport.assembleVos(VoPriceList.class, PriceListDTO.class, dtos);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoPriceList getPriceById(final long id) throws Exception {
        final PriceListDTO dto = dtoPriceListsService.getById(id);
        if (federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            return voAssemblySupport.assembleVo(VoPriceList.class, PriceListDTO.class, new VoPriceList(), dto);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoPriceList updatePrice(final VoPriceList vo) throws Exception {
        final PriceListDTO dto = dtoPriceListsService.getById(vo.getSkuPriceId());
        if (dto != null && federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            dtoPriceListsService.updatePrice(
                    voAssemblySupport.assembleDto(PriceListDTO.class, VoPriceList.class, dto, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getPriceById(vo.getSkuPriceId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoPriceList createPrice(final VoPriceList vo) throws Exception {
        if (federationFacade.isManageable(vo.getShopCode(), ShopDTO.class)) {
            PriceListDTO dto = new PriceListDTOImpl();
            dto = dtoPriceListsService.createPrice(
                    voAssemblySupport.assembleDto(PriceListDTO.class, VoPriceList.class, dto, vo)
            );
            return getPriceById(dto.getSkuPriceId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePrice(final long id) throws Exception {

        getPriceById(id); // check access
        dtoPriceListsService.removePrice(id);

    }
}
