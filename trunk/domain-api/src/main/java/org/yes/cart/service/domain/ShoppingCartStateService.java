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

package org.yes.cart.service.domain;

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
     * Get all states that were modified before given date.
     *
     * @param lastModification last modification date
     *
     * @return all saved states that were not modified since given date
     */
    List<ShoppingCartState> findByModificationPrior(Date lastModification);

}
