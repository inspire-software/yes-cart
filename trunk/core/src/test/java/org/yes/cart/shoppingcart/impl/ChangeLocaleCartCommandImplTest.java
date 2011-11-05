package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.shoppingcart.ShoppingCart;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 11:39 AM
 */
public class ChangeLocaleCartCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeLocaleCartCommandImpl(null, singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, "en"))
                .execute(shoppingCart);
        assertEquals("en", shoppingCart.getCurrentLocale());
        new ChangeLocaleCartCommandImpl(null, singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, "uk"))
                .execute(shoppingCart);
        assertEquals("uk", shoppingCart.getCurrentLocale());
    }
}
