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

package org.yes.cart.bulkjob.shoppingcart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.service.domain.SystemService;
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
    private final CustomerOrderService customerOrderService;
    private final SystemService systemService;
    private long abandonedTimeoutMs = 30;
    private int batchSize = 20;

    public BulkAbandonedShoppingCartProcessorImpl(final ShoppingCartStateService shoppingCartStateService,
                                                  final CustomerOrderService customerOrderService,
                                                  final SystemService systemService) {
        this.shoppingCartStateService = shoppingCartStateService;
        this.customerOrderService = customerOrderService;
        this.systemService = systemService;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        final Logger log = ShopCodeContext.getLog(this);

        final long start = System.currentTimeMillis();

        final Date lastModification =
                new Date(System.currentTimeMillis() - determineExpiryInMs());

        log.info("Look up all ShoppingCartStates not modified since {}", lastModification);

        final ResultsIterator<ShoppingCartState> abandoned = this.shoppingCartStateService.findByModificationPrior(lastModification);

        try {
            int count = 0;
            int removedOrders = 0;
            while (abandoned.hasNext()) {

                final ShoppingCartState scs = abandoned.next();

                final String guid = scs.getGuid();

                log.debug("Removing abandoned cart for {}, guid {}", scs.getCustomerEmail(), guid);
                this.shoppingCartStateService.delete(scs);
                log.debug("Removed abandoned cart for {}, guid {}", scs.getCustomerEmail(), guid);

                final CustomerOrder tempOrder = this.customerOrderService.findByGuid(guid);
                if (CustomerOrder.ORDER_STATUS_NONE.equals(tempOrder.getOrderStatus())) {
                    log.debug("Removing temporary order for cart guid {}", guid);
                    this.customerOrderService.delete(tempOrder);
                    removedOrders++;
                    log.debug("Removed temporary order for cart guid {}", guid);
                }

                if (++count % this.batchSize == 0 ) {
                    //flush a batch of updates and release memory:
                    shoppingCartStateService.getGenericDao().flush();
                    shoppingCartStateService.getGenericDao().clear();
                }

            }

            log.info("Removed {} carts and {} temporary orders", count, removedOrders);

        } finally {
            try {
                abandoned.close();
            } catch (Exception exp) {
                log.error("Processing abandoned baskets exception, error closing iterator: " + exp.getMessage(), exp);
            }
        }

        final long finish = System.currentTimeMillis();

        final long ms = (finish - start);

        log.info("Processing abandoned baskets ... completed in {}s", (ms > 0 ? ms / 1000 : 0));

    }


    private long determineExpiryInMs() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.CART_ABANDONED_TIMEOUT_SECONDS);

        if (av != null && StringUtils.isNotBlank(av)) {
            long expiry = NumberUtils.toInt(av) * 1000L;
            if (expiry > 0) {
                return expiry;
            }
        }
        return this.abandonedTimeoutMs;

    }


    /**
     * Set number of days after which the cart is considered to be abandoned.
     *
     * @param abandonedTimeoutDays number of days
     */
    public void setAbandonedTimeoutDays(final int abandonedTimeoutDays) {
        this.abandonedTimeoutMs = abandonedTimeoutDays * MS_IN_DAY;
    }


    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

}
