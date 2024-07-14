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
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 10/03/2024
 * Time: 14:11
 */
public class BulkExpiredInStockNotificationsProcessorInternalImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        final CustomerWishListService customerWishListService = (CustomerWishListService) ctx().getBean(ServiceSpringKeys.CUSTOMER_WISH_LIST_SERVICE);
        final CronJobProcessor bulkExpiredInStockNotificationsProcessor = ctx().getBean("bulkExpiredInStockNotificationsProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("bulkExpiredInStockNotificationsProcessor", "notifications-timeout-seconds=" + (24 * 60 * 60));

        // clear state
        bulkExpiredInStockNotificationsProcessor.process(ctx);

        final Customer customer = customerService.findById(10001L);
        assertNotNull(customer);

        final CustomerWishList wl1d1s = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wl1d1s.setCustomer(customer);
        wl1d1s.setQuantity(BigDecimal.ONE);
        wl1d1s.setSkuCode("TEST1s1s");
        wl1d1s.setSupplierCode("S1");
        wl1d1s.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wl1d1s.setVisibility(CustomerWishList.PRIVATE);
        wl1d1s.setRegularPriceWhenAdded(BigDecimal.ONE);
        wl1d1s.setRegularPriceCurrencyWhenAdded("USD");
        customerWishListService.create(wl1d1s);
        wl1d1s.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS).plus(-1, ChronoUnit.SECONDS));
        customerWishListService.update(wl1d1s);

        final CustomerWishList wl1d = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wl1d.setCustomer(customer);
        wl1d.setQuantity(BigDecimal.ONE);
        wl1d.setSkuCode("TEST1s");
        wl1d.setSupplierCode("S1");
        wl1d.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wl1d.setVisibility(CustomerWishList.PRIVATE);
        wl1d.setRegularPriceWhenAdded(BigDecimal.ONE);
        wl1d.setRegularPriceCurrencyWhenAdded("USD");
        customerWishListService.create(wl1d);
        wl1d.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS));
        customerWishListService.update(wl1d);

        final CustomerWishList wl23h59m59s = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wl23h59m59s.setCustomer(customer);
        wl23h59m59s.setQuantity(BigDecimal.ONE);
        wl23h59m59s.setSkuCode("TEST23h59m59s");
        wl23h59m59s.setSupplierCode("S1");
        wl23h59m59s.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wl23h59m59s.setVisibility(CustomerWishList.PRIVATE);
        wl23h59m59s.setRegularPriceWhenAdded(BigDecimal.ONE);
        wl23h59m59s.setRegularPriceCurrencyWhenAdded("USD");
        customerWishListService.create(wl23h59m59s);
        wl23h59m59s.setCreatedTimestamp(Instant.now().plus(-1, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));
        customerWishListService.update(wl23h59m59s);

        bulkExpiredInStockNotificationsProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) bulkExpiredInStockNotificationsProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Removed notifications: 2]"));

        assertNull(customerWishListService.findById(wl1d1s.getCustomerwishlistId()));
        assertNull(customerWishListService.findById(wl1d.getCustomerwishlistId()));
        assertNotNull(customerWishListService.findById(wl23h59m59s.getCustomerwishlistId()));

        customerWishListService.delete(wl23h59m59s);


    }
}