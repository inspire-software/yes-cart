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

package org.yes.cart.service.order;

import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;

/**
 * Assemble {@link CustomerOrder} from {@link org.yes.cart.shoppingcart.ShoppingCart}.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface OrderAssembler {


    /**
     * Create and fill {@link CustomerOrder} from given {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @return order
     */
    CustomerOrder assembleCustomerOrder(ShoppingCart shoppingCart);

    /**
     * Create and fill {@link CustomerOrder} from given {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @param temp         true if not all data need to be filled
     * @return order
     */
    CustomerOrder assembleCustomerOrder(ShoppingCart shoppingCart, boolean temp);

    /**
     *
     * Format given address to string.
     *
     * @param defaultAddress given address
     * @return formated address
     */
    String formatAddress(Address defaultAddress);



}
