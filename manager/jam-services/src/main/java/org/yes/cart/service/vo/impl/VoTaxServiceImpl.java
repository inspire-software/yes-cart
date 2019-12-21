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
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoTaxConfigService;
import org.yes.cart.service.dto.DtoTaxService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoTaxService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 14/09/2016
 * Time: 18:25
 */
public class VoTaxServiceImpl implements VoTaxService {

    private final DtoTaxService dtoTaxService;
    private final DtoTaxConfigService dtoTaxConfigService;

    private final ShopService shopService;
    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;


    public VoTaxServiceImpl(final DtoTaxService dtoTaxService,
                            final DtoTaxConfigService dtoTaxConfigService,
                            final ShopService shopService,
                            final FederationFacade federationFacade,
                            final VoAssemblySupport voAssemblySupport) {
        this.shopService = shopService;
        this.dtoTaxService = dtoTaxService;
        this.dtoTaxConfigService = dtoTaxConfigService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoSearchResult<VoTax> getFilteredTax(final String shopCode, final String currency, final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoTax> result = new VoSearchResult<>();
        final List<VoTax> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        if (!federationFacade.isManageable(shopCode, ShopDTO.class)) {
            return result;
        }

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter"
        );


        final SearchResult<TaxDTO> batch = dtoTaxService.findTaxes(shopCode, currency, searchContext);
        if (!batch.getItems().isEmpty()) {
            results.addAll(voAssemblySupport.assembleVos(VoTax.class, TaxDTO.class, batch.getItems()));
        }

        result.setTotal(batch.getTotal());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoTax getTaxById(final long id) throws Exception {
        final TaxDTO dto = dtoTaxService.getById(id);
        if (federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            return voAssemblySupport.assembleVo(VoTax.class, TaxDTO.class, new VoTax(), dto);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoTax updateTax(final VoTax vo) throws Exception {
        final TaxDTO dto = dtoTaxService.getById(vo.getTaxId());
        if (dto != null && federationFacade.isManageable(dto.getShopCode(), ShopDTO.class)) {
            dtoTaxService.update(
                    voAssemblySupport.assembleDto(TaxDTO.class, VoTax.class, dto, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getTaxById(vo.getTaxId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoTax createTax(final VoTax vo) throws Exception {
        if (federationFacade.isManageable(vo.getShopCode(), ShopDTO.class)) {
            final Shop shop = shopService.getShopByCode(vo.getShopCode());
            if (shop.getMaster() != null) {
                vo.setShopCode(shop.getMaster().getCode());
            }
            TaxDTO dto = dtoTaxService.getNew();
            dto = dtoTaxService.create(
                    voAssemblySupport.assembleDto(TaxDTO.class, VoTax.class, dto, vo)
            );
            return getTaxById(dto.getTaxId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTax(final long id) throws Exception {

        getTaxById(id); // check access
        dtoTaxService.remove(id);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoSearchResult<VoTaxConfig> getFilteredTaxConfig(final long taxId, final VoSearchContext filter) throws Exception {

        getTaxById(taxId); // check access

        final VoSearchResult<VoTaxConfig> result = new VoSearchResult<>();
        final List<VoTaxConfig> results = new ArrayList<>();
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

        final SearchResult<TaxConfigDTO> batch = dtoTaxConfigService.findTaxConfigs(taxId, searchContext);
        if (!batch.getItems().isEmpty()) {
            results.addAll(voAssemblySupport.assembleVos(VoTaxConfig.class, TaxConfigDTO.class, batch.getItems()));
        }

        result.setTotal(batch.getTotal());

        return result;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoTaxConfig getTaxConfigById(final long id) throws Exception {

        final TaxConfigDTO dto = dtoTaxConfigService.getById(id);
        if (dto != null && getTaxById(dto.getTaxId()) != null) {
            return voAssemblySupport.assembleVo(VoTaxConfig.class, TaxConfigDTO.class, new VoTaxConfig(), dto);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoTaxConfig createTaxConfig(final VoTaxConfig vo) throws Exception {

        getTaxById(vo.getTaxId()); // check access

        TaxConfigDTO dto = dtoTaxConfigService.getNew();
        dto = dtoTaxConfigService.create(
                voAssemblySupport.assembleDto(TaxConfigDTO.class, VoTaxConfig.class, dto, vo)
        );
        return getTaxConfigById(dto.getTaxConfigId());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTaxConfig(final long id) throws Exception {

        getTaxConfigById(id); // check access
        dtoTaxConfigService.remove(id);

    }
}
