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
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.SystemService;

import java.util.Date;

/**
 * Bulk processor to remove guest checkout accounts.
 *
 * User: denispavlov
 * Date: 10/02/2016
 * Time: 17:56
 */
public class BulkExpiredGuestsProcessorImpl implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExpiredGuestsProcessorImpl.class);

    private static final long MS_IN_DAY = 86400000L;

    private final CustomerService customerService;
    private final SystemService systemService;
    private long expiredTimeoutMs = 1 * MS_IN_DAY;
    private int batchSize = 20;

    public BulkExpiredGuestsProcessorImpl(final CustomerService customerService,
                                          final SystemService systemService) {
        this.customerService = customerService;
        this.systemService = systemService;
    }


    /** {@inheritDoc} */
    @Override
    public void run() {

        final Date lastModification =
                new Date(System.currentTimeMillis() - determineExpiryInMs());

        LOG.info("Look up all Guest accounts created before {}", lastModification);

        final ResultsIterator<Customer> expired = this.customerService.findGuestsBefore(lastModification);

        try {
            int count = 0;
            while (expired.hasNext()) {

                final Customer guest = expired.next();

                final String guid = guest.getGuid();

                LOG.debug("Removing expired guest {}, guid {}", guest.getGuestEmail(), guid);
                this.customerService.delete(guest);
                LOG.debug("Removed expired guest {}, guid {}", guest.getGuestEmail(), guid);

                if (++count % this.batchSize == 0 ) {
                    //flush a batch of updates and release memory:
                    customerService.getGenericDao().flush();
                    customerService.getGenericDao().clear();
                }

            }

            LOG.info("Removed {} guest account(s)", count);

        } finally {
            try {
                expired.close();
            } catch (Exception exp) {
                LOG.error("Processing expired guest accounts exception, error closing iterator: " + exp.getMessage(), exp);
            }
        }

        LOG.info("Processing expired guest ... completed");

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

}
