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
import org.yes.cart.domain.vo.VoTax;
import org.yes.cart.domain.vo.VoTaxConfig;
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

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;


    public VoTaxServiceImpl(final DtoTaxService dtoTaxService,
                            final DtoTaxConfigService dtoTaxConfigService,
                            final FederationFacade federationFacade,
                            final VoAssemblySupport voAssemblySupport) {
        this.dtoTaxService = dtoTaxService;
        this.dtoTaxConfigService = dtoTaxConfigService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /**
     * {@inheritDoc}
     */
    public List<VoTax> getFilteredTax(final String shopCode, final String currency, final String filter, final int max) throws Exception {

        final List<VoTax> list = new ArrayList<>();

        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {

            final List<TaxDTO> dtos = dtoTaxService.findBy(shopCode, currency, filter, 0, max);
            return voAssemblySupport.assembleVos(VoTax.class, TaxDTO.class, dtos);

        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
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
    public VoTax createTax(final VoTax vo) throws Exception {
        if (federationFacade.isManageable(vo.getShopCode(), ShopDTO.class)) {
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
    public void removeTax(final long id) throws Exception {

        getTaxById(id); // check access
        dtoTaxService.remove(id);

    }

    /**
     * {@inheritDoc}
     */
    public List<VoTaxConfig> getFilteredTaxConfig(final long taxId, final String filter, final int max) throws Exception {

        getTaxById(taxId); // check access

        final List<TaxConfigDTO> configs = dtoTaxConfigService.findBy(taxId, filter, 0, max);

        return voAssemblySupport.assembleVos(VoTaxConfig.class, TaxConfigDTO.class, configs);
    }

    /**
     * {@inheritDoc}
     */
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
    public void removeTaxConfig(final long id) throws Exception {

        getTaxConfigById(id); // check access
        dtoTaxConfigService.remove(id);

    }
}
