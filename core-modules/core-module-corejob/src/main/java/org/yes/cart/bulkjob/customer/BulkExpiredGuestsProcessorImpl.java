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

package org.yes.cart.bulkjob.customer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerLoggerWrapperImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.SystemService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Bulk processor to remove guest checkout accounts.
 *
 * User: denispavlov
 * Date: 10/02/2016
 * Time: 17:56
 */
public class BulkExpiredGuestsProcessorImpl implements BulkExpiredGuestsProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExpiredGuestsProcessorImpl.class);

    private static final long MS_IN_DAY = 86400000L;

    private final CustomerService customerService;
    private final SystemService systemService;
    private long expiredTimeoutMs = MS_IN_DAY;
    private int batchSize = 500;

    private final JobStatusListener listener = new JobStatusListenerLoggerWrapperImpl(LOG);

    public BulkExpiredGuestsProcessorImpl(final CustomerService customerService,
                                          final SystemService systemService) {
        this.customerService = customerService;
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

        LOG.info("Look up all Guest accounts created before {}", lastModification);

        final int batchSize = determineBatchSize();
        final List<Customer> batch = new ArrayList<>(batchSize);

        final int count[] = new int[] { 0 };

        this.customerService.findByCriteriaIterator(
                " where e.guest = ?1 and e.createdTimestamp < ?2",
                new Object[] { Boolean.TRUE, lastModification },
                guest -> {
                    if (batch.size() + 1 > batchSize) {
                        // Remove batch
                        self().removeGuests(batch);
                        count[0] += batch.size();
                        batch.clear();
                        // release memory from HS
                        customerService.getGenericDao().clear();
                    }
                    batch.add(guest);

                    if (count[0] % batchSize == 0) { // minify string concatenation
                        listener.notifyPing("Removed guest accounts: " + count[0]);
                    }

                    return true; // read fully
                }
        );

        if (batch.size() > 0) {
            // Remove last batch
            self().removeGuests(batch);
            count[0] += batch.size();
        }

        LOG.info("Removed {} guest account(s)", count[0]);
        listener.notifyPing("Removed " + count[0] + " guest account(s) in last run");

        LOG.info("Processing expired guest ... completed");

    }

    @Override
    public void removeGuests(final List<Customer> guests) {

        for (final Customer guest : guests) {
            final String guid = guest.getGuid();
            LOG.debug("Removing expired guest {}, guid {}", guest.getGuestEmail(), guid);
            this.customerService.delete(guest);
            LOG.debug("Removed expired guest {}, guid {}", guest.getGuestEmail(), guid);
        }

    }

    private int determineBatchSize() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.JOB_EXPIRE_GUESTS_BATCH_SIZE);

        if (av != null && StringUtils.isNotBlank(av)) {
            int batch = NumberUtils.toInt(av);
            if (batch > 0) {
                return batch;
            }
        }
        return this.batchSize;

    }

    private long determineExpiryInMs() {

        final String av = systemService.getAttributeValue(AttributeNamesKeys.System.GUESTS_EXPIRY_TIMEOUT_SECONDS);

        if (av != null && StringUtils.isNotBlank(av)) {
            long expiry = NumberUtils.toInt(av) * 1000L;
            if (expiry > 0) {
                return expiry;
            }
        }
        return this.expiredTimeoutMs;

    }


    /**
     * Set number of days after which the cart is considered to be abandoned.
     *
     * @param abandonedTimeoutDays number of days
     */
    public void setExpiredTimeoutDays(final int abandonedTimeoutDays) {
        this.expiredTimeoutMs = abandonedTimeoutDays * MS_IN_DAY;
    }


    /**
     * Batch size for remote index update.
     *
     * @param batchSize batch size
     */
    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
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


}
