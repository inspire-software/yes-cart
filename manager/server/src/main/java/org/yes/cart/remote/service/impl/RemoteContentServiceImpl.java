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
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteContentService;
import org.yes.cart.service.dto.DtoContentService;

import java.util.List;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class RemoteContentServiceImpl
        extends AbstractRemoteService<CategoryDTO>
        implements RemoteContentService {


    /**
     * Construct remote service.
     *
     * @param dtoContentService dto service.
     */
    public RemoteContentServiceImpl(
            final DtoContentService dtoContentService) {
        super(dtoContentService);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).getAllByShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllWithAvailabilityFilter(final long shopId, final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).getAllWithAvailabilityFilter(shopId, withAvailabilityFiltering);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).getEntityAttributes(entityPk);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityContentAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).getEntityContentAttributes(entityPk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ((DtoContentService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


}
