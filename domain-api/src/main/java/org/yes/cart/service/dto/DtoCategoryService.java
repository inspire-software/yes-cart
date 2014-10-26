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

import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ShopCategoryDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCategoryService extends GenericDTOService<CategoryDTO>, GenericAttrValueService {

    /**
     * Get all assigned to shop categories.
     *
     * @param shopId shop id
     * @return list of assigned categories
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get all categories with or without availability date range filtering.
     *
     * @param withAvailabilityFiltering true if need to filter
     * @return list of categories
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<CategoryDTO> getAllWithAvailabilityFilter(boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Assign category to shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     * @return {@link org.yes.cart.domain.entity.ShopCategory}
     */
    ShopCategoryDTO assignToShop(long categoryId, long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Unassign category from shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     */
    void unassignFromShop(long categoryId, long shopId);

    /**
     * Get all categories, that contains product with given id.
     *
     * @param productId given product id.
     * @return list of categories, that contains product.
     */
    List<CategoryDTO> getByProductId(long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Check if URI is available to be set for given category.
     *
     * @param seoUri uri to check
     * @param categoryId PK or null for transient
     *
     * @return true if this URI is available
     */
    boolean isUriAvailableForCategory(String seoUri, Long categoryId);


}
