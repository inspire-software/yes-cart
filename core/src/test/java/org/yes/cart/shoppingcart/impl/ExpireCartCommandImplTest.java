package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
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

        //assertNull(shoppingCart.getAuthentication());

        Map<String,String> params = new HashMap<String,String>();
        params.put(LoginCommandImpl.EMAIL,"test@test.com");
        params.put(LoginCommandImpl.NAME,"John Doe");

        LoginCommandImpl loginCommand = new LoginCommandImpl(
                null,
                params
                );

        loginCommand.execute(shoppingCart);

        //assertNotNull(shoppingCart.getAuthentication());

        /*assertEquals("TEst that auth in spring security context",
                SecurityContextHolder.getContext().getAuthentication(),
                shoppingCart.getAuthentication());   */

        ExpireCartCommandImpl expireCartCommand = new ExpireCartCommandImpl(null, null);
        expireCartCommand.execute(shoppingCart);

        //assertNull(shoppingCart.getAuthentication());
        assertNull(shoppingCart.getCustomerEmail());        
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        assertNotNull(shoppingCart.getCustomerName());

        //assertEquals(ShoppingCart.SESSION_EXPIRED, shoppingCart.getLogonState());

        //TODO remake test


    }
}
