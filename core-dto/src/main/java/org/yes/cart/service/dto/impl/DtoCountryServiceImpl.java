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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CountryDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCountryService;

import java.util.*;

/**
 * Country dto service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCountryServiceImpl
        extends AbstractDtoServiceImpl<CountryDTO, CountryDTOImpl, Country>
        implements DtoCountryService {

    /**
     * Construct country dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param countryGenericService    generic country service
     * @param adaptersRepository value converter
     */
    public DtoCountryServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<Country> countryGenericService,
                                 final AdaptersRepository adaptersRepository) {
        super(dtoFactory, countryGenericService, adaptersRepository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResult<CountryDTO> findCountries(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final CountryService countryService = (CountryService) service;

        final Map<String, List> currentFilter = new HashMap<>();
        if (StringUtils.isNotBlank(textFilter)) {

            SearchContext.JoinMode.OR.setMode(currentFilter);
            currentFilter.put("countryCode", Collections.singletonList(textFilter));
            currentFilter.put("isoCode", Collections.singletonList(textFilter));
            currentFilter.put("name", Collections.singletonList(textFilter));
            currentFilter.put("guid", Collections.singletonList(textFilter));

        }

        final int count = countryService.findCountryCount(currentFilter);
        if (count > startIndex) {

            final List<CountryDTO> entities = new ArrayList<>();
            final List<Country> countries = countryService.findCountries(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(countries, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CountryDTO> getDtoIFace() {
        return CountryDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CountryDTOImpl> getDtoImpl() {
        return CountryDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Country> getEntityIFace() {
        return Country.class;
    }
}
