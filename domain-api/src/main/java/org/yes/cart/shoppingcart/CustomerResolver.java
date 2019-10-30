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

package org.yes.cart.shoppingcart;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 12:08
 */
public interface CustomerResolver {

    /**
     * Determine is manager login is enabled.
     *
     * @param shop shop
     *
     * @return true is manager login is enabled
     */
    boolean isManagerLoginEnabled(Shop shop);

    /**
     * Get customer by email.
     *
     * @param email email
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByEmail(String email, Shop shop);


    /**
     * Get customer shops by email.
     *
     * @param customer customer
     * @return List of {@link Shop} or null if customer not found
     */
    List<Shop> getCustomerShops(Customer customer);

    /**
     * Get name as specified by shop name formatting.
     *
     * @param customer customer
     * @param shop shop
     *
     * @return name
     */
    String formatNameFor(Customer customer, Shop shop);

    /**
     * Check is provided password for customer valid.
     *
     * @param email    email to check
     * @param shop     shop
     * @param password password
     * @return true in case if email unique.
     */
    boolean authenticate(String email, Shop shop, String password);

}
