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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * User: inspiresoftware
 * Date: 26/01/2021
 * Time: 11:48
 */
public class BulkExpiredGuestsProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        final CronJobProcessor bulkExpiredGuestsProcessor = ctx().getBean("bulkExpiredGuestsProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("bulkExpiredGuestsProcessor", "guest-timeout-seconds=" + (24 * 60 * 60));

        // clear state
        bulkExpiredGuestsProcessor.process(ctx);

        final Customer customer1d1s = createCustomerB2G("Guest1d1s");
        customer1d1s.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS).plus(-1, ChronoUnit.SECONDS));
        customerService.update(customer1d1s);

        final Customer customer1d = createCustomerB2G("Guest1d");
        customer1d.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS));
        customerService.update(customer1d);

        final Customer customer23h59m59s = createCustomerB2G("Guest23h59m59s");
        customer23h59m59s.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));
        customerService.update(customer23h59m59s);

        bulkExpiredGuestsProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) bulkExpiredGuestsProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Removed guest accounts: 2]"));

        assertNull(customerService.findById(customer1d1s.getCustomerId()));
        assertNull(customerService.findById(customer1d.getCustomerId()));
        assertNotNull(customerService.findById(customer23h59m59s.getCustomerId()));


    }
}