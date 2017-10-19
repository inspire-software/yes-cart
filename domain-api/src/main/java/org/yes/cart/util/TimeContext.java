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

package org.yes.cart.util;

import java.util.Calendar;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 17/10/2017
 * Time: 18:31
 */
public class TimeContext {

    private static ThreadLocal<Date> NOW = new ThreadLocal<Date>();

    private static final ThreadLocal<Calendar> CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };



    /**
     * Time frame set for this thread local.
     * If time frame is not set then new Date() is returned.
     *
     * @return date now
     */
    public static long getMillis() {
        final Date date = NOW.get();
        if (date == null) {
            return System.currentTimeMillis();
        }
        return date.getTime();
    }


    /**
     * Time frame set for this thread local.
     * If time frame is not set then new Date() is returned.
     *
     * @return date now
     */
    public static Date getTime() {
        final Date date = NOW.get();
        if (date == null) {
            return new Date();
        }
        return (Date) date.clone();
    }


    /**
     * Time frame set for this thread local.
     * If time frame is not set then new Date() is returned.
     *
     * @return date now
     */
    public static Calendar getCalendar() {
        final Calendar cal = CALENDAR.get();
        cal.setTime(getTime());
        return (Calendar) cal.clone();
    }


    /**
     * Time frame to use for this thread local.
     *
     * @param time time
     */
    public static void setTime(final Date time) {
        NOW.set(time);
    }

    /**
     * Set time frame to now (new Date())
     */
    public static void setNow() {
        setTime(new Date());
    }

    /**
     * Remove thread local setting
     */
    public static void clear() {
        setTime(null);
    }

}
