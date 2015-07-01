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

package org.yes.cart.bulkjob.order;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 20/05/2015
 * Time: 15:02
 */
public class BulkAwaitingInventoryDeliveriesProcessorImplUnitTest {


    @Test
    public void testSkuBulking() throws Exception {

        final BulkAwaitingInventoryDeliveriesProcessorImpl p = new BulkAwaitingInventoryDeliveriesProcessorImpl(null, null, null, null, null);

        final List<String> original = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));

        int size = 3;
        int start = 0;
        List<String> batch;

        batch = p.buildSkuBatch(original, start, size);

        assertEquals(3, batch.size());
        assertEquals("1", batch.get(0));
        assertEquals("2", batch.get(1));
        assertEquals("3", batch.get(2));

        start += size;
        batch = p.buildSkuBatch(original, start, size);

        assertEquals(3, batch.size());
        assertEquals("4", batch.get(0));
        assertEquals("5", batch.get(1));
        assertEquals("6", batch.get(2));

        start += size;
        batch = p.buildSkuBatch(original, start, size);

        assertEquals(3, batch.size());
        assertEquals("7", batch.get(0));
        assertEquals("8", batch.get(1));
        assertEquals("9", batch.get(2));

        start += size;
        batch = p.buildSkuBatch(original, start, size);

        assertEquals(1, batch.size());
        assertEquals("10", batch.get(0));

    }
}
