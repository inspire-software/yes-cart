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

import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.service.dto.DtoCustomerOrderService;
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
public class ShopFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;

    public ShopFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy) {
        this.shopFederationStrategy = shopFederationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public void applyFederationFilter(final Collection list, final Class objectType) {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();

        final Iterator<ShopDTO> shopIt = list.iterator();
        while (shopIt.hasNext()) {
            final ShopDTO shop = shopIt.next();
            if (!manageableShopIds.contains(shop.getShopId())) {
                shopIt.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isManageable(final Object object, final Class objectType) {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        return manageableShopIds.contains(object);
    }

}
