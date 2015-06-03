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
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeAttrDTOImpl;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeAttrServiceImpl
        extends AbstractDtoServiceImpl<ProductTypeAttrDTO, ProductTypeAttrDTOImpl, ProductTypeAttr>
        implements DtoProductTypeAttrService {
    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productTypeAttrGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoProductTypeAttrServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<ProductTypeAttr> productTypeAttrGenericService,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, productTypeAttrGenericService, adaptersRepository);
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductTypeAttrDTO> getDtoIFace() {
        return ProductTypeAttrDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductTypeAttrDTOImpl> getDtoImpl() {
        return ProductTypeAttrDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductTypeAttr> getEntityIFace() {
        return ProductTypeAttr.class;
    }

    public List<ProductTypeAttrDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ProductTypeAttr> list = ((ProductTypeAttrService)service).getByProductTypeId(productTypeId);
        final List<ProductTypeAttrDTO> result = new ArrayList<ProductTypeAttrDTO>(list.size());
        fillDTOs(list, result);
        return result;
    }
}
