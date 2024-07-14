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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.ShopService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * User: inspiresoftware
 * Date: 09/03/2024
 * Time: 15:15
 */
public class BulkExpiredInStockNotificationsProcessorInternalImpl extends AbstractCronJobProcessorImpl
        implements BulkExpiredInStockNotificationsProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExpiredInStockNotificationsProcessorInternalImpl.class);

    private static final String REMOVED_COUNTER = "Removed notifications";

    private static final long EXPIRE_SECONDS_DEFAULT = 90 * 24 * 60 * 60; // 90 days

    private ShopService shopService;
    private CustomerWishListService customerWishListService;

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
        final long defaultSeconds = NumberUtils.toLong(properties.getProperty("notifications-timeout-seconds"), EXPIRE_SECONDS_DEFAULT);

        final List<CustomerWishList> batch = new ArrayList<>(batchSize);

        this.customerWishListService.findByCriteriaIterator(
                " where e.wlType = ?1",
                new Object[] { CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE },
                wishList -> {

                    boolean remove = CollectionUtils.isEmpty(wishList.getCustomer().getShops());
                    if (!remove) {
                        final Shop shop = shopService.getById(wishList.getCustomer().getShops().iterator().next().getShop().getShopId());
                        remove = shop == null;
                        if (!remove) {
                            final long offsetMs = NumberUtils.toLong(properties.getProperty("notifications-timeout-seconds-" + shop.getCode()), defaultSeconds) * 1000L;
                            final Instant changeToKeep = Instant.now().plusMillis(-offsetMs);
                            remove = wishList.getCreatedTimestamp().isBefore(changeToKeep);
                        }
                    }

                    if (remove) {

                        int count;
                        if (batch.size() + 1 > batchSize) {
                            // Remove batch
                            self().removeNotifications(batch);
                            count = listener.count(REMOVED_COUNTER, batch.size());
                            batch.clear();
                            // release memory from HS
                            customerWishListService.getGenericDao().clear();
                        } else {
                            count = listener.getCount(REMOVED_COUNTER);
                        }
                        batch.add(wishList);

                        if (count % batchSize == 0) { // minify string concatenation
                            listener.notifyPing("Removed notifications: " + count);
                        }
                    }

                    return true; // read fully
                }
        );

        if (batch.size() > 0) {
            // Remove last batch
            self().removeNotifications(batch);
            listener.count(REMOVED_COUNTER, batch.size());
        }

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }

    @Override
    public void removeNotifications(final List<CustomerWishList> items) {

        for (final CustomerWishList item : items) {
            final String guid = item.getGuid();
            LOG.debug("Removing expired notification for {}, item: {}/{}, guid {}",
                    item.getCustomer().getCustomerId(), item.getSkuCode(),
                    item.getSupplierCode(), guid);
            final CustomerWishList wlItem = this.customerWishListService.findById(item.getCustomerwishlistId());
            if (wlItem != null) {
                this.customerWishListService.delete(wlItem);
            }
            LOG.debug("Removed expired notification for {}, item: {}/{}, guid {}",
                    item.getCustomer().getCustomerId(), item.getSkuCode(),
                    item.getSupplierCode(), guid);
        }

    }

    private BulkExpiredInStockNotificationsProcessorInternal self;

    private BulkExpiredInStockNotificationsProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public BulkExpiredInStockNotificationsProcessorInternal getSelf() {
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
     * @param customerWishListService service
     */
    public void setCustomerWishListService(final CustomerWishListService customerWishListService) {
        this.customerWishListService = customerWishListService;
    }

}
