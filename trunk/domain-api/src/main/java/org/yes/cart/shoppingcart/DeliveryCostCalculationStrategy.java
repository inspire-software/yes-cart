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

package org.yes.cart.shoppingcart;

import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 5:53 PM
 */
public interface DeliveryCostCalculationStrategy {

    /**
     * Get the delivery price for cart.
     *
     * @param cart current cart
     *
     * @return price of delivery
     */
    BigDecimal getDeliveryPrice(ShoppingCart cart);

}
