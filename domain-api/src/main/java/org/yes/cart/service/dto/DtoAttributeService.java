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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAttributeService extends GenericDTOService<AttributeDTO> {


    /**
     * Get {@link AttributeDTO} by given code.
     *
     * @param attributeCode given attribute group code
     * @return list of {@link AttributeDTO}
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    AttributeDTO getByAttributeCode(String attributeCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get {@link AttributeDTO} by given code.
     *
     * @param attributeCode given attribute group code
     * @return list of {@link AttributeDTO}
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    AttributeDTO findByAttributeCode(String attributeCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get list of {@link AttributeDTO} by given group code.
     *
     * @param attributeGroupCode given attribute group code
     * @return list of {@link AttributeDTO}
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findByAttributeGroupCode(String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get list of available attributes within given <code>attributeGroupCode</code>, that can be assigned to business entity.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @param assignedAttributeCodes list of codes, that already assigned to entity (hence excluded)
     * @return list of available attributes to fill with values.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findAvailableAttributes(String attributeGroupCode, List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the list of attributes, that belong to product type.
     *
     * @param productTypeId given product ype id
     * @return list of attributes
     */
    List<AttributeDTO> findAvailableAttributesByProductTypeId(long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the list of image attributes , that belong to attribute group.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @return list of attributes
     */
    List<AttributeDTO> findAvailableImageAttributesByGroupCode(String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get the list of attributes , that belong to group code and start with given prefix.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @param codePrefix             see Attribute#code
     * @return list of attributes
     */
    List<AttributeDTO> findAvailableAttributesByGroupCodeStartsWith(String attributeGroupCode, String codePrefix)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Find all attribute DTOs in given group , that allow to have several attributes
     *
     * @param attributeGroupCode group of attributes
     * @return list of attribute DTOs with allowed multiple values or null if no such attributes found
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findAttributesWithMultipleValues(
            String attributeGroupCode) throws UnmappedInterfaceException, UnableToCreateInstanceException;



    /**
     * Find attributes by filter.
     *
     * @param filter filter
     *
     * @return list of attributes
     */
    SearchResult<AttributeDTO> findAttributes(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
