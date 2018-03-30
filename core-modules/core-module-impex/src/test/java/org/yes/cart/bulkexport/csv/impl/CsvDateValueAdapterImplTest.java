package org.yes.cart.bulkexport.csv.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.util.DateUtils;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/02/2018
 * Time: 22:36
 */
public class CsvDateValueAdapterImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void fromRaw() throws Exception {

        final ImpExColumn ldColumn = this.mockery.mock(ImpExColumn.class, "ldColumn");
        final ImpExColumn ldtColumn = this.mockery.mock(ImpExColumn.class, "ldtColumn");
        final ImpExColumn zdtColumn = this.mockery.mock(ImpExColumn.class, "zdtColumn");
        final ImpExColumn iColumn = this.mockery.mock(ImpExColumn.class, "iColumn");
        final ImpExTuple tuple = this.mockery.mock(ImpExTuple.class, "tuple");

        final CsvDateValueAdapterImpl adapter = new CsvDateValueAdapterImpl();

        this.mockery.checking(new Expectations() {{
            oneOf(ldColumn).getContext(); will(returnValue("yyyy-MM-dd"));
            oneOf(ldtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
            oneOf(zdtColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
            oneOf(iColumn).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
        }});

        assertEquals(
                "2017-06-06",
                adapter.fromRaw(DateUtils.ldParseSDT("2017-06-06"), "DATE", ldColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.ldtParseSDT("2017-06-06 15:30:17"), "DATE", ldtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.zdtParseSDT("2017-06-06 15:30:17"), "DATE", zdtColumn, tuple)
        );
        assertEquals(
                "2017-06-06 15:30:17",
                adapter.fromRaw(DateUtils.iParseSDT("2017-06-06 15:30:17"), "DATE", iColumn, tuple)
        );

        this.mockery.assertIsSatisfied();

    }

}