package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetMultipleDeliveryCommandImplTest {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        assertFalse(shoppingCart.getOrderInfo().isMultipleDelivery());
        Map params = new HashMap();
        params.put(SetMultipleDeliveryCommandImpl.CMD_KEY, "true");
        new SetMultipleDeliveryCommandImpl(null, params).execute(shoppingCart);
        assertTrue(shoppingCart.getOrderInfo().isMultipleDelivery());
        params.put(SetMultipleDeliveryCommandImpl.CMD_KEY, "false");
        new SetMultipleDeliveryCommandImpl(null, params).execute(shoppingCart);
        assertFalse(shoppingCart.getOrderInfo().isMultipleDelivery());
    }
}
