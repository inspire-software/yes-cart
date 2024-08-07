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

package org.yes.cart.bulkjob.promotion;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * User: denispavlov
 * Date: 17/04/2018
 * Time: 16:20
 */
public interface BulkCustomerTagProcessorInternal {

    /**
     * Update customer tags.
     *
     * @param customers customers
     */
    void updateCustomers(List<Pair<Customer, String>> customers);

}
