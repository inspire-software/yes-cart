package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/6/11
 * Time: 8:30 PM
 */
public class SetPaymentGatewayLabelCommandImplTest {
    /**
     * Test command.
     */
    @Test
    public void testExecute() {

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        assertNull(shoppingCart.getOrderInfo().getPaymentGatewayLabel());

        Map params = new HashMap();

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_KEY, "label");

        SetPaymentGatewayLabelCommandImpl command =
                new SetPaymentGatewayLabelCommandImpl(null, params);

        command.execute(shoppingCart);
        assertEquals("label", shoppingCart.getOrderInfo().getPaymentGatewayLabel());

        params.put(SetPaymentGatewayLabelCommandImpl.CMD_KEY, "qwerty");

        command =
                new SetPaymentGatewayLabelCommandImpl(null, params);

        command.execute(shoppingCart);
        assertEquals("qwerty", shoppingCart.getOrderInfo().getPaymentGatewayLabel());

    }
}
