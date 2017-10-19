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

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/10/2017
 * Time: 17:53
 */
public class TimeContextTest {
    @Test
    public void getTime() throws Exception {

        assertNotNull(TimeContext.getTime());

        final Date now = new Date();
        TimeContext.setTime(now);

        assertEquals("The time should be equal", now, TimeContext.getTime());
        assertNotSame("The time must not be same BUT CLONE", now, TimeContext.getTime());
        assertNotSame("The time must not be same BUT CLONE", TimeContext.getTime(), TimeContext.getTime());
        assertEquals("The time should be equal", now, TimeContext.getTime());

        TimeContext.clear();
        
    }

    @Test
    public void getCalendar() throws Exception {

        assertNotNull(TimeContext.getCalendar());

        final Date now = new Date();
        TimeContext.setTime(now);

        assertEquals("The calendar should be equal", now, TimeContext.getCalendar().getTime());
        assertNotSame("The calendar must not be same BUT CLONE", now, TimeContext.getCalendar().getTime());
        assertNotSame("The calendar must not be same BUT CLONE", TimeContext.getCalendar(), TimeContext.getCalendar());
        assertEquals("The calendar should be equal", now, TimeContext.getCalendar().getTime());

        TimeContext.clear();

    }

    @Test
    public void setNow() throws Exception {

        TimeContext.setTime(new Date(0L));
        assertEquals(0L, TimeContext.getTime().getTime());

        final Date now = new Date();
        TimeContext.setNow();
        assertTrue(!now.after(TimeContext.getTime()));

        TimeContext.clear();

    }

    @Test
    public void clear() throws Exception {

        assertNotNull(TimeContext.getCalendar());

        final Date now = new Date();
        TimeContext.setTime(now);

        assertEquals("The time should be equal", now, TimeContext.getCalendar().getTime());

        try {
            Thread.sleep(10L);
        } catch (InterruptedException ie) {}

        TimeContext.clear();

        assertTrue(now.before(TimeContext.getTime()));

        TimeContext.clear();

    }

}