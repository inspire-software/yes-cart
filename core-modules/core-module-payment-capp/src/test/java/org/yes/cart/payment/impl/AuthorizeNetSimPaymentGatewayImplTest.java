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
        assertEquals(
                "<input type='hidden' name='qwerty' value='1234567890'>\n",
                gateway.getHiddenFiled("qwerty", "1234567890")
        );
        assertEquals(
                "<input type='hidden' name='qwerty' value='9223372036854775807'>\n",
                gateway.getHiddenFiled("qwerty", Long.MAX_VALUE)
        );
        assertEquals(
                "<input type='hidden' name='qwerty' value='922337203685477.58'>\n",
                gateway.getHiddenFiled("qwerty", new BigDecimal("922337203685477.58"))
        );
    }
}
