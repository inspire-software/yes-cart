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

package org.yes.cart.service.order.impl;

import org.hibernate.criterion.Criterion;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DefaultOrderNumberGeneratorImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testDatePart() throws Exception {

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final Date lastCheck = format.parse("2017-01-01 00:00:00");

        final DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl();
        defaultOrderNumberGenerator.setLastCheck(lastCheck);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(format.parse("2017-02-05 14:04:34"));
        assertEquals("170205140434", defaultOrderNumberGenerator.datePart(calendar));

        calendar.setTime(format.parse("2017-01-31 04:43:00"));
        assertEquals("170131044300", defaultOrderNumberGenerator.datePart(calendar));

        calendar.setTime(format.parse("2017-12-31 23:59:59"));
        assertEquals("171231235959", defaultOrderNumberGenerator.datePart(calendar));

        calendar.setTime(format.parse("2017-01-01 00:00:00"));
        assertEquals("170101000000", defaultOrderNumberGenerator.datePart(calendar));

    }

    @Test
    public void testGetNextOrderNumber() throws Exception {

        final GenericDAO<CustomerOrder, Long> customerOrderDao = this.mockery.mock(GenericDAO.class, "customerOrderDao");

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Calendar now = Calendar.getInstance();
        now.setTime(format.parse("2017-12-25 11:11:11"));

        DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl(customerOrderDao) {
            @Override
            Calendar now() {
                return (Calendar) now.clone();
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(customerOrderDao).findCountByCriteria(with(any(Criterion[].class))); will(returnValue(0));
        }});

        int prevMonth = 11;
        int prev = 0;
        int countMth = 1;

        for (int day = 1; day < 40; day++) {

            final int month = now.get(Calendar.MONTH);
            if (month != prevMonth)  {
                prevMonth = month;
                prev = 0;
                countMth++;
            }

            for (int i = 1; i <= 10000; i = i + 1) {
                String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
                String expected = "-" + (i + prev);
                assertEquals(expected, orderNum.substring(orderNum.indexOf("-")));
            }
            now.add(Calendar.DAY_OF_YEAR, 1);
            prev += 10000;
        }

        assertEquals(3, countMth);

        this.mockery.assertIsSatisfied();
    }

    @Test
    public void testGetNextOrderNumberMultithread() throws Exception {


        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date lastCheck = format.parse("2017-01-01 00:00:00");
        final Calendar now = Calendar.getInstance();
        now.setTime(format.parse("2017-01-01 11:11:11"));

        //Multithread.
        int THREADGROUPSIZE = 100;
        DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator = new DefaultOrderNumberGeneratorImpl() {
            @Override
            Calendar now() {
                return now;
            }
        };
        defaultOrderNumberGenerator.setLastCheck(lastCheck);
        Set<String> rez = Collections.synchronizedSet(new HashSet<String>());
        final CyclicBarrier gate = new CyclicBarrier(THREADGROUPSIZE);
        Thread[] th = new Thread[THREADGROUPSIZE];
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            th[i] = new MyOrderCreatorThread(defaultOrderNumberGenerator, rez, gate);
            th[i].start();
        }
        for (int i = 0; i < THREADGROUPSIZE; i++) {
            try {
                th[i].join();
            } catch (InterruptedException e) {
                // System.out.print("Join interrupted\n");
            }
        }
        assertEquals(THREADGROUPSIZE, rez.size()); // if set has all elem then no duplicates

    }

    class MyOrderCreatorThread extends Thread {

        private DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator;
        private Set<String> generated;
        private CyclicBarrier gate;

        MyOrderCreatorThread(final DefaultOrderNumberGeneratorImpl defaultOrderNumberGenerator, final Set<String> generated, final CyclicBarrier gate) {
            this.defaultOrderNumberGenerator = defaultOrderNumberGenerator;
            this.generated = generated;
            this.gate = gate;
        }

        @Override
        public void run() {
            try {
                gate.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            String orderNum = defaultOrderNumberGenerator.getNextOrderNumber();
            assertFalse(generated.contains(orderNum));
            generated.add(orderNum);
        }
    }
}
