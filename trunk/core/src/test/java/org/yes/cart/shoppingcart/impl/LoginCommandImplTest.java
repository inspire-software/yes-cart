package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LoginCommandImplTest {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        //TODO rethink tis test
        //assertNull(shoppingCart.getAuthentication());
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());
        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, "test@test.com");
        params.put(LoginCommandImpl.NAME, "John Doe");
        new LoginCommandImpl(null, params)
                .execute(shoppingCart);
        //assertNotNull(shoppingCart.getAuthentication());
        assertEquals(ShoppingCart.LOGGED_IN, shoppingCart.getLogonState());
        /*assertEquals("TEst that auth in spring security context",
                SecurityContextHolder.getContext().getAuthentication(),
                shoppingCart.getAuthentication());  */
    }
}
