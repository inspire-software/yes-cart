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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderStateTransitionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 29/10/2019
 * Time: 15:32
 */
public class ManagedListsRemovalListenerImpl implements OrderStateTransitionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ManagedListsRemovalListenerImpl.class);

    private final CustomerWishListService customerWishListService;

    public ManagedListsRemovalListenerImpl(final CustomerWishListService customerWishListService) {
        this.customerWishListService = customerWishListService;
    }

    @Override
    public boolean onEvent(final OrderEvent orderEvent) {

        final CustomerOrder customerOrder = orderEvent.getCustomerOrder();

        if (customerOrder.getCustomer() != null && !customerOrder.getCustomer().isGuest()) {

            final Map<String, List<String>> wlToRemove = new HashMap<>();

            for (final CustomerOrderDet customerOrderDet : customerOrder.getOrderDetail()) {

                final Pair<String, I18NModel> managerSpaceList = customerOrderDet.getValue(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_MANAGED_LIST);
                if (managerSpaceList != null) {
                    final String[] managerList = StringUtils.split(managerSpaceList.getFirst(), " ", 2);
                    if (managerList != null && managerList.length == 2) {
                        final List<String> skus = wlToRemove.computeIfAbsent(managerList[1], key -> new ArrayList<>());
                        skus.add(customerOrderDet.getProductSkuCode());
                    }
                }

            }

            if (!wlToRemove.isEmpty()) {

                LOG.info("Recorded managed lists {} in order {} ... removing", wlToRemove.keySet(), customerOrder.getOrdernum());

                final List<CustomerWishList> all = customerWishListService.findWishListByCustomerId(customerOrder.getCustomer().getCustomerId());

                for (final CustomerWishList item : all) {

                    if (CustomerWishList.MANAGED_LIST_ITEM.equals(item.getWlType())
                            && wlToRemove.containsKey(item.getTag())
                            && wlToRemove.get(item.getTag()).contains(item.getSkuCode())) {

                        customerWishListService.delete(item);

                    }

                }

            }

        }

        return true;
    }

}
