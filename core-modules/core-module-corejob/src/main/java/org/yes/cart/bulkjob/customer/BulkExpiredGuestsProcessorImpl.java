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
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Bulk processor to remove guest checkout accounts.
 *
 * User: denispavlov
 * Date: 10/02/2016
 * Time: 17:56
 */
public class BulkExpiredGuestsProcessorImpl extends AbstractCronJobProcessorImpl
        implements BulkExpiredGuestsProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExpiredGuestsProcessorImpl.class);

    private static final String REMOVED_COUNTER = "Removed guest accounts";

    private static final long GUEST_SECONDS_DEFAULT = 24 * 60 * 60; // 1day

    private ShopService shopService;
    private CustomerService customerService;

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
        final long emptyDefaultSeconds = NumberUtils.toLong(properties.getProperty("guest-timeout-seconds"), GUEST_SECONDS_DEFAULT);

        final List<Customer> batch = new ArrayList<>(batchSize);

        this.customerService.findByCriteriaIterator(
                " where e.guest = ?1",
                new Object[] { Boolean.TRUE },
                guest -> {

                    boolean remove = CollectionUtils.isEmpty(guest.getShops());
                    if (!remove) {
                        final Shop shop = shopService.getById(guest.getShops().iterator().next().getShop().getShopId());
                        remove = shop == null;
                        if (!remove) {
                            final long offsetMs = NumberUtils.toLong(properties.getProperty("guest-timeout-seconds-" + shop.getCode()), emptyDefaultSeconds) * 1000L;
                            final Instant changeToKeep = Instant.now().plusMillis(-offsetMs);
                            remove = guest.getCreatedTimestamp().isBefore(changeToKeep);
                        }
                    }

                    if (remove) {

                        int count;
                        if (batch.size() + 1 > batchSize) {
                            // Remove batch
                            self().removeGuests(batch);
                            count = listener.count(REMOVED_COUNTER, batch.size());
                            batch.clear();
                            // release memory from HS
                            customerService.getGenericDao().clear();
                        } else {
                            count = listener.getCount(REMOVED_COUNTER);
                        }
                        batch.add(guest);

                        if (count % batchSize == 0) { // minify string concatenation
                            listener.notifyPing("Removed guest accounts: " + count);
                        }
                    }

                    return true; // read fully
                }
        );

        if (batch.size() > 0) {
            // Remove last batch
            self().removeGuests(batch);
            listener.count(REMOVED_COUNTER, batch.size());
        }

        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }

    @Override
    public void removeGuests(final List<Customer> guests) {

        for (final Customer guest : guests) {
            final String guid = guest.getGuid();
            LOG.debug("Removing expired guest {}/{}, guid {}", guest.getLogin(), guest.getEmail(), guid);
            final Customer customer = this.customerService.findById(guest.getCustomerId());
            if (customer != null) {
                this.customerService.delete(customer);
            }
            LOG.debug("Removed expired guest {}/{}, guid {}", guest.getLogin(), guest.getEmail(), guid);
        }

    }

    private BulkExpiredGuestsProcessorInternal self;

    private BulkExpiredGuestsProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public BulkExpiredGuestsProcessorInternal getSelf() {
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
     * @param customerService service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

}
