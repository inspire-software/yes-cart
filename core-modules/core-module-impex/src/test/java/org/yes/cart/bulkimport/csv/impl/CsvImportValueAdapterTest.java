package org.yes.cart.bulkimport.csv.impl;

import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.util.DateUtils;
import org.yes.cart.utils.impl.ExtendedConversionService;

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
        assertEquals("Text", adapter.fromRaw("Text", CsvImpExColumn.STRING, null, null));
        assertEquals(Boolean.TRUE, adapter.fromRaw("true", CsvImpExColumn.BOOLEAN, null, null));
        assertEquals(Boolean.FALSE, adapter.fromRaw("false", CsvImpExColumn.BOOLEAN, null, null));
        assertEquals(Long.valueOf(0L), adapter.fromRaw("0", CsvImpExColumn.LONG, null, null));
        assertEquals(Long.valueOf(100L), adapter.fromRaw("100", CsvImpExColumn.LONG, null, null));
        assertEquals(Integer.valueOf(100), adapter.fromRaw("100", CsvImpExColumn.INT, null, null));
        assertEquals(new BigDecimal(100), adapter.fromRaw("100", CsvImpExColumn.DECIMAL, null, null));
        assertEquals(DateUtils.ldParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", CsvImpExColumn.DATE, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01"), adapter.fromRaw("2017-01-01", CsvImpExColumn.DATETIME, null, null));
        assertEquals(DateUtils.ldtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", CsvImpExColumn.DATETIME, null, null));
        assertEquals(DateUtils.zdtParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", CsvImpExColumn.ZONEDTIME, null, null));
        assertEquals(DateUtils.iParseSDT("2017-01-01 14:25:00"), adapter.fromRaw("2017-01-01 14:25:00", CsvImpExColumn.INSTANT, null, null));
        Instant now = Instant.now();
        assertEquals(now, adapter.fromRaw(String.valueOf(now.toEpochMilli()), CsvImpExColumn.INSTANT, null, null));

    }

}