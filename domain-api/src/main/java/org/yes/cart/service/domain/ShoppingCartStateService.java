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

import org.yes.cart.domain.entity.ShoppingCartState;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 21:17
 */
public interface ShoppingCartStateService extends GenericService<ShoppingCartState> {

    /**
     * Get state by guid.
     *
     * @param guid guid
     *
     * @return get by guid
     */
    ShoppingCartState findByGuid(String guid);

    /**
     * Get state by guid.
     *
     * @param login customer login
     * @param shopId shop PK
     *
     * @return get by customer email and shop
     */
    List<ShoppingCartState> findByCustomerLogin(String login, long shopId);

    /**
     * Get state by order number.
     *
     * @param ordernum order number
     *
     * @return get by order number
     */
    List<ShoppingCartState> findByOrdernum(String ordernum);

    /**
     * Find shopping cart states by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list.
     */
    List<ShoppingCartState> findShoppingCartStates(int start,
                                                   int offset,
                                                   String sort,
                                                   boolean sortDescending,
                                                   Map<String, List> filter);

}
