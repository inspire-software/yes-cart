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
import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoCountryService;
import org.yes.cart.service.dto.DtoStateService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoLocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 14:45
 */
public class VoLocationServiceImpl implements VoLocationService {

    private final DtoCountryService dtoCountryService;
    private final DtoStateService dtoStateService;

    private final FederationFacade federationFacade;

    private final VoAssemblySupport voAssemblySupport;

    public VoLocationServiceImpl(final DtoCountryService dtoCountryService,
                                 final DtoStateService dtoStateService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport) {
        this.dtoCountryService = dtoCountryService;
        this.dtoStateService = dtoStateService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    @Override
    public VoSearchResult<VoCountryInfo> getFilteredCountries(VoSearchContext filter) throws Exception {

        final VoSearchResult<VoCountryInfo> result = new VoSearchResult<>();
        final List<VoCountryInfo> results = new ArrayList<>();
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

        final SearchResult<CountryDTO> batch = dtoCountryService.findCountries(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoCountryInfo.class, CountryDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;

    }

    /** {@inheritDoc} */
    @Override
    public VoCountry getCountryById(final long id) throws Exception {
        final CountryDTO countryDTO = dtoCountryService.getById(id);
        if (countryDTO != null) {
            final VoCountry country = voAssemblySupport.assembleVo(VoCountry.class, CountryDTO.class, new VoCountry(), countryDTO);
            country.setStates(getCountryStatesInternal(countryDTO));
            return country;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public VoCountry createCountry(final VoCountry vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            CountryDTO countryDTO = dtoCountryService.getNew();
            countryDTO = dtoCountryService.create(
                    voAssemblySupport.assembleDto(CountryDTO.class, VoCountry.class, countryDTO, vo)
            );
            return getCountryById(countryDTO.getCountryId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoCountry updateCountry(final VoCountry vo) throws Exception {
        CountryDTO countryDTO = dtoCountryService.getById(vo.getCountryId());
        if (countryDTO != null && federationFacade.isCurrentUserSystemAdmin()) {
            countryDTO = dtoCountryService.update(
                    voAssemblySupport.assembleDto(CountryDTO.class, VoCountry.class, countryDTO, vo)
            );
            return getCountryById(countryDTO.getCountryId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeCountry(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoCountryService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<VoState> getCountryStatesAll(final long id) throws Exception {
        CountryDTO countryDTO = dtoCountryService.getById(id);
        final List<VoState> rez = new ArrayList<>();
        if (countryDTO != null) {
            return getCountryStatesInternal(countryDTO);
        }
        return rez;
    }

    List<VoState> getCountryStatesInternal(final CountryDTO countryDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<StateDTO> all = dtoStateService.findByCountry(countryDTO.getCountryCode());
        return voAssemblySupport.assembleVos(VoState.class, StateDTO.class, all);
    }

    @Override
    public VoSearchResult<VoState> getFilteredStates(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoState> result = new VoSearchResult<>();
        final List<VoState> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "countryCodes"
        );

        final SearchResult<StateDTO> batch = dtoStateService.findStates(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoState.class, StateDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;

    }

    /** {@inheritDoc} */
    @Override
    public VoState getStateById(final long id) throws Exception {
        final StateDTO stateDTO = dtoStateService.getById(id);
        if (stateDTO != null) {
            return voAssemblySupport.assembleVo(VoState.class, StateDTO.class, new VoState(), stateDTO);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public VoState createState(final VoState vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            StateDTO stateDTO = dtoStateService.getNew();
            stateDTO = dtoStateService.create(
                    voAssemblySupport.assembleDto(StateDTO.class, VoState.class, stateDTO, vo)
            );
            return getStateById(stateDTO.getStateId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public VoState updateState(final VoState vo) throws Exception {
        StateDTO stateDTO = dtoStateService.getById(vo.getStateId());
        if (stateDTO != null && federationFacade.isCurrentUserSystemAdmin()) {
            stateDTO = dtoStateService.update(
                    voAssemblySupport.assembleDto(StateDTO.class, VoState.class, stateDTO, vo)
            );
            return getStateById(stateDTO.getStateId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeState(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoStateService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

}
