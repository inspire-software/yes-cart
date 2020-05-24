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

package org.yes.cart.utils;

import org.junit.Test;

import java.time.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/01/2018
 * Time: 21:51
 */
public class DateUtilsTest {


    @Test
    public void instant() throws Exception {

        final Instant instant = Instant.now();

        final String format = DateUtils.formatSDT(instant);

        final Instant copy = DateUtils.iParseSDT(format);

        assertEquals(instant.toEpochMilli(), copy.toEpochMilli());

        final Instant fromMillis = DateUtils.iParseSDT(String.valueOf(instant.toEpochMilli()));

        assertEquals(instant.toEpochMilli(), fromMillis.toEpochMilli());

        final ZonedDateTime zonedDateTime = fromMillis.atZone(DateUtils.zone());

        final String sdtFormat = DateUtils.formatSDT(zonedDateTime);

        final Instant stdInstant = DateUtils.iParseSDT(sdtFormat);

        assertEquals(instant.toEpochMilli() / 1000, stdInstant.toEpochMilli() / 1000);

    }

    @Test
    public void convert() throws Exception {

        Instant instant;

        instant = DateUtils.iParseSDT("2017-10-10 00:00:00");
        final LocalDate localDate = DateUtils.ldParseSDT("2017-10-10");
        assertEquals(instant.toEpochMilli(), DateUtils.millis(localDate));
        assertEquals(0L, DateUtils.millis((LocalDate) null));
        assertEquals(localDate, DateUtils.ldFrom(instant.toEpochMilli()));

        instant = DateUtils.iParseSDT("2017-10-10 11:55:23");
        final LocalDateTime localDateTime = DateUtils.ldtParseSDT("2017-10-10 11:55:23");
        assertEquals(instant.toEpochMilli(), DateUtils.millis(localDateTime));
        assertEquals(0L, DateUtils.millis((LocalDateTime) null));
        assertEquals(localDateTime, DateUtils.ldtFrom(instant.toEpochMilli()));

        instant = DateUtils.iParseSDT("2017-10-10 11:55:23");
        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 11:55:23");
        assertEquals(instant.toEpochMilli(), DateUtils.millis(zonedDateTime));
        assertEquals(0L, DateUtils.millis((ZonedDateTime) null));
        assertEquals(zonedDateTime, DateUtils.zdtFrom(instant.toEpochMilli()));

        instant = DateUtils.iParseSDT("2017-10-10 11:55:23");
        assertEquals(instant, DateUtils.iFrom(instant.toEpochMilli()));
        assertEquals(instant, DateUtils.iFrom(DateUtils.ldtParseSDT("2017-10-10 11:55:23")));
        assertEquals(instant, DateUtils.iFrom(DateUtils.zdtParseSDT("2017-10-10 11:55:23")));
        instant = DateUtils.iParseSDT("2017-10-10");
        assertEquals(instant, DateUtils.iFrom(DateUtils.ldParseSDT("2017-10-10")));

    }

    @Test
    public void standardDate() throws Exception {

        final LocalDate localDate = DateUtils.ldParseSDT("2017-10-10");
        assertNotNull(localDate);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(localDate));
        assertEquals("2017-10-10", DateUtils.formatSD(localDate));

