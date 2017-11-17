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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.CartValidityModelMessage;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Collections;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 16:41
 */
public class CheckoutBlockedValidator extends AbstractCartContentsValidatorImpl {

    /** {@inheritDoc} */
    @Override
    public CartValidityModel validate(final ShoppingCart cart) {

        final boolean checkoutBlocked = Boolean.valueOf(cart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT));
        if (checkoutBlocked) {

            return new CartValidityModelImpl(
                    new CartValidityModelMessageImpl(
                            true,
                            CartValidityModelMessage.MessageType.ERROR,
                            "orderErrorCheckoutDisabled",
                            Collections.singletonMap("email", cart.getCustomerEmail())
                    )
            );
        }

        return OK;
    }
}
