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

package org.yes.cart.payment.impl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AuthorizeNetSimPaymentGatewayImplTest {

    @Test
    public void testGetHiddenFiled() {
        AuthorizeNetSimPaymentGatewayImpl gateway = new AuthorizeNetSimPaymentGatewayImpl();
        assertEquals("<input type='hidden' name='qwerty' value='1234567890'>\n",
                gateway.getHiddenField("qwerty", "1234567890"));
        assertEquals("<input type='hidden' name='qwerty' value='9223372036854775807'>\n",
                gateway.getHiddenField("qwerty", Long.MAX_VALUE));
        assertEquals("<input type='hidden' name='qwerty' value='922337203685477.58'>\n",
                gateway.getHiddenField("qwerty", new BigDecimal("922337203685477.58")));
    }
}
