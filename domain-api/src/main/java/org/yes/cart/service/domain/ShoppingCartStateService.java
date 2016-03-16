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

package org.yes.cart.service.domain;

import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.ShoppingCartState;

import java.util.Date;
import java.util.List;

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
     * @param email customer email
     *
     * @return get by customer email
     */
    List<ShoppingCartState> findByCustomerEmail(String email);

    /**
     * Get state by order number.
     *
     * @param ordernum order number
     *
     * @return get by order number
     */
    List<ShoppingCartState> findByOrdernum(String ordernum);

    /**
     * Get all states that were modified before given date.
     *
     * @param lastModification last modification date
     *
     * @return all saved states that were not modified since given date
     */
    ResultsIterator<ShoppingCartState> findByModificationPrior(Date lastModification);

    /**
     * Get all states that were modified before given date.
     *
     * @param lastModification last modification date
     * @param emptyAnonymous flag to indicate whether to select only empty anonymous carts
     *
     * @return all saved states that were not modified since given date
     */
    ResultsIterator<ShoppingCartState> findByModificationPrior(Date lastModification, boolean emptyAnonymous);

}
