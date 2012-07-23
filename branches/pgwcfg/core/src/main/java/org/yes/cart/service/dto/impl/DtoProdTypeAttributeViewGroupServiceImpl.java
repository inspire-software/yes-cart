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
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProdTypeAttributeViewGroupDTOImpl;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoProdTypeAttributeViewGroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:31 PM
 */
public class DtoProdTypeAttributeViewGroupServiceImpl
        extends AbstractDtoServiceImpl<ProdTypeAttributeViewGroupDTO, ProdTypeAttributeViewGroupDTOImpl, ProdTypeAttributeViewGroup>
        implements DtoProdTypeAttributeViewGroupService {
    
    private final GenericService<ProductType> productTypeService;


    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param prodTypeAttributeViewGroupGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param AdaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoProdTypeAttributeViewGroupServiceImpl(final DtoFactory dtoFactory,
                                                    final GenericService<ProdTypeAttributeViewGroup> prodTypeAttributeViewGroupGenericService,
                                                    final AdaptersRepository AdaptersRepository,
                                                    final GenericService<ProductType> productTypeService) {
        super(dtoFactory, prodTypeAttributeViewGroupGenericService, AdaptersRepository);
        this.productTypeService = productTypeService;
    }

    /** {@inheritDoc} */
    public List<ProdTypeAttributeViewGroupDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        //final List<ProdTypeAttributeViewGroup> list = getService().findByCriteria(Restrictions.eq("producttype.producttypeId", productTypeId)); hibernate plus derby fails
        ProductType prodType = productTypeService.getById(productTypeId);
        final List<ProdTypeAttributeViewGroupDTO> rez = new ArrayList<ProdTypeAttributeViewGroupDTO>(prodType.getAttributeViewGroup().size());
        fillDTOs(prodType.getAttributeViewGroup(), rez);
        return rez;


    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroupDTO> getDtoIFace() {
        return ProdTypeAttributeViewGroupDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroupDTOImpl> getDtoImpl() {
        return ProdTypeAttributeViewGroupDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroup> getEntityIFace() {
        return ProdTypeAttributeViewGroup.class;
    }
}
