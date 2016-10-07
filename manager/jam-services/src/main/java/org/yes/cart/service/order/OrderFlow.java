/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.order;

import java.util.List;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 20:02
 */
public interface OrderFlow {

    /**
     * Determine options for next transition of the order
     *
     * @param pgLabel payment gateway
     * @param currentStatus current order status
     *
     * @return transition options
     */
    List<String> getNext(String pgLabel, String currentStatus);

    /**
     * Determine action object for given action key.
     *
     * @param action action key
     *
     * @return action object (or null)
     */
    OrderFlowAction getAction(String action);

}
