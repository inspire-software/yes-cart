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

package org.yes.cart.service.federation.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class CustomerOrderFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final DtoCustomerOrderService customerOrderService;

    public CustomerOrderFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                             final DtoCustomerOrderService customerOrderService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.customerOrderService = customerOrderService;
    }

    /**
     * {@inheritDoc}
     */
    public void applyFederationFilter(final Collection list, final Class objectType) {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Iterator<CustomerOrderDTO> customerOrdersIt = list.iterator();
        while (customerOrdersIt.hasNext()) {
            final CustomerOrderDTO customerOrder = customerOrdersIt.next();
            if (!manageableShopIds.contains(customerOrder.getShopId())) {
                customerOrdersIt.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isManageable(final Object object, final Class objectType) {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        if (object instanceof String) {
            try {
                final List<CustomerOrderDTO> orders = customerOrderService.findCustomerOrdersByCriteria(0L, null, null, null, null, null, null, (String) object);
                return orders != null && orders.size() == 1 && manageableShopIds.contains(orders.get(0).getShopId());
            } catch (Exception exp) {
                return false;
            }
        }
        try {
            final CustomerOrderDTO order = customerOrderService.getById((Long) object);
            return order != null && manageableShopIds.contains(order.getShopId());
        } catch (Exception exp) {
            return false;
        }
    }

}
