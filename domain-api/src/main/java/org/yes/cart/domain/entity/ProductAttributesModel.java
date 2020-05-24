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

package org.yes.cart.domain.entity;

import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/05/2020
 * Time: 23:20
 */
public interface ProductAttributesModel extends Serializable {

    /**
     * Get product ID.
     *
     * @return product ID
     */
    long getProductId();

    /**
     * Get product code.
     *
     * @return product code
     */
    String getProductCode();

    /**
     * Get SKU ID.
     *
     * @return SKU ID.
     */
    long getSkuId();

    /**
     * Get SKU code.
     *
     * @return SKU code
     */
    String getSkuCode();

    /**
     * Get product type ID
     *
     * @return product type
     */
    long getProductType();

    /**
     * Get all groups applicable for given product type.
     *
     * @return groups of attributes
     */
    List<ProductAttributesModelGroup> getGroups();

    /**
     * Get group by code.
     *
     * @param code code
     *
     * @return group
     */
    ProductAttributesModelGroup getGroup(String code);

    /**
     * Get attributes by code (could be in multiple groups)
     *
     * @param code code
     *
     * @return attributes
     */
    List<ProductAttributesModelAttribute> getAttributes(String code);

}
