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
public class SetBillingSeparateFromShippingAddressCommandImplTest {


    @Test
    public void testExecute() {
        // Add your code here

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        assertFalse(shoppingCart.isSeparateBillingAddress());

        Map params = new HashMap();

        params.put(SetBillingSeparateFromShippingAddressCommandImpl.CMD_KEY, "true");

        SetBillingSeparateFromShippingAddressCommandImpl command =
                new SetBillingSeparateFromShippingAddressCommandImpl(null, params);

        command.execute(shoppingCart);
        assertTrue(shoppingCart.isSeparateBillingAddress());

        params.put(SetBillingSeparateFromShippingAddressCommandImpl.CMD_KEY, "false");
        command =
                new SetBillingSeparateFromShippingAddressCommandImpl(null, params);
        command.execute(shoppingCart);
        assertFalse(shoppingCart.isSeparateBillingAddress());
    }
}
