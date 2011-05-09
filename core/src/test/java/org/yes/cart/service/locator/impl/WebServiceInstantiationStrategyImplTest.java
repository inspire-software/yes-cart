package org.yes.cart.service.locator.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class WebServiceInstantiationStrategyImplTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetServiceName() throws Exception {

        WebServiceInstantiationStrategyImpl instantiationStrategy = new WebServiceInstantiationStrategyImpl();
        assertEquals("OrteLookup.asmx",
                instantiationStrategy.getServiceName("http://mathertel.de/AJAXEngine/S02_AJAXCoreSamples/OrteLookup.asmx"));
    }
}
