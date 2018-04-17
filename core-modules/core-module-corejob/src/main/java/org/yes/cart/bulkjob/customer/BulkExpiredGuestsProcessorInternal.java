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

package org.yes.cart.bulkjob.customer;

import org.yes.cart.domain.entity.Customer;

import java.util.List;

/**
 * User: denispavlov
 * Date: 17/04/2018
 * Time: 14:50
 */
public interface BulkExpiredGuestsProcessorInternal extends Runnable {

    /**
     * Remove guest accounts.
     *
     * @param guests guests to remove
     */
    void removeGuests(List<Customer> guests);

}
