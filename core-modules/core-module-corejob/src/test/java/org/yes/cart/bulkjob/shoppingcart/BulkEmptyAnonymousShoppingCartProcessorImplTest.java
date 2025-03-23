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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * User: inspiresoftware
 * Date: 26/01/2021
 * Time: 12:05
 */
public class BulkEmptyAnonymousShoppingCartProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final ShoppingCartStateService shoppingCartStateService = ctx().getBean("shoppingCartStateService", ShoppingCartStateService.class);
        final CronJobProcessor bulkEmptyAnonymousShoppingCartProcessor = ctx().getBean("bulkEmptyAnonymousShoppingCartProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("bulkEmptyAnonymousShoppingCartProcessor",
                "process-batch-size=1\n" +
                       "empty-timeout-seconds=-100");

        // clear state
        bulkEmptyAnonymousShoppingCartProcessor.process(ctx);

        final ShoppingCartState cartEmptyAnonymous1State = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartEmptyAnonymous1State.setState("{}".getBytes());
        cartEmptyAnonymous1State.setShopId(10L);
        shoppingCartStateService.create(cartEmptyAnonymous1State);
        final ShoppingCartState cartEmptyAnonymous2State = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartEmptyAnonymous2State.setState("{}".getBytes());
        cartEmptyAnonymous2State.setShopId(10L);
        shoppingCartStateService.create(cartEmptyAnonymous2State);
        final ShoppingCartState cartFilledAnonymousState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartFilledAnonymousState.setState("{}".getBytes());
        cartFilledAnonymousState.setEmpty(false);
        cartFilledAnonymousState.setShopId(10L);
        shoppingCartStateService.create(cartFilledAnonymousState);
        final ShoppingCartState cartEmptyRegisteredState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        cartEmptyRegisteredState.setState("{}".getBytes());
        cartEmptyRegisteredState.setCustomerLogin("abc");
        cartEmptyRegisteredState.setShopId(10L);
        shoppingCartStateService.create(cartEmptyRegisteredState);

        bulkEmptyAnonymousShoppingCartProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) bulkEmptyAnonymousShoppingCartProcessor).getStatus(null);

        assertNotNull(status);
        assertTrue(status.getReport(), status.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Removed carts: 2, Removed temp orders: 0]"));

        assertNull(shoppingCartStateService.findById(cartEmptyAnonymous1State.getShoppingCartStateId()));
        assertNull(shoppingCartStateService.findById(cartEmptyAnonymous2State.getShoppingCartStateId()));
        assertNotNull(shoppingCartStateService.findById(cartFilledAnonymousState.getShoppingCartStateId()));
        assertNotNull(shoppingCartStateService.findById(cartEmptyRegisteredState.getShoppingCartStateId()));

    }
}