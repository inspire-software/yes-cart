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

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.order.OrderNumberGenerator;

import java.util.Calendar;
import java.util.Date;
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
    private Date lastCheck;

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

    void setLastCheck(final Date lastCheck) {
        this.lastCheck = lastCheck;
    }

    Calendar now() {
        return Calendar.getInstance();
    }

    /**
     * Generate Order number.
     *
     * @return Generated order number.
     */
    public synchronized String getNextOrderNumber() {

        final Calendar now = now();
        final String datePart = datePart(now);
        final String sequencePart = getOrderSequence(now);
        return datePart + '-' + sequencePart;

    }

    String datePart(final Calendar calendar) {

        final long year = calendar.get(Calendar.YEAR);
        final long mth = calendar.get(Calendar.MONTH) + 1;
        final long day = calendar.get(Calendar.DAY_OF_MONTH);
        final long hour = calendar.get(Calendar.HOUR_OF_DAY);
        final long min = calendar.get(Calendar.MINUTE);
        final long sec = calendar.get(Calendar.SECOND);

        final long time = (year % 100) * 10000000000l + mth * 100000000l + day * 1000000l + hour * 10000l + min * 100 + sec;

        return String.valueOf(time);

    }

    private String getOrderSequence(final Calendar calendar) {

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (lastCheck == null || lastCheck.before(calendar.getTime())) {
            synchronized (DefaultOrderNumberGeneratorImpl.class) {
                if (lastCheck == null || lastCheck.before(calendar.getTime())) {
                    // Restart count for current month
                    orderSequence.set(
                            customerOrderDao.findCountByCriteria(
                                    Restrictions.ge("orderTimestamp", calendar.getTime()),
                                    Restrictions.ne("orderStatus", CustomerOrder.ORDER_STATUS_NONE)
                            )
                    );
                    lastCheck = calendar.getTime();
                }
            }
        }

        return String.valueOf(orderSequence.incrementAndGet());

    }

}
