package org.yes.cart.domain.entity.bridge;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class PriceBridgeTest {

    @Test
    public void testPriceBridge() {
        BigDecimalBridge bigDecimalBridge = new BigDecimalBridge();
        assertEquals("00003210", bigDecimalBridge.objectToString(new BigDecimal("32.1")));
        assertEquals("00000012", bigDecimalBridge.objectToString(new BigDecimal(".12")));
        assertEquals("00009800", bigDecimalBridge.objectToString(new BigDecimal("98")));
    }
}
