package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.util.DateUtils;
import org.yes.cart.utils.impl.ExtendedConversionService;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.*;

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
        assertEquals("Text", adapter.fromRaw("Text", ImpExColumn.STRING, null, null));
        assertEquals(Boolean.TRUE, adapter.fromRaw("true", ImpExColumn.BOOLEAN, null, null));
        assertEquals(Boolean.FALSE, adapter.fromRaw("false", ImpExColumn.BOOLEAN, null, null));
        assertEquals(Long.valueOf(0L), adapter.fromRaw("0", ImpExColumn.LONG, null, null));
        assertEquals(Long.valueOf(100L), adapter.fromRaw("100", ImpExColumn.LONG, null, null));
        assertEquals(Integer.valueOf(100), adapter.fromRaw("100", ImpExColumn.INT, null, null));
        assertEquals(new BigDecimal(100), adapter.fromRaw("100", ImpExColumn.DECIMAL, null, null));
        assertEquals(DateUtils.ldParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", ImpExColumn.DATE, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", ImpExColumn.DATETIME, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExColumn.DATETIME, null, null));
        assertEquals(DateUtils.zdtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExColumn.ZONEDTIME, null, null));
        assertEquals(DateUtils.iParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", ImpExColumn.INSTANT, null, null));
        Instant now = Instant.now();
        assertEquals(now, adapter.fromRaw(String.valueOf(now.toEpochMilli()), ImpExColumn.INSTANT, null, null));

    }

}