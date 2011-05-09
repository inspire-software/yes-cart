package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.domain.dto.ShoppingCart;

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
        // Add your code here

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        assertFalse(shoppingCart.isMultipleDelivery());

        Map params = new HashMap();

        params.put(SetMultipleDeliveryCommandImpl.CMD_KEY, "true");

        SetMultipleDeliveryCommandImpl command =
                new SetMultipleDeliveryCommandImpl(null, params);

        command.execute(shoppingCart);
        assertTrue(shoppingCart.isMultipleDelivery());

        params.put(SetMultipleDeliveryCommandImpl.CMD_KEY, "false");
        command =
                new SetMultipleDeliveryCommandImpl(null, params);
        command.execute(shoppingCart);
        assertFalse(shoppingCart.isMultipleDelivery());


    }

}
