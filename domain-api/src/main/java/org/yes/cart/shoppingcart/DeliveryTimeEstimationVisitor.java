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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

/**
 * User: denispavlov
 * Date: 06/02/2017
 * Time: 23:09
 */
public interface DeliveryTimeEstimationVisitor {

    /**
     * Visit customer order and evaluate the delivery time.
     *
     * @param order customer order
     */
    void visit(CustomerOrder order);

    /**
     * Visit customer order and evaluate the delivery time.
     *
     * @param delivery customer order delivery
     */
    void visit(CustomerOrderDelivery delivery);

    /**
     * Visit shopping cart and evaluate delivery time options.
     *
     * Populates {@link OrderInfo#getDetails()} with delivery time information for given
     * carrier sla selection.
     *
     * @param shoppingCart shopping cart
     */
    void visit(MutableShoppingCart shoppingCart);

}
