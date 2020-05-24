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

package org.yes.cart.service.order;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * User: denispavlov
 * Date: 26/01/2020
 * Time: 22:17
 */
public interface OrderAssemblerPostProcessor {

    /**
     * Post process assembled order.
     *
     * @param customerOrder order assmebled by order assembler
     * @param shoppingCart  shopping cart from which order is assembled
     * @param orderNumber   order number used
     * @param temp          true if this is temporary order assmebly
     *
     * @throws OrderAssemblyException order assembly exception
     */
    void postProcess(CustomerOrder customerOrder,
                     ShoppingCart shoppingCart,
                     String orderNumber,
                     boolean temp) throws OrderAssemblyException;

}
