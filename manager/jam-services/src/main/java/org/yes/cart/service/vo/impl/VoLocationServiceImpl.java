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

import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.domain.vo.VoCountry;
import org.yes.cart.domain.vo.VoState;
import org.yes.cart.service.dto.DtoCountryService;
import org.yes.cart.service.dto.DtoStateService;
import org.yes.cart.service.federation.FederationFacade;
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

    public VoLocationServiceImpl(final DtoCountryService dtoCountryService,
                                 final DtoStateService dtoStateService,
                                 final FederationFacade federationFacade) {
        this.dtoCountryService = dtoCountryService;
        this.dtoStateService = dtoStateService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    @Override
    public List<VoCountry> getAllCountries() throws Exception {
        final List<CountryDTO> all = dtoCountryService.getAll();
        final List<VoCountry> rez = new ArrayList<>(all.size());
        DTOAssembler.newAssembler(VoCountry.class, CountryDTO.class).assembleDtos(rez, all, null, null);
        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public VoCountry getCountryById(final long id) throws Exception {
        final CountryDTO countryDTO = dtoCountryService.getById(id);
        if (countryDTO != null) {
            final VoCountry voCountry = new VoCountry();
            DTOAssembler.newAssembler(VoCountry.class, CountryDTO.class).assembleDto(voCountry, countryDTO, null, null);
            return voCountry;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public VoCountry createCountry(final VoCountry vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            CountryDTO countryDTO = dtoCountryService.getNew();
            DTOAssembler.newAssembler(VoCountry.class, CountryDTO.class).assembleEntity(vo, countryDTO, null, null);
            countryDTO = dtoCountryService.create(countryDTO);
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
            DTOAssembler.newAssembler(VoCountry.class, CountryDTO.class).assembleEntity(vo, countryDTO, null, null);
            countryDTO = dtoCountryService.update(countryDTO);
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
    public List<VoState> getAllStates(final long id) throws Exception {
        CountryDTO countryDTO = dtoCountryService.getById(id);
        final List<VoState> rez = new ArrayList<>();
        if (countryDTO != null) {
            final List<StateDTO> all = dtoStateService.findByCountry(countryDTO.getCountryCode());
            DTOAssembler.newAssembler(VoState.class, StateDTO.class).assembleDtos(rez, all, null, null);
        }
        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public VoState getStateById(final long id) throws Exception {
        final StateDTO stateDTO = dtoStateService.getById(id);
        if (stateDTO != null) {
            final VoState voState = new VoState();
            DTOAssembler.newAssembler(VoState.class, StateDTO.class).assembleDto(voState, stateDTO, null, null);
            return voState;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public VoState createState(final VoState vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            StateDTO stateDTO = dtoStateService.getNew();
            DTOAssembler.newAssembler(VoState.class, StateDTO.class).assembleEntity(vo, stateDTO, null, null);
            stateDTO = dtoStateService.create(stateDTO);
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
            DTOAssembler.newAssembler(VoState.class, StateDTO.class).assembleEntity(vo, stateDTO, null, null);
            stateDTO = dtoStateService.update(stateDTO);
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
