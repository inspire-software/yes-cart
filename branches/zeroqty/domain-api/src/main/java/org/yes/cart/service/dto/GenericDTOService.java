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

package org.yes.cart.service.dto;

import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface GenericDTOService<T> {

    /**
     * Get all attributes group.
     *
     * @return list of all <T>.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<T> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get single attribute by given code.
     *
     * @param id given primary key value
     * @return instance if found, otherwise null.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    T getById(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get single attribute by given code.
     *
     * @param id         given primary key value
     * @param converters map of converters
     * @return instance if found, otherwise null.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    T getById(long id, final Map converters)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create new empty DTO.
     *
     * @return new instance
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    T getNew() throws UnableToCreateInstanceException, UnmappedInterfaceException;

    /**
     * Persist given DTO
     *
     * @param instance DTO to persist
     * @return persisted DTO
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    T create(T instance) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update given DTO
     *
     * @param instance DTO to persist
     * @return persisted DTO
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    T update(T instance) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Delete  by given primary key value.
     *
     * @param id given primary key value
     */
    void remove(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get generic service. Please, use this method to getByKey related dao service from generic service,
     * rather then inject the dao direclty into dto services.
     *
     * @return {@link org.yes.cart.service.domain.GenericService}
     */
    GenericService getService();


}
