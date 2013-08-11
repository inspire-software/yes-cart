/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.order.impl;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DefaultOrderNumberGeneratorImplTest {

    @Test
    public void testGetNextOrderNumber() {
        DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl();
        for (int i = 1; i <= 10000; i = i + 1) {
            String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
            String expected = "-" + i;
            assertEquals(expected, orderNum.substring(orderNum.indexOf("-")));
        }
        //Multithread.
        int THREADGROUPSIZE = 100;
        defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl();
        Set<String> rez = Collections.synchronizedSet(new HashSet<String>());
        Thread[] th = new Thread[THREADGROUPSIZE];
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            th[i] = new MyOrderCreatorThread(defaultOrderNumberGenerator, rez);
            th[i].start();
        }
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            try {
                th[i].join();
            } catch (InterruptedException e) {
                System.out.print("Join interrupted\n");
            }
        }
        assertEquals(THREADGROUPSIZE, rez.size());
    }

    class MyOrderCreatorThread extends Thread {

        private DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator;
        private Set<String> generated;

        MyOrderCreatorThread(final DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator, final Set<String> generated) {
            this.defaultOrderNumberGenerator = defaultOrderNumberGenerator;
            this.generated = generated;
        }

        @Override
        public void run() {
            String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
            assertFalse(generated.contains(orderNum));
            generated.add(orderNum);
        }
    }
}
