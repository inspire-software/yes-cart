package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ExpireCartCommandImplTest {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, "test@test.com");
        params.put(LoginCommandImpl.NAME, "John Doe");
        new LoginCommandImpl(null, params).execute(shoppingCart);
        new ExpireCartCommandImpl(null, null)
                .execute(shoppingCart);
        assertNull(shoppingCart.getCustomerEmail());
        assertNull(shoppingCart.getCustomerName());
        //assertEquals(ShoppingCart.SESSION_EXPIRED, shoppingCart.getLogonState());
        //TODO remake test
    }
}
