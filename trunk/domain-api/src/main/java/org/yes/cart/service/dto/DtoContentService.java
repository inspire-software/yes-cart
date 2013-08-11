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

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Denis Pavlov
 * Date: 27-June-2013
 */
public interface DtoContentService extends GenericDTOService<CategoryDTO>, GenericAttrValueService {

    /**
     * Create root content for given shop if none exists.
     *
     * @param shopId shop id
     */
    void createContentRoot(final long shopId);

    /**
     * Get all assigned to shop content.
     *
     * @param shopId shop id
     * @return list of assigned content
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get all content with or without availability date range filtering.
     *
     * @param shopId shop id
     * @param withAvailabilityFiltering true if need to filter
     * @return list of categories
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<CategoryDTO> getAllWithAvailabilityFilter(final long shopId, boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the business entity attributes values, that contains two set of attribute values: with values
     * and with empty values for available attributes. Example:
     * category type has attr1, attr2 and attr3, particular category
     * has attr1 and attr3 with values, so service return 3 records:
     * attr1 - value
     * attr2 - empty value
     * attr3 value.
     * <p/>
     * This service mostly for UI.
     *
     * @param entityPk entity pk value
     * @return list of attribute values
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of configuration errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration errors
     */
    List<? extends AttrValueDTO> getEntityContentAttributes(long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
