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
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.service.domain.SystemService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Processor that allows to clean up abandoned shopping cart, so that we do not accumulate
 * junk data.
 *
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 12:47
 */
public class BulkEmptyAnonymousShoppingCartProcessorImpl implements BulkShoppingCartRemoveProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkEmptyAnonymousShoppingCartProcessorImpl.class);

    private static final long MS_IN_DAY = 86400000L;

    private final ShoppingCartStateService shoppingCartStateService;
    private final CustomerOrderService customerOrderService;
    private final SystemService systemService;
    private long abandonedTimeoutMs = MS_IN_DAY;
    private int batchSize = 500;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG);

    public BulkEmptyAnonymousShoppingCartProcessorImpl(final ShoppingCartStateService shoppingCartStateService,
                                                       final CustomerOrderService customerOrderService,
                                                       final SystemService systemService) {
        this.shoppingCartStateService = shoppingCartStateService;
        this.customerOrderService = customerOrderService;
        this.systemService = systemService;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public void run() {

        final Instant lastModification = Instant.now().plusMillis(-determineExpiryInMs());

        LOG.info("Look up all ShoppingCartStates not modified since {}", lastModification);

        final int count[] = new int[] { 0 };
        final int removedOrders[] = new int[] { 0 };

        final int batchSize = determineBatchSize();
        final List<ShoppingCartState> batch = new ArrayList<>();

        this.shoppingCartStateService.findByCriteriaIterator(
                " where e.empty = ?2 AND e.customerEmail IS NULL AND (e.updatedTimestamp < ?1 OR e.updatedTimestamp IS NULL)",
                new Object[] { lastModification, Boolean.TRUE },
                cart -> {

                    if (batch.size() + 1 >= batchSize) {
                        // Remove batch
                        removedOrders[0] += self().removeCarts(batch);
                        count[0] += batch.size();
                        batch.clear();
                        // release memory from HS
                        shoppingCartStateService.getGenericDao().clear();
                    }
                    batch.add(cart);

                    return true; // all
                }
        );

        if (batch.size() > 0) {
            // Remove last batch
            removedOrders[0] += self().removeCarts(batch);
            count[0] += batch.size();
        }

        LOG.info("Removed {} empty carts and {} temporary orders", count, removedOrders);
        listener.notifyPing("Removed " + count[0] + " empty carts and " + removedOrders[0] + " temporary orders in last run");

        LOG.info("Processing empty baskets ... completed");

    }


    @Override
    public int removeCarts(final List<ShoppingCartState> carts) {

        int removedOrders = 0;
        for (final ShoppingCartState state : carts) {

            final String guid = state.getGuid();

            LOG.debug("Removing empty cart for {}, guid {}", state.getCustomerEmail(), guid);
            this.shoppingCartStateService.delete(state);
            LOG.debug("Removed empty cart for {}, guid {}", state.getCustomerEmail(), guid);

            final CustomerOrder tempOrder = this.customerOrderService.findByReference(guid);
            if (tempOrder != null && CustomerOrder.ORDER_STATUS_NONE.equals(tempOrder.getOrderStatus())) {
                LOG.debug("Removing temporary order for empty cart guid {}", guid);
                this.customerOrderService.delete(tempOrder);
                removedOrders++;
                LOG.debug("Removed temporary order for empty cart guid {}", guid);
            }

        }

        return removedOrders;

    }

    private int determineBatchSize() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.JOB_EMPTY_CARTS_BATCH_SIZE);

        if (av != null && StringUtils.isNotBlank(av)) {
            int batch = NumberUtils.toInt(av);
            if (batch > 0) {
                return batch;
            }
        }
        return this.batchSize;

    }

    private long determineExpiryInMs() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS);

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



    private BulkShoppingCartRemoveProcessorInternal self;

    private BulkShoppingCartRemoveProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public BulkShoppingCartRemoveProcessorInternal getSelf() {
        return null;
    }


}
