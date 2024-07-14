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

package org.yes.cart.service.order.impl.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateTransitionListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: inspiresoftware
 * Date: 08/03/2024
 * Time: 11:19
 */
public class WishListPurchaseDateListenerImpl implements OrderStateTransitionListener {

    private static final Logger LOG = LoggerFactory.getLogger(WishListPurchaseDateListenerImpl.class);

    private final CustomerWishListService customerWishListService;

    public WishListPurchaseDateListenerImpl(final CustomerWishListService customerWishListService) {
        this.customerWishListService = customerWishListService;
    }

    @Override
    public boolean onEvent(final OrderEvent orderEvent) {

        final CustomerOrder customerOrder = orderEvent.getCustomerOrder();

        if (customerOrder.getCustomer() != null && !customerOrder.getCustomer().isGuest()) {

            final Set<Pair<String, String>> skusAndSuppliers = new HashSet<>();

            for (final CustomerOrderDet customerOrderDet : customerOrder.getOrderDetail()) {
                skusAndSuppliers.add(Pair.of(customerOrderDet.getProductSkuCode(), customerOrderDet.getSupplierCode()));
            }

            LOG.info("Processing wish list purchase dates for order {}", customerOrder.getOrdernum());

            final List<CustomerWishList> all = customerWishListService.findWishListByCustomerId(customerOrder.getCustomer().getCustomerId());

            for (final CustomerWishList item : all) {

                final Pair<String, String> skuAndSupplier = Pair.of(item.getSkuCode(), item.getSupplierCode());

                if (skusAndSuppliers.contains(skuAndSupplier)) {

                    LOG.info("Updating wish list {} purchase date to {} because of order {}", item.getCustomerwishlistId(), customerOrder.getOrderTimestamp(), customerOrder.getOrdernum());

                    item.setLastPurchaseDate(customerOrder.getOrderTimestamp().toLocalDate());
                    customerWishListService.update(item);

                }

            }

        }

        return true;
    }

}
