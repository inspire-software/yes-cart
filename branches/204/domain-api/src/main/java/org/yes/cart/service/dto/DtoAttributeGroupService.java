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

import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAttributeGroupService extends GenericDTOService<AttributeGroupDTO> {


    /**
     * Get single attribute by given code.
     *
     * @param code given code
     * @return {@link AttributeGroup} if found, otherwise null.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    AttributeGroupDTO getAttributeGroupByCode(String code) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Persist {@link AttributeGroup}
     *
     * @param code        code
     * @param name        name
     * @param description description
     * @return created dto
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    AttributeGroupDTO create(String code, String name, String description) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update  {@link AttributeGroup} entity.
     *
     * @param code        code
     * @param name        name
     * @param description description
     * @return updated entity
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    AttributeGroupDTO update(String code, String name, String description) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Delete  {@link AttributeGroup} by given code.
     *
     * @param code code of {@link AttributeGroup} to delete
     */
    void remove(String code);


}
