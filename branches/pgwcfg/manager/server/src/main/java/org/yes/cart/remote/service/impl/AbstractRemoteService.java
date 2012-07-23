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


import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class AbstractRemoteService<TDTOIFACE> implements GenericDTOService<TDTOIFACE> {


    private final GenericDTOService<TDTOIFACE> genericDTOService;

    public AbstractRemoteService(final GenericDTOService<TDTOIFACE> genericDTOService) {
        this.genericDTOService = genericDTOService;
    }

    /**
     * Get generic dto service.
     *
     * @return {@link GenericDTOService}
     */
    public GenericDTOService<TDTOIFACE> getGenericDTOService() {
        return genericDTOService;
    }

    /**
     * {@inheritDoc}
     */
    public GenericService getService() {
        return genericDTOService.getService();
    }


    /**
     * {@inheritDoc}
     */
    public List<TDTOIFACE> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getById(id, converters);
    }


    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getNew() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        return genericDTOService.getNew();
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE create(final TDTOIFACE instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE update(final TDTOIFACE instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) {
        genericDTOService.remove(id);
    }


}
