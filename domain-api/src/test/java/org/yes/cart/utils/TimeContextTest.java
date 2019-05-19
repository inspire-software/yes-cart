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

package org.yes.cart.utils;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/10/2017
 * Time: 17:53
 */
public class TimeContextTest {

    @Test
    public void defaults() throws Exception {

        assertTrue(TimeContext.getMillis() > 0L);
        assertNotNull(TimeContext.getZone());
        assertNotNull(TimeContext.getLocalDateTime());

    }

    @Test
    public void getTime() throws Exception {

        assertNotNull(TimeContext.getTime());

        final Instant now = Instant.now();
        TimeContext.setTime(now);

        assertEquals("The time should be equal", now, TimeContext.getTime());
        assertSame("The time must be the same since Instant is immutable", now, TimeContext.getTime());
        assertSame("The time must be the same since Instant is immutable", TimeContext.getTime(), TimeContext.getTime());

        TimeContext.clear();
        
    }

    @Test
    public void getLocalDateTime() throws Exception {

        assertNotNull(TimeContext.getLocalDateTime());

        final Instant now = Instant.now();
        TimeContext.setTime(now);

        assertEquals("The calendar should be equal", now, TimeContext.getLocalDateTime().atZone(TimeContext.getZone()).toInstant());
        assertNotSame("The time must be the same since LocalDateTime is immutable", TimeContext.getLocalDateTime(), TimeContext.getLocalDateTime());

        TimeContext.clear();

    }

    @Test
    public void setNow() throws Exception {

        TimeContext.setTime(Instant.ofEpochMilli(0L));
        assertEquals(0L, TimeContext.getTime().toEpochMilli());
        assertEquals(0L, TimeContext.getMillis());

        final Instant now = Instant.now();
        TimeContext.setNow();
        assertTrue(!now.isAfter(TimeContext.getTime()));

        TimeContext.clear();

    }

    @Test
    public void clear() throws Exception {

        assertNotNull(TimeContext.getLocalDateTime());

        final Instant now = Instant.now();
        TimeContext.setTime(now);

        assertEquals("The time should be equal", now, TimeContext.getLocalDateTime().atZone(TimeContext.getZone()).toInstant());

        try {
            Thread.sleep(10L);
        } catch (InterruptedException ie) {
            // OK
        }

        TimeContext.clear();

        assertTrue(now.isBefore(TimeContext.getTime()));

        TimeContext.clear();

    }

}