package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExValues;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.service.misc.impl.ExtendedConversionService;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 10/02/2018
 * Time: 22:38
 */
public class CsvImportValueAdapterTest {

    private CsvImportValueAdapter adapter = new CsvImportValueAdapter(new ExtendedConversionService());

    @Test
    public void fromRaw() throws Exception {


        assertEquals("Text", adapter.fromRaw("Text", null, null, null));
        assertEquals("Text", adapter.fromRaw("Text", ImpExValues.STRING, null, null));
        assertEquals(Boolean.TRUE, adapter.fromRaw("true", ImpExValues.BOOLEAN, null, null));
        assertEquals(Boolean.FALSE, adapter.fromRaw("false", ImpExValues.BOOLEAN, null, null));
        assertEquals(Long.valueOf(0L), adapter.fromRaw("0", ImpExValues.LONG, null, null));
        assertEquals(Long.valueOf(100L), adapter.fromRaw("100", ImpExValues.LONG, null, null));
        assertEquals(Integer.valueOf(100), adapter.fromRaw("100", ImpExValues.INT, null, null));
        assertEquals(new BigDecimal(100), adapter.fromRaw("100", ImpExValues.DECIMAL, null, null));
        assertEquals(DateUtils.ldParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", ImpExValues.DATE, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", ImpExValues.DATETIME, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExValues.DATETIME, null, null));
        assertEquals(DateUtils.zdtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExValues.ZONEDTIME, null, null));
        assertEquals(DateUtils.iParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExValues.INSTANT, null, null));
        Instant now = Instant.now();
        Instant converted = (Instant) adapter.fromRaw(String.valueOf(now.toEpochMilli()), ImpExValues.INSTANT, null, null);
        assertEquals(DateUtils.format(now, "yyyyMMddHHmmss"), DateUtils.format(converted, "yyyyMMddHHmmss"));

    }

}