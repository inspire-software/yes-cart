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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductType;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductTypeService extends GenericService<ProductType> {

    /**
     * Find all product types that use attribute code.
     *
     * @param code {@link Attribute#getCode()}
     *
     * @return list of product types
     */
    List<ProductType> findByAttributeCode(String code);

    /**
     * Find all product types that are used in attribute navigation.
     *
     * @return list of product types
     */
    List<ProductType> findAllAssignedToCategories();


    /**
     * Find product type by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of types.
     */
    List<ProductType> findProductTypes(int start,
                                       int offset,
                                       String sort,
                                       boolean sortDescending,
                                       Map<String, List> filter);

    /**
     * Find product type by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findProductTypeCount(Map<String, List> filter);



}
