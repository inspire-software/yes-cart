/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
public interface ProductCompareModel extends Serializable {

    /**
     * Get all groups applicable for given product type.
     *
     * @return groups of attributes
     */
    List<ProductCompareModelGroup> getGroups();

    /**
     * Get group by code.
     *
     * @param code code
     *
     * @return group
     */
    ProductCompareModelGroup getGroup(String code);

    /**
     * Get attributes by code (could be in multiple groups)
     *
     * @param code code
     *
     * @return attributes
     */
    List<ProductCompareModelAttribute> getAttributes(String code);

}
