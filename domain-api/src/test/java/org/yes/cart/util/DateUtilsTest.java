package org.yes.cart.util;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        final LocalDate localDate = LocalDate.now();
        final Date dateFromLD = DateUtils.from(localDate);
        assertEquals(dateFromLD.getTime(), DateUtils.millis(localDate));
        assertEquals(0L, DateUtils.millis((LocalDate) null));

        final LocalDateTime localDateTime = LocalDateTime.now();
        final Date dateFromLDT = DateUtils.from(localDateTime);
        assertEquals(dateFromLDT.getTime(), DateUtils.millis(localDateTime));
        assertEquals(0L, DateUtils.millis((LocalDateTime) null));

        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final Date dateFromZDT = DateUtils.from(zonedDateTime);
        assertEquals(dateFromZDT.getTime(), DateUtils.millis(zonedDateTime));
        assertEquals(0L, DateUtils.millis((ZonedDateTime) null));

        final Instant instant = Instant.now();
        final Date dateFromI = DateUtils.from(instant);
        assertEquals(dateFromI.getTime(), instant.toEpochMilli());

    }

    @Test
    public void standardDate() throws Exception {

        final Date date = DateUtils.dParseSDT("2017-10-10");
        assertNotNull(date);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(date));
        assertEquals("2017-10-10", DateUtils.formatSD(date));

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

        final Instant testSQLDate = DateUtils.iParseSDT("2017-10-10");
        final java.sql.Date sqlDate = new java.sql.Date(testSQLDate.toEpochMilli());
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(sqlDate));
        assertEquals("2017-10-10", DateUtils.formatSD(sqlDate));

        final Instant testSQLTimestamp = DateUtils.iParseSDT("2017-10-10");
        final Timestamp sqlTimestamp = new Timestamp(testSQLTimestamp.toEpochMilli());
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(sqlTimestamp));
        assertEquals("2017-10-10", DateUtils.formatSD(sqlTimestamp));

    }

    @Test
    public void standardDateTime() throws Exception {

        final Date date = DateUtils.dParseSDT("2017-10-10 10:30:00");
        assertNotNull(date);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(date));
        assertEquals("2017-10-10", DateUtils.formatSD(date));

        final LocalDate localDate = DateUtils.ldParseSDT("2017-10-10 10:30:00");
        assertNotNull(localDate);
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(localDate));
        assertEquals("2017-10-10", DateUtils.formatSD(localDate));

        final LocalDateTime localDateTime = DateUtils.ldtParseSDT("2017-10-10 10:30:00");
        assertNotNull(localDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(localDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(localDateTime));

        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 10:30:00");
        assertNotNull(zonedDateTime);
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(zonedDateTime));
        assertEquals("2017-10-10", DateUtils.formatSD(zonedDateTime));

        assertNotNull(DateUtils.formatSDT());

        final Instant testSQLDate = DateUtils.iParseSDT("2017-10-10 10:30:00");
        final java.sql.Date sqlDate = new java.sql.Date(testSQLDate.toEpochMilli());
        assertEquals("2017-10-10 00:00:00", DateUtils.formatSDT(sqlDate));
        assertEquals("2017-10-10", DateUtils.formatSD(sqlDate));

        final Instant testSQLTimestamp = DateUtils.iParseSDT("2017-10-10 10:30:00");
        final Timestamp sqlTimestamp = new Timestamp(testSQLTimestamp.toEpochMilli());
        assertEquals("2017-10-10 10:30:00", DateUtils.formatSDT(sqlTimestamp));
        assertEquals("2017-10-10", DateUtils.formatSD(sqlTimestamp));


    }


    @Test
    public void exportFileTimestamp() throws Exception {

        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 10:30:00");

        assertEquals("20171010103000", DateUtils.exportFileTimestamp(zonedDateTime));

        assertNotNull(DateUtils.exportFileTimestamp());

    }


    @Test
    public void impexFileTimestamp() throws Exception {

        final ZonedDateTime zonedDateTime = DateUtils.zdtParseSDT("2017-10-10 10:30:00");

        assertEquals("2017-10-10_103000", DateUtils.impexFileTimestamp(zonedDateTime));

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

        final Date date = DateUtils.dParseSDT("2017-10-10 10:30:00");

        assertEquals("10 October 2017", DateUtils.formatCustomer(date, Locale.UK));

    }


    @Test
    public void formatYear() throws Exception {

        final Instant now = Instant.now();

        assertEquals(String.valueOf(now.atZone(DateUtils.zone()).getYear()), DateUtils.formatYear());

    }
}