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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/10/2019
 * Time: 09:52
 */
public interface ManagerServiceFacade {

    /**
     * Get pagination options configuration.
     *
     * @param customerShopId current shop
     *
     * @return pagination options
     */
    List<String> getItemsPerPageOptionsConfig(long customerShopId);

    /**
     * Get sorting options configuration.
     *
     * @param customerShopId current shop
     *
     * @return sorting options
     */
    List<String> getPageSortingOptionsConfig(long customerShopId);

    /**
     * Get category viewing image size configuration.
     *
     * @param customerShopId current shop
     *
     * @return first width, second height
     */
    Pair<String, String> getCustomerListImageSizeConfig(long customerShopId);

    /**
     * Get number of columns options configuration.
     *
     * @param customerShopId current shop
     *
     * @return product columns options
     */
    int getCustomerListColumnOptionsConfig(long customerShopId);

    /**
     * Get list of customers.
     *
     * Context Parameters:
     * shopId - current shop
     * email, firstname, lastname - OR'ed criteria
     *
     * @param searchContext context
     *
     * @return results
     */
    SearchResult<Customer> getCustomers(SearchContext searchContext);

}
