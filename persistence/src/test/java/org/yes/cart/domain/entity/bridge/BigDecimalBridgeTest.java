/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.entity.bridge;

import org.junit.Test;
import org.yes.cart.constants.Constants;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 27/11/2014
 * Time: 10:52
 */
public class BigDecimalBridgeTest {

    @Test
    public void testObjectToString() throws Exception {

        assertEquals(Constants.MONEY_FORMAT_TOINDEX, new BigDecimalBridge().objectToString(BigDecimal.ZERO));
        assertEquals("100000000000000", new BigDecimalBridge().objectToString(new BigDecimal(1000000000000L)));
        assertEquals("10000000", new BigDecimalBridge().objectToString(new BigDecimal(100000L)));
        assertEquals("00100000", new BigDecimalBridge().objectToString(new BigDecimal(1000L)));
        assertEquals("00000100", new BigDecimalBridge().objectToString(new BigDecimal(1L)));
        assertEquals("00000133", new BigDecimalBridge().objectToString(new BigDecimal("1.33")));
        assertEquals("00003210", new BigDecimalBridge().objectToString(new BigDecimal("32.1")));
        assertEquals("00000012", new BigDecimalBridge().objectToString(new BigDecimal(".12")));
        assertEquals("00009800", new BigDecimalBridge().objectToString(new BigDecimal("98")));


    }

}
