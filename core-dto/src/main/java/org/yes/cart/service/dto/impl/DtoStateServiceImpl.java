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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.StateDTOImpl;
import org.yes.cart.domain.entity.State;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.service.dto.DtoStateService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoStateServiceImpl extends AbstractDtoServiceImpl<StateDTO, StateDTOImpl, State>
        implements DtoStateService {


    /**
     * Construct state dto service.
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param stateGenericService generic service
     * @param adaptersRepository     value converter
     */
    public DtoStateServiceImpl(final DtoFactory dtoFactory,
                               final GenericService<State> stateGenericService,
                               final AdaptersRepository adaptersRepository) {
        super(dtoFactory, stateGenericService, adaptersRepository);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResult<StateDTO> findStates(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "countryCodes");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final List countries = params.get("countryCodes");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final StateService stateService = (StateService) service;

        final Map<String, List> currentFilter = new HashMap<>();
        if (StringUtils.isNotBlank(textFilter)) {

            SearchContext.JoinMode.OR.setMode(currentFilter);
            currentFilter.put("countryCode", Collections.singletonList(textFilter));
            currentFilter.put("stateCode", Collections.singletonList(textFilter));
            currentFilter.put("name", Collections.singletonList(textFilter));
            currentFilter.put("guid", Collections.singletonList(textFilter));

        }

        if (CollectionUtils.isNotEmpty(countries)) {
            currentFilter.put("countryCodes", countries);
        }

        final int count = stateService.findStateCount(currentFilter);
        if (count > startIndex) {

            final List<StateDTO> entities = new ArrayList<>();
            final List<State> states = stateService.findStates(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(states, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);


    }

    /** {@inheritDoc} */
    @Override
    public Class<StateDTO> getDtoIFace() {
        return StateDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<StateDTOImpl> getDtoImpl() {
        return StateDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<State> getEntityIFace() {
        return State.class;
    }

    /** {@inheritDoc} */
    @Override
    public List<StateDTO> findByCountry(final String countryCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<State> states = ((StateService)service).findByCountry(countryCode);
        final List<StateDTO> out = getDTOs(states);
        out.sort((a, b) -> a.getStateCode().compareToIgnoreCase(b.getStateCode()));
        return out;
    }

}
