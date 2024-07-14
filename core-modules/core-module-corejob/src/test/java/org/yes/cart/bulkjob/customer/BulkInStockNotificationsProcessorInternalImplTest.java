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
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 12/03/2024
 * Time: 15:54
 */
public class BulkInStockNotificationsProcessorInternalImplTest  extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        final CustomerWishListService customerWishListService = (CustomerWishListService) ctx().getBean(ServiceSpringKeys.CUSTOMER_WISH_LIST_SERVICE);
        final CronJobProcessor bulkInStockNotificationsProcessor = ctx().getBean("bulkInStockNotificationsProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("bulkInStockNotificationsProcessor", "");

        // clear state
        bulkInStockNotificationsProcessor.process(ctx);

        final Customer customer = customerService.findById(10001L);
        assertNotNull(customer);

        final CustomerWishList wlSend = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wlSend.setCustomer(customer);
        wlSend.setQuantity(BigDecimal.ONE);
        wlSend.setSkuCode("BENDER-ua");
        wlSend.setSupplierCode("WAREHOUSE_2");
        wlSend.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wlSend.setVisibility(CustomerWishList.PRIVATE);
        wlSend.setRegularPriceWhenAdded(BigDecimal.ONE);
        wlSend.setRegularPriceCurrencyWhenAdded("EUR");
        customerWishListService.create(wlSend);

        final CustomerWishList wlOOS = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wlOOS.setCustomer(customer);
        wlOOS.setQuantity(BigDecimal.ONE);
        wlOOS.setSkuCode("BENDER-ua");
        wlOOS.setSupplierCode("S1");
        wlOOS.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wlOOS.setVisibility(CustomerWishList.PRIVATE);
        wlOOS.setRegularPriceWhenAdded(BigDecimal.ONE);
        wlOOS.setRegularPriceCurrencyWhenAdded("EUR");
        customerWishListService.create(wlOOS);

        final CustomerWishList wlNotEnough = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wlNotEnough.setCustomer(customer);
        wlNotEnough.setQuantity(new BigDecimal(10000));
        wlNotEnough.setSkuCode("BENDER-ua");
        wlNotEnough.setSupplierCode("WAREHOUSE_2");
        wlNotEnough.setWlType(CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE);
        wlNotEnough.setVisibility(CustomerWishList.PRIVATE);
        wlNotEnough.setRegularPriceWhenAdded(BigDecimal.ONE);
        wlNotEnough.setRegularPriceCurrencyWhenAdded("EUR");
        customerWishListService.create(wlNotEnough);

        bulkInStockNotificationsProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) bulkInStockNotificationsProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Sent notifications: 1, Skipped notifications: 2]"));

        assertNull(customerWishListService.findById(wlSend.getCustomerwishlistId()));
        assertNotNull(customerWishListService.findById(wlOOS.getCustomerwishlistId()));
        assertNotNull(customerWishListService.findById(wlNotEnough.getCustomerwishlistId()));

        customerWishListService.delete(wlOOS);
        customerWishListService.delete(wlNotEnough);


    }
}