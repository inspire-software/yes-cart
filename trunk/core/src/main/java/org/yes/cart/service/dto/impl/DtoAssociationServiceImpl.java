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
import org.yes.cart.domain.dto.AssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AssociationDTOImpl;
import org.yes.cart.domain.entity.Association;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoAssociationService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAssociationServiceImpl
        extends AbstractDtoServiceImpl<AssociationDTO, AssociationDTOImpl, Association>
        implements DtoAssociationService {

    /**
     * Construct association remote service.
     *
     * @param dtoFactory                {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param associationGenericService {@link org.yes.cart.service.domain.GenericService}
     * @param AdaptersRepository  {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoAssociationServiceImpl(final DtoFactory dtoFactory,
                                     final GenericService<Association> associationGenericService,
                                     final AdaptersRepository AdaptersRepository) {
        super(dtoFactory, associationGenericService, AdaptersRepository);
    }


    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<AssociationDTO> getDtoIFace() {
        return AssociationDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<AssociationDTOImpl> getDtoImpl() {
        return AssociationDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Association> getEntityIFace() {
        return Association.class;
    }
}
