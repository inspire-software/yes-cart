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

package org.yes.cart.bulkcommon.model;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 07:53
 */
public interface ImpExContext {

    /**
     * Get shop context or null if no specific shop is defined.
     *
     * @return shop code
     */
    String getShopCode();

    /**
     * Get shop context column name (dynamic lookup).
     *
     * @return name of column that holds shop code
     */
    String getShopCodeColumn();

}
