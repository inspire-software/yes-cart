/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.domain.dto.CountryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CountryDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCountryService;

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
     * @param countryGenericService    generic counry service
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
    public Class<CountryDTO> getDtoIFace() {
        return CountryDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<CountryDTOImpl> getDtoImpl() {
        return CountryDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Country> getEntityIFace() {
        return Country.class;
    }
}
