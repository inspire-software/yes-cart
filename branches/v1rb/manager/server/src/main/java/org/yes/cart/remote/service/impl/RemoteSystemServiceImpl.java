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
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteSystemService;
import org.yes.cart.service.dto.DtoSystemService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:50 AM
 */
public class RemoteSystemServiceImpl implements RemoteSystemService {

    private final DtoSystemService dtoSystemService;

    public RemoteSystemServiceImpl(final DtoSystemService dtoSystemService) {
        this.dtoSystemService = dtoSystemService;
    }

    /** {@inheritDoc} */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoSystemService.getEntityAttributes(entityPk);
    }

    /** {@inheritDoc} */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoSystemService.updateEntityAttributeValue(attrValueDTO);
    }

    /** {@inheritDoc} */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoSystemService.createEntityAttributeValue(attrValueDTO);
    }

    /** {@inheritDoc} */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoSystemService.deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

}
