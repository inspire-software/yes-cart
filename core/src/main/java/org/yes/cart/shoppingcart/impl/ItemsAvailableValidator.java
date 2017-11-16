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

import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 16:41
 */
public class ItemsAvailableValidator extends AbstractCartContentsValidatorImpl {

    /** {@inheritDoc} */
    @Override
    public ValidationResult validate(final ShoppingCart cart) {

        if (cart.getCartItemsCount() == 0) {

            return new ValidationResultImpl(
                    true,
                    new Pair<String, Map<String, Object>>(
                            "emptyCart",
                            Collections.emptyMap()
                    )
            );

        }

        final List<Pair<String, Map<String, Object>>> unavailable = new ArrayList<Pair<String, Map<String, Object>>>();
        for (final Map.Entry<DeliveryBucket, List<CartItem>> bucketAndItems : cart.getCartItemMap().entrySet()) {

            if (CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP.equals(bucketAndItems.getKey().getGroup()) ||
                    CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP.equals(bucketAndItems.getKey().getGroup())) {

                for (final CartItem item : bucketAndItems.getValue()) {

                    unavailable.add(
                            new Pair<String, Map<String, Object>>(
                                    "orderErrorSkuInvalid",
                                    Collections.singletonMap("sku", "(" + item.getProductSkuCode() + ") " + item.getProductName())
                            )
                    );

                }

            }

        }

        if (!unavailable.isEmpty()) {

            return new ValidationResultImpl(
                    true,
                    unavailable
            );

        }

        return OK;
    }
}
