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
import org.yes.cart.domain.dto.EtypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.EtypeDTOImpl;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.dto.DtoEtypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoEtypeServiceImpl
        extends AbstractDtoServiceImpl<EtypeDTO, EtypeDTOImpl, Etype>
        implements DtoEtypeService {


    /**
     * Constrict remote service.
     * @param etypeService {@link EtypeService}
     * @param dtoFactory {@link DtoFactory}
     */
    public DtoEtypeServiceImpl(final EtypeService etypeService,
                               final DtoFactory dtoFactory,
                               final AdaptersRepository adaptersRepository) {
        super(dtoFactory, etypeService, adaptersRepository);
    }



    /** {@inheritDoc} */
    public Class<EtypeDTO> getDtoIFace() {
        return EtypeDTO.class;
    }

    /** {@inheritDoc} */
    public Class<EtypeDTOImpl> getDtoImpl() {
        return EtypeDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Etype> getEntityIFace() {
        return Etype.class;
    }

}
