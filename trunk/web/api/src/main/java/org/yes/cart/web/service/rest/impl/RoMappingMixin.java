/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.service.rest.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.yes.cart.domain.dto.factory.DtoFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 08:33
 */
@Service("restRoMappingMixin")
public class RoMappingMixin {

    @Autowired
    @Qualifier("roInterfaceToClassFactory")
    private DtoFactory dtoFactory;
    @Autowired
    @Qualifier("roAssemblerAdaptersRepository")
    private AdaptersRepository adaptersRepository;

    /**
     * Generic mapping method.
     *
     * @param objects object to map
     * @param ro RO (requested object) class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    public <RO, Entity> List<RO> map(final List<Entity> objects, final Class<RO> ro, final Class<Entity> entity) {
        final List<RO> ros = new ArrayList<RO>();
        if (objects != null) {
            DTOAssembler.newAssembler(ro, entity).assembleDtos(ros, objects, adaptersRepository.getAll(), dtoFactory);
        }
        return ros;
    }

    /**
     * Generic mapping method.
     *
     * @param objects object to map
     * @param ro RO (requested object) class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return set of RO
     */
    public <RO, Entity> Set<RO> map(final Set<Entity> objects, final Class<RO> ro, final Class<Entity> entity) {
        final Set<RO> ros = new HashSet<RO>();
        if (objects != null) {
            DTOAssembler.newAssembler(ro, entity).assembleDtos(ros, objects, adaptersRepository.getAll(), dtoFactory);
        }
        return ros;
    }

    /**
     * Generic mapping method.
     *
     * @param object object to map
     * @param ro RO (requested object) class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    public <RO, Entity> RO map(final Entity object, final Class<RO> ro, final Class<Entity> entity) {
        if (entity != null) {
            final RO dto = (RO) dtoFactory.getByIface(ro);
            DTOAssembler.newAssembler(ro, entity).assembleDto(dto, object, adaptersRepository.getAll(), dtoFactory);
            return dto;
        }
        return null;
    }


}
