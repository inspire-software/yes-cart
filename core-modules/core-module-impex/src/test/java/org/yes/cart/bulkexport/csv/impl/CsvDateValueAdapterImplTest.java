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

        final ImpExColumn column = this.mockery.mock(ImpExColumn.class);
        final ImpExTuple tuple = this.mockery.mock(ImpExTuple.class);

        final CsvDateValueAdapterImpl adapter = new CsvDateValueAdapterImpl();

        this.mockery.checking(new Expectations() {{
            oneOf(column).getContext(); will(returnValue("yyyy-MM-dd HH:mm:ss"));
        }});

        assertEquals(
                "2017-06-06 00:00:00",
                adapter.fromRaw(DateUtils.dParseSDT("2017-06-06"), "DATE", column, tuple)
        );

        this.mockery.assertIsSatisfied();

    }

}