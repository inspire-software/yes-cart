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
import org.yes.cart.domain.dto.ProductOptionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductOptionDTOImpl;
import org.yes.cart.domain.entity.ProductOption;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoProductOptionService;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 13:55
 */
public class DtoProductOptionServiceImpl
    extends AbstractDtoServiceImpl<ProductOptionDTO, ProductOptionDTOImpl, ProductOption>
        implements DtoProductOptionService {

    public DtoProductOptionServiceImpl(final GenericService<ProductOption> service,
                                       final DtoFactory dtoFactory,
                                       final AdaptersRepository adaptersRepository) {
        super(dtoFactory, service, adaptersRepository);
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<ProductOptionDTO> getDtoIFace() {
        return ProductOptionDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<ProductOptionDTOImpl> getDtoImpl() {
        return ProductOptionDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<ProductOption> getEntityIFace() {
        return ProductOption.class;
    }
}
