/*
 * Copyright (c) 2010. The intellectual rights for this code remain to the NPA developer team.
 * Code distribution, sale or modification is prohibited unless authorized by all members of NPA
 * development team.
 */

package org.yes.cart.domain.entity.bridge;

import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class PriceBridgeTest extends TestCase {

     public void testPriceBridge() {
         BigDecimalBridge bigDecimalBridge = new BigDecimalBridge();
         assertEquals("00003210", bigDecimalBridge.objectToString(new BigDecimal("32.1")));
         assertEquals("00000012", bigDecimalBridge.objectToString(new BigDecimal(".12")));
         assertEquals("00009800", bigDecimalBridge.objectToString(new BigDecimal("98")));
     }

}
