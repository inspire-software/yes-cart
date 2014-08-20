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

package org.yes.cart.web.service.rest;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.yes.cart.domain.dto.factory.DtoFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/08/2014
 * Time: 09:11
 */
public class AbstractApiController {


    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    @Autowired
    public AbstractApiController(@Qualifier("roInterfaceToClassFactory") final DtoFactory dtoFactory,
                                 @Qualifier("roAssemblerAdaptersRepository") final AdaptersRepository adaptersRepository) {
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;
    }

    /**
     * Generic mapping method.
     *
     * @param objects object to map
     * @param ro RO class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    protected <RO, Entity> List<RO> map(final List<Entity> objects, final Class<RO> ro, final Class<Entity> entity) {
        final List<RO> ros = new ArrayList<RO>();
        if (objects != null) {
            DTOAssembler.newAssembler(ro, entity).assembleDtos(ros, objects, adaptersRepository.getAll(), dtoFactory);
        }
        return ros;
    }

    /**
     * Generic mapping method.
     *
     * @param object object to map
     * @param ro RO class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    protected <RO, Entity> RO map(final Entity object, final Class<RO> ro, final Class<Entity> entity) {
        if (entity != null) {
            final RO dto = dtoFactory.getByIface(ro);
            DTOAssembler.newAssembler(ro, entity).assembleDto(dto, object, adaptersRepository.getAll(), dtoFactory);
            return dto;
        }
        return null;
    }


}
