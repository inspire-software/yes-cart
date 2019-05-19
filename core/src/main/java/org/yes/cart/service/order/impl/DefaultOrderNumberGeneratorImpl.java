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

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.order.OrderNumberGenerator;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * Generate order numbers - yyyymmdd-xxxxxx format
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DefaultOrderNumberGeneratorImpl implements OrderNumberGenerator {

    private final GenericDAO<CustomerOrder, Long> customerOrderDao;
    private AtomicInteger orderSequence = new AtomicInteger();
    private LocalDateTime lastCheck;

    /**
     * Construct order number generator service.
     * @param customerOrderDao order dao to use in check what consequence number need to use.
     */
    public DefaultOrderNumberGeneratorImpl(final GenericDAO<CustomerOrder, Long> customerOrderDao) {
        this.customerOrderDao = customerOrderDao;
        orderSequence.set(0);
        lastCheck = null;
    }

    /**
     * Default constructor.
     */
    DefaultOrderNumberGeneratorImpl() {
        customerOrderDao = null;
        orderSequence.set(0);
        lastCheck = null;
    }

    void setLastCheck(final LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * Generate Order number.
     *
     * @return Generated order number.
     */
    @Override
    public synchronized String getNextOrderNumber() {

        final LocalDateTime now = now();
        final String datePart = datePart(now);
        final String sequencePart = getOrderSequence(now);
        return datePart + '-' + sequencePart;

    }

    String datePart(final LocalDateTime dateTime) {

        final long year = dateTime.getYear();
        final long mth = dateTime.getMonthValue();
        final long day = dateTime.getDayOfMonth();
        final long hour = dateTime.getHour();
        final long min = dateTime.getMinute();
        final long sec = dateTime.getSecond();

        final long time = (year % 100) * 10000000000L + mth * 100000000L + day * 1000000L + hour * 10000L + min * 100 + sec;

        return String.valueOf(time);

    }

    private String getOrderSequence(final LocalDateTime dateTime) {

        final LocalDateTime startOfThisMonth = DateUtils.ldtAtStartOfMonth(dateTime);

        if (lastCheck == null || lastCheck.isBefore(startOfThisMonth)) {
            synchronized (DefaultOrderNumberGeneratorImpl.class) {
                if (lastCheck == null || lastCheck.isBefore(startOfThisMonth)) {
                    // Restart count for current month
                    orderSequence.set(
                            customerOrderDao.findCountByCriteria(
                                    " where e.orderTimestamp >= ?1 and e.orderStatus <> ?2",
                                    startOfThisMonth, CustomerOrder.ORDER_STATUS_NONE
                            )
                    );
                    lastCheck = startOfThisMonth;
                }
            }
        }

        return String.valueOf(orderSequence.incrementAndGet());

    }

}
