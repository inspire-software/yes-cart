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

package org.yes.cart.search.dao.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 13/09/2019
 * Time: 19:21
 */
public class LuceneDocumentAdapterUtilsTest {

    @Test
    public void determineBoots() throws Exception {

        // Category product ranking, mid point is 500 anything lower is higher rank, higher lower rank with step of 1000
        assertEquals(0.25d, LuceneDocumentAdapterUtils.determineBoots(10000, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.50d, LuceneDocumentAdapterUtils.determineBoots(1000, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.55d, LuceneDocumentAdapterUtils.determineBoots(950, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.65d, LuceneDocumentAdapterUtils.determineBoots(850, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.75d, LuceneDocumentAdapterUtils.determineBoots(750, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.85d, LuceneDocumentAdapterUtils.determineBoots(650, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(0.95d, LuceneDocumentAdapterUtils.determineBoots(550, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.00d, LuceneDocumentAdapterUtils.determineBoots(500, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.05d, LuceneDocumentAdapterUtils.determineBoots(450, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.15d, LuceneDocumentAdapterUtils.determineBoots(350, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.25d, LuceneDocumentAdapterUtils.determineBoots(250, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.35d, LuceneDocumentAdapterUtils.determineBoots(150, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.45d, LuceneDocumentAdapterUtils.determineBoots(50, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(1.50d, LuceneDocumentAdapterUtils.determineBoots(0, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(2.00d, LuceneDocumentAdapterUtils.determineBoots(-500, 500, 1000, 0.25d, 5d), 0.01d);
        assertEquals(5.00d, LuceneDocumentAdapterUtils.determineBoots(-10000, 500, 1000, 0.25d, 5d), 0.01d);

        // SKU ranking, mid point is 0 with step 50 so that it is not too distributed
        assertEquals(1.20d, LuceneDocumentAdapterUtils.determineBoots(-10, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(1.00d, LuceneDocumentAdapterUtils.determineBoots(0, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(0.90d, LuceneDocumentAdapterUtils.determineBoots(5, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(0.80d, LuceneDocumentAdapterUtils.determineBoots(10, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(0.50d, LuceneDocumentAdapterUtils.determineBoots(25, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(0.25d, LuceneDocumentAdapterUtils.determineBoots(50, 0, 50, 0.25d, 5d), 0.01d);
        assertEquals(0.25d, LuceneDocumentAdapterUtils.determineBoots(100, 0, 50, 0.25d, 5d), 0.01d);


    }

}