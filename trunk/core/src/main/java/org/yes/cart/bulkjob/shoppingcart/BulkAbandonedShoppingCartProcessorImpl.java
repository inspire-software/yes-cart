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

package org.yes.cart.bulkjob.shoppingcart;

import org.slf4j.Logger;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.util.ShopCodeContext;

import java.util.Date;
import java.util.List;

/**
 * Processor that allows to clean up abandoned shopping cart, so that we do not accumulate
 * junk data.
 *
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 12:47
 */
public class BulkAbandonedShoppingCartProcessorImpl implements Runnable {

    private static final long MS_IN_DAY = 86400000L;

    private final ShoppingCartStateService shoppingCartStateService;
    private int abandonedTimeoutDays = 30;

    public BulkAbandonedShoppingCartProcessorImpl(final ShoppingCartStateService shoppingCartStateService) {
        this.shoppingCartStateService = shoppingCartStateService;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final Date lastModification =
                new Date(System.currentTimeMillis() - abandonedTimeoutDays * MS_IN_DAY);

        final List<ShoppingCartState> abandoned = this.shoppingCartStateService.findByModificationPrior(lastModification);

        log.info("Look up all ShoppingCartStates not modified since {} ... found {}", lastModification, abandoned.size());

        for (final ShoppingCartState scs : abandoned) {

            log.info("Removed abandoned basket for {}, guid {}", scs.getCustomerEmail(), scs.getGuid());
            this.shoppingCartStateService.delete(scs);

        }

    }

    /**
     * Set number of days after which the cart is considered to be abandoned.
     *
     * @param abandonedTimeoutDays number of days
     */
    public void setAbandonedTimeoutDays(final int abandonedTimeoutDays) {
        this.abandonedTimeoutDays = abandonedTimeoutDays;
    }
}