        final LocalDateTime localDateTime = DateUtils.ldtParseSDT("2017-10-10");
        assertNotNull(localDateTime);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(localDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(localDateTime));

        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10");
        assertNotNull(zonedDateTime);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(zonedDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(zonedDateTime));

    }

    @Test
    public void standardDateTime() throws Exception {

        assertNull(DateUtils.formatSDT((Instant) null));
        final Instant instant = DateUtils.iParseSDT("2017-10-10 10:30:00");
        assertNotNull(instant);
        assertTrue(DateUtils.formatSDT(instant).contains("2017-10-10T10:30"));

        assertNull(DateUtils.formatSDT((LocalDate) null));
        final LocalDate localDate = DateUtils.ldParseSDT("2017-10-10 10:30:00");
        assertNotNull(localDate);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(localDate));
        assertEquals("2017-10-10", DateUtils.formatSD(localDate));

        assertNull(DateUtils.formatSDT((LocalDateTime) null));
        final LocalDateTime localDateTime = DateUtils.ldtParseSDT("2017-10-10 10:30:00");
        assertNotNull(localDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(localDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(localDateTime));

        assertNull(DateUtils.formatSDT((ZonedDateTime) null));
        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 10:30:00");
        assertNotNull(zonedDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(zonedDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(zonedDateTime));

        assertNotNull(DateUtils.formatSDT());

    }

    @Test
    public void custom() throws Exception {

        final LocalDate localDate = DateUtils.ldParse("10/10/17 10:30:00", "dd/MM/yy HH:mm:ss");
        assertNotNull(localDate);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(localDate));
        assertEquals("2017-10-10", DateUtils.formatSD(localDate));
        assertEquals("10/10/17 00:00:00", DateUtils.format(localDate, "dd/MM/yy HH:mm:ss"));

        final LocalDateTime localDateTime = DateUtils.ldtParse("10/10/17 10:30:00", "dd/MM/yy HH:mm:ss");
        assertNotNull(localDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(localDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(localDateTime));
        assertEquals("10/10/17 10:30", DateUtils.format(localDateTime, "dd/MM/yy HH:mm"));

        final ZonedDateTime zonedDateTime = DateUtils.zdtParse("10/10/17 10:30:00", "dd/MM/yy HH:mm:ss");
        assertNotNull(zonedDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(zonedDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(zonedDateTime));
        assertEquals("10/10/17 10:30:00", DateUtils.format(zonedDateTime, "dd/MM/yy HH:mm:ss"));

        assertEquals("10/10/17 10:30:00", DateUtils.format(zonedDateTime.toInstant(), "dd/MM/yy HH:mm:ss"));

        assertNotNull(DateUtils.formatSDT());

    }


    @Test
    public void exportFileTimestamp() throws Exception {

        assertEquals("20171010103000", DateUtils.exportFileTimestamp(DateUtils.iParseSDT("2017-10-10 10:30:00")));
        assertEquals("20171010103000", DateUtils.exportFileTimestamp(DateUtils.ldtParseSDT("2017-10-10 10:30:00")));
        assertEquals("20171010103000", DateUtils.exportFileTimestamp(DateUtils.zdtParseSDT("2017-10-10 10:30:00")));

        assertNotNull(DateUtils.exportFileTimestamp());

    }


    @Test
    public void numberTimestamp() throws Exception {

        assertEquals("20171010103000", DateUtils.numberTimestamp(DateUtils.iParseSDT("2017-10-10 10:30:00")));
        assertEquals("20171010103000", DateUtils.numberTimestamp(DateUtils.ldtParseSDT("2017-10-10 10:30:00")));
        assertEquals("20171010103000", DateUtils.numberTimestamp(DateUtils.zdtParseSDT("2017-10-10 10:30:00")));

    }


    @Test
    public void impexFileTimestamp() throws Exception {

        assertEquals("2017-10-10_103000", DateUtils.impexFileTimestamp(DateUtils.iParseSDT("2017-10-10 10:30:00")));
        assertEquals("2017-10-10_103000", DateUtils.impexFileTimestamp(DateUtils.ldtParseSDT("2017-10-10 10:30:00")));
        assertEquals("2017-10-10_103000", DateUtils.impexFileTimestamp(DateUtils.zdtParseSDT("2017-10-10 10:30:00")));

        assertNotNull(DateUtils.impexFileTimestamp());

    }


    @Test
    public void autoImportTimestamp() throws Exception {

        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 10:30:00");

        assertEquals("_2017-10-10_103000_000", DateUtils.autoImportTimestamp(zonedDateTime));

        assertNotNull(DateUtils.autoImportTimestamp());

    }


    @Test
    public void formatCustomer() throws Exception {

        assertEquals("10 October 2017", DateUtils.formatCustomer(DateUtils.iParseSDT("2017-10-10 10:30:00"), Locale.US));
        assertEquals("10 October 2017", DateUtils.formatCustomer(DateUtils.ldParseSDT("2017-10-10 10:30:00"), Locale.US));
        assertEquals("10 October 2017", DateUtils.formatCustomer(DateUtils.ldtParseSDT("2017-10-10 10:30:00"), Locale.US));
        assertEquals("10 October 2017", DateUtils.formatCustomer(DateUtils.zdtParseSDT("2017-10-10 10:30:00"), Locale.US));

    }


    @Test
    public void formatYear() throws Exception {

        final Instant now = Instant.now();

        assertEquals(String.valueOf(now.atZone(DateUtils.zone()).getYear()), DateUtils.formatYear());

    }

    @Test
    public void atStartOfYear() throws Exception {

        assertNull(DateUtils.zdtAtStartOfYear(null));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfYear(DateUtils.zdtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfYear(DateUtils.zdtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfYear(DateUtils.zdtParseSDT("2017-10-01 00:00:00"))));

        assertNull(DateUtils.ldtAtStartOfYear(null));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfYear(DateUtils.ldtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfYear(DateUtils.ldtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-01-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfYear(DateUtils.ldtParseSDT("2017-10-01 00:00:00"))));

    }

    @Test
    public void atStartOfMonth() throws Exception {

        assertNull(DateUtils.zdtAtStartOfMonth(null));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfMonth(DateUtils.zdtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfMonth(DateUtils.zdtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfMonth(DateUtils.zdtParseSDT("2017-10-01 00:00:00"))));

        assertNull(DateUtils.ldtAtStartOfMonth(null));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfMonth(DateUtils.ldtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfMonth(DateUtils.ldtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfMonth(DateUtils.ldtParseSDT("2017-10-01 00:00:00"))));

    }


    @Test
    public void atStartOfWeek() throws Exception {

        assertNull(DateUtils.zdtAtStartOfWeek(null));
        assertEquals("2017-09-25 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-01 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-02 23:59:59"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-03 00:00:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-04 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-05 23:59:59"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-06 00:00:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-07 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-08 23:59:59"))));
        assertEquals("2017-10-09 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfWeek(DateUtils.zdtParseSDT("2017-10-09 00:00:00"))));

        assertNull(DateUtils.ldtAtStartOfWeek(null));
        assertEquals("2017-09-25 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-01 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-02 23:59:59"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-03 00:00:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-04 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-05 23:59:59"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-06 00:00:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-07 10:30:00"))));
        assertEquals("2017-10-02 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-08 23:59:59"))));
        assertEquals("2017-10-09 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfWeek(DateUtils.ldtParseSDT("2017-10-09 00:00:00"))));


    }


    @Test
    public void atStartOfDay() throws Exception {

        assertNull(DateUtils.zdtAtStartOfDay(null));
        assertEquals("2017-10-10 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfDay(DateUtils.zdtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-10-31 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfDay(DateUtils.zdtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.zdtAtStartOfDay(DateUtils.zdtParseSDT("2017-10-01 00:00:00"))));

        assertNull(DateUtils.ldtAtStartOfDay(null));
        assertEquals("2017-10-10 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfDay(DateUtils.ldtParseSDT("2017-10-10 10:30:00"))));
        assertEquals("2017-10-31 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfDay(DateUtils.ldtParseSDT("2017-10-31 23:59:59"))));
        assertEquals("2017-10-01 00:00:00",
                DateUtils.formatSDT(DateUtils.ldtAtStartOfDay(DateUtils.ldtParseSDT("2017-10-01 00:00:00"))));

    }


    @Test
    public void fromCalendarDaysOfWeekToISO() throws Exception {

        assertNotNull(DateUtils.fromCalendarDaysOfWeekToISO(null));
        assertTrue(DateUtils.fromCalendarDaysOfWeekToISO(null).isEmpty());

        assertNotNull(DateUtils.fromCalendarDaysOfWeekToISO(Collections.emptyList()));
        assertTrue(DateUtils.fromCalendarDaysOfWeekToISO(Collections.emptyList()).isEmpty());

        List<DayOfWeek> dow;
        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.MONDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.MONDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.TUESDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.TUESDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.WEDNESDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.WEDNESDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.THURSDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.THURSDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.FRIDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.FRIDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.SATURDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.SATURDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Collections.singletonList(Calendar.SUNDAY));
        assertNotNull(dow);
        assertEquals(1, dow.size());
        assertTrue(dow.contains(DayOfWeek.SUNDAY));

        dow = DateUtils.fromCalendarDaysOfWeekToISO(Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.SUNDAY));
        assertNotNull(dow);
        assertEquals(3, dow.size());
        assertTrue(dow.contains(DayOfWeek.MONDAY));
        assertTrue(dow.contains(DayOfWeek.WEDNESDAY));
        assertTrue(dow.contains(DayOfWeek.SUNDAY));
    }

    @Test
    public void fromISOtoCalendarDayOfWeek() throws Exception {

        assertEquals(-1, DateUtils.fromISOtoCalendarDayOfWeek(null));
        assertEquals(Calendar.MONDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.MONDAY));
        assertEquals(Calendar.TUESDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.TUESDAY));
        assertEquals(Calendar.WEDNESDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.WEDNESDAY));
        assertEquals(Calendar.THURSDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.THURSDAY));
        assertEquals(Calendar.FRIDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.FRIDAY));
        assertEquals(Calendar.SATURDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.SATURDAY));
        assertEquals(Calendar.SUNDAY, DateUtils.fromISOtoCalendarDayOfWeek(DayOfWeek.SUNDAY));

    }

}