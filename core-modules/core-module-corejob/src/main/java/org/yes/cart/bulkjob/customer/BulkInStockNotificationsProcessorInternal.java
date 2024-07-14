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

package org.yes.cart.bulkjob.customer;

import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;

import java.util.Collection;
import java.util.List;

/**
 * User: inspiresoftware
 * Date: 09/03/2024
 * Time: 15:08
 */
public interface BulkInStockNotificationsProcessorInternal {

    /**
     * Create an email notification for "back in stock" products
     *
     * @param shop     shop in which the in stock items where checked
     * @param customer customer to notify
     * @param items    items that are back in stock
     */
    void createNotificationEmail(Shop shop, Customer customer, List<Pair<CustomerWishList, ProductSku>> items);

    /**
     * Remove notifications objects that are no longer needed.
     *
     * @param items    notifications to remove
     */
    void removeSentNotifications(Collection<CustomerWishList> items);

}
