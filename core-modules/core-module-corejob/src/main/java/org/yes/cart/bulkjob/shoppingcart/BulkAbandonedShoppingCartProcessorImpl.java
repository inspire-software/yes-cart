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

package org.yes.cart.bulkjob.shoppingcart;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.time.Instant;
import java.util.*;

/**
 * Processor that allows to clean up abandoned shopping cart, so that we do not accumulate
 * junk data.
 *
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 12:47
 */
public class BulkAbandonedShoppingCartProcessorImpl extends AbstractCronJobProcessorImpl
        implements BulkShoppingCartRemoveProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkAbandonedShoppingCartProcessorImpl.class);

    private static final String REMOVED_CARTS_COUNTER = "Removed carts";
    private static final String REMOVED_ORDERS_COUNTER = "Removed temp orders";

    private static final long ABANDONED_SECONDS_DEFAULT = 30 * 24 * 60 * 60; // 30days

    private ShopService shopService;
    private ShoppingCartStateService shoppingCartStateService;
    private CustomerOrderService customerOrderService;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        final Properties properties = readContextAsProperties(context, job, definition);

        final int batchSize = NumberUtils.toInt(properties.getProperty("process-batch-size"), 500);
        final long abandonedDefaultSeconds = NumberUtils.toLong(properties.getProperty("abandoned-timeout-seconds"), ABANDONED_SECONDS_DEFAULT);

        listener.notifyInfo("Staring bulk abandon cart clean, with batch {} and timeout {}s", batchSize, abandonedDefaultSeconds);

        List<ShoppingCartState> batch;

        for (final Shop shop : shopService.getNonSubShops()) {

            final long offsetSec = NumberUtils.toLong(properties.getProperty("abandoned-timeout-seconds-" + shop.getCode()), abandonedDefaultSeconds);
            final Instant changeToKeep = Instant.now().plusSeconds(-offsetSec);

            final Map<String, List> filter = new HashMap<>();
            filter.put("shopId", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(shop.getShopId())));
            filter.put("updatedTimestamp", Collections.singletonList(SearchContext.MatchMode.LE.toParam(changeToKeep)));

            do {
                batch = shoppingCartStateService.findShoppingCartStates(
                        0,
                        batchSize,
                        "shoppingCartStateId",
                        false,
                        filter);
                // Remove batch
                listener.count(REMOVED_ORDERS_COUNTER, self().removeCarts(batch));
                listener.count(REMOVED_CARTS_COUNTER, batch.size());

                listener.notifyPing("Examining carts in {}, removed carts {}, removed orders {}",
                        shop.getCode(),
                        listener.getCount(REMOVED_CARTS_COUNTER),
                        listener.getCount(REMOVED_ORDERS_COUNTER)
                );

            } while (!batch.isEmpty());
        }

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }


    @Override
    public int removeCarts(final List<ShoppingCartState> carts) {

        int removedOrders = 0;
        for (final ShoppingCartState state : carts) {

            final String guid = state.getGuid();

            LOG.debug("Removing abandoned cart for {}, guid {}, last modified {}", state.getCustomerLogin(), guid, state.getUpdatedTimestamp());
            this.shoppingCartStateService.delete(state);
            LOG.debug("Removed abandoned cart for {}, guid {}", state.getCustomerLogin(), guid);

            final CustomerOrder tempOrder = this.customerOrderService.findByReference(guid);
            if (tempOrder != null && CustomerOrder.ORDER_STATUS_NONE.equals(tempOrder.getOrderStatus())) {
                LOG.debug("Removing temporary order for cart guid {}", guid);
                this.customerOrderService.delete(tempOrder);
                removedOrders++;
                LOG.debug("Removed temporary order for cart guid {}", guid);
            }

        }

        return removedOrders;

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


    /**
     * Spring IoC.
     *
     * @param shopService service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param shoppingCartStateService service
     */
    public void setShoppingCartStateService(final ShoppingCartStateService shoppingCartStateService) {
        this.shoppingCartStateService = shoppingCartStateService;
    }

    /**
     * Spring IoC.
     *
     * @param customerOrderService service
     */
    public void setCustomerOrderService(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }
}
