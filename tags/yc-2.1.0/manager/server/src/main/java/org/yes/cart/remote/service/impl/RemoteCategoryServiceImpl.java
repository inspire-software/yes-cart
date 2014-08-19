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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ShopCategoryDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCategoryService;
import org.yes.cart.service.dto.DtoCategoryService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCategoryServiceImpl
        extends AbstractRemoteService<CategoryDTO>
        implements RemoteCategoryService {


    /**
     * Construct remote service.
     *
     * @param dtoCategoryService dto service.
     */
    public RemoteCategoryServiceImpl(
            final DtoCategoryService dtoCategoryService) {
        super(dtoCategoryService);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).getAllByShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllWithAvailabilityFilter(final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).getAllWithAvailabilityFilter(withAvailabilityFiltering);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).getEntityAttributes(entityPk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public ShopCategoryDTO assignToShop(final long categoryId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).assignToShop(categoryId, shopId);
    }

    /**
     * {@inheritDoc}
     */
    public void unassignFromShop(final long categoryId, final long shopId) {
        ((DtoCategoryService) getGenericDTOService()).unassignFromShop(categoryId, shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getByProductId(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).getByProductId(productId);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


}
