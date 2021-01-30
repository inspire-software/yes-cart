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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 26/01/2021
 * Time: 12:33
 */
public class BulkAbandonedShoppingCartProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = ctx().getBean("shoppingCartStateService", ShoppingCartStateService.class);
        final CronJobProcessor bulkAbandonedShoppingCartProcessor = ctx().getBean("bulkAbandonedShoppingCartProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("bulkAbandonedShoppingCartProcessor", "abandoned-timeout-seconds=-100");

        // clear state
        bulkAbandonedShoppingCartProcessor.process(ctx);

        final ShoppingCartState cartEmptyAnonymousState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartEmptyAnonymousState.setState("{}".getBytes());
        shoppingCartStateService.create(cartEmptyAnonymousState);
        final ShoppingCartState cartFilledAnonymousState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartFilledAnonymousState.setState("{}".getBytes());
        cartFilledAnonymousState.setEmpty(false);
        shoppingCartStateService.create(cartFilledAnonymousState);
        final ShoppingCartState cartEmptyRegisteredState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartEmptyRegisteredState.setState("{}".getBytes());
        cartEmptyRegisteredState.setCustomerLogin("abc");
        shoppingCartStateService.create(cartEmptyRegisteredState);
        final ShoppingCartState cartFilledRegisteredState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartFilledRegisteredState.setState("{}".getBytes());
        cartFilledRegisteredState.setCustomerLogin("abc");
        cartFilledRegisteredState.setEmpty(false);
        shoppingCartStateService.create(cartFilledRegisteredState);

        bulkAbandonedShoppingCartProcessor.process(ctx);
        final JobStatus status = ((JobStatusAware) bulkAbandonedShoppingCartProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Removed carts: 4, Removed temp orders: 0]"));

        assertNull(shoppingCartStateService.findById(cartEmptyAnonymousState.getShoppingCartStateId()));
        assertNull(shoppingCartStateService.findById(cartFilledAnonymousState.getShoppingCartStateId()));
        assertNull(shoppingCartStateService.findById(cartEmptyRegisteredState.getShoppingCartStateId()));
        assertNull(shoppingCartStateService.findById(cartFilledRegisteredState.getShoppingCartStateId()));

    }
}