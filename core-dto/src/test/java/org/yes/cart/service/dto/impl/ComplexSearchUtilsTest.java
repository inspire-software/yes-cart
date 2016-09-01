package org.yes.cart.service.dto.impl;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/09/2016
 * Time: 10:13
 */
public class ComplexSearchUtilsTest {

    @Test
    public void testCheckSpecialSearch() throws Exception {

        assertNull(ComplexSearchUtils.checkSpecialSearch("a", new char[]{'#'}));
        assertNull(ComplexSearchUtils.checkSpecialSearch("abc", new char[]{'#'}));
        assertNull(ComplexSearchUtils.checkSpecialSearch("#", new char[]{'#'}));

        Pair<String, String> search;
        search = ComplexSearchUtils.checkSpecialSearch("#tag", new char[]{'#'});
        assertNotNull(search);
        assertEquals("#", search.getFirst());
        assertEquals("tag", search.getSecond());
        search = ComplexSearchUtils.checkSpecialSearch("?phrase", new char[]{'#', '?'});
        assertNotNull(search);
        assertEquals("?", search.getFirst());
        assertEquals("phrase", search.getSecond());

    }

    @Test
    public void testCheckNumericSearch() throws Exception {

        assertNull(ComplexSearchUtils.checkNumericSearch("a", new char[] { '-' }, 2));
        assertNull(ComplexSearchUtils.checkNumericSearch("abc", new char[] { '-' }, 2));
        assertNull(ComplexSearchUtils.checkNumericSearch("-", new char[] { '-' }, 2));
        assertNull(ComplexSearchUtils.checkNumericSearch("--", new char[] { '-' }, 2));
        assertNull(ComplexSearchUtils.checkNumericSearch("-a", new char[] { '-' }, 2));
        assertNull(ComplexSearchUtils.checkNumericSearch("-1", new char[]{'-'}, 2));

        Pair<String, BigDecimal> search;
        search = ComplexSearchUtils.checkNumericSearch("-1", new char[]{'-', '+'}, 2);
        assertNotNull(search);
        assertEquals("-", search.getFirst());
        assertEquals("1.00", search.getSecond().toPlainString());
        search = ComplexSearchUtils.checkNumericSearch("-1.001", new char[]{'-', '+'}, 2);
        assertNotNull(search);
        assertEquals("-", search.getFirst());
        assertEquals("1.01", search.getSecond().toPlainString());
        search = ComplexSearchUtils.checkNumericSearch("+1", new char[] { '-', '+' }, 3);
        assertNotNull(search);
        assertEquals("+", search.getFirst());
        assertEquals("1.000", search.getSecond().toPlainString());

    }

    @Test
    public void testCheckDateRangeSearch() throws Exception {

        assertNull(ComplexSearchUtils.checkDateRangeSearch("a"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("today"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("1982"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("1982099766<"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("<1982099766"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("1982a<"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("1982-SEP<"));
        assertNull(ComplexSearchUtils.checkDateRangeSearch("<1982-SEP"));

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pair<Date, Date> search;

        search = ComplexSearchUtils.checkDateRangeSearch("1982<");
        assertNotNull(search);
        assertEquals("1982-01-01 00:00:00.000", format.format(search.getFirst()));
        assertNull(search.getSecond());

        search = ComplexSearchUtils.checkDateRangeSearch("1982-09<");
        assertNotNull(search);
        assertEquals("1982-09-01 00:00:00.000", format.format(search.getFirst()));
        assertNull(search.getSecond());

        search = ComplexSearchUtils.checkDateRangeSearch("1982-09-28<");
        assertNotNull(search);
        assertEquals("1982-09-28 00:00:00.000", format.format(search.getFirst()));
        assertNull(search.getSecond());

        search = ComplexSearchUtils.checkDateRangeSearch("1982-09-31<");
        assertNotNull(search);
        assertEquals("1982-10-01 00:00:00.000", format.format(search.getFirst())); // calendar feature!
        assertNull(search.getSecond());

        search = ComplexSearchUtils.checkDateRangeSearch("<1982");
        assertNotNull(search);
        assertNull(search.getFirst());
        assertEquals("1982-01-01 00:00:00.000", format.format(search.getSecond()));

        search = ComplexSearchUtils.checkDateRangeSearch("<1982-09");
        assertNotNull(search);
        assertNull(search.getFirst());
        assertEquals("1982-09-01 00:00:00.000", format.format(search.getSecond()));

        search = ComplexSearchUtils.checkDateRangeSearch("<1982-09-28");
        assertNotNull(search);
        assertNull(search.getFirst());
        assertEquals("1982-09-28 00:00:00.000", format.format(search.getSecond()));

        search = ComplexSearchUtils.checkDateRangeSearch("<1982-09-31");
        assertNotNull(search);
        assertNull(search.getFirst());
        assertEquals("1982-10-01 00:00:00.000", format.format(search.getSecond())); // calendar feature!

        search = ComplexSearchUtils.checkDateRangeSearch("1982-09<2017-03-12");
        assertNotNull(search);
        assertEquals("1982-09-01 00:00:00.000", format.format(search.getFirst()));
        assertEquals("2017-03-12 00:00:00.000", format.format(search.getSecond()));

    }
}