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

package org.yes.cart.service.order;

import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 08:33
 */
public interface OrderSplittingStrategy {

    /**
     * Determine delivery buckets for given item at current point in time.
     *
     * @param item   item to determine buckets for
     * @param cart   cart with items for which buckets are calculated (item has to be one of the cart items)
     *
     * @return delivery group marker
     */
    DeliveryBucket determineDeliveryBucket(CartItem item,
                                           ShoppingCart cart);


    /**
     * Determine delivery buckets for given items at current point in time.
     *
     * @param shopId              shop PK
     * @param items               items to determine buckets for
     * @param onePhysicalDelivery true if need to create one physical delivery (per supplier).
     *
     * @return cart items by delivery bucket
     */
    Map<DeliveryBucket, List<CartItem>> determineDeliveryBuckets(long shopId,
                                                                 Collection<CartItem> items,
                                                                 Map<String, Boolean> onePhysicalDelivery);

    /**
     * Can order have multiple deliveries by supplier.
     *
     * @param shopId              shop PK
     * @param items               items to determine buckets for
     *
     * @return true in case if order can have multiple physical deliveries by supplier.
     */
    Map<String, Boolean> isMultipleDeliveriesAllowed(long shopId,
                                                     Collection<CartItem> items);

}
