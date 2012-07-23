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
import org.yes.cart.domain.dto.StateDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.StateDTOImpl;
import org.yes.cart.domain.entity.State;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.service.dto.DtoStateService;

import java.util.List;

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
     * @param stateGenericService generic serivce
     * @param AdaptersRepository     value converter
     */
    public DtoStateServiceImpl(final DtoFactory dtoFactory,
                               final GenericService<State> stateGenericService,
                               final AdaptersRepository AdaptersRepository) {
        super(dtoFactory, stateGenericService, AdaptersRepository);
    }


    /** {@inheritDoc} */
    public Class<StateDTO> getDtoIFace() {
        return StateDTO.class;
    }

    /** {@inheritDoc} */
    public Class<StateDTOImpl> getDtoImpl() {
        return StateDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<State> getEntityIFace() {
        return State.class;
    }

    /** {@inheritDoc} */
    public List<StateDTO> findByCountry(final String countryCode) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<State> states = ((StateService)service).findByCountry(countryCode);
        return getDTOs(states);
    }
}
