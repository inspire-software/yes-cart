package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveAllSkuFromCartCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeCurrencyEventCommandImpl(ctx(), Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR"))
                .execute(shoppingCart);
        new SetShopCartCommandImpl(ctx(), Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal());
        Map<String, String> params = new HashMap<String, String>();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST2");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "10");
        new SetSkuQuantityToCartEventCommandImpl(ctx(), params).execute(shoppingCart);
        assertTrue("Expected 221.70 but was " + shoppingCart.getCartSubTotal(), (new BigDecimal("221.70")).equals(shoppingCart.getCartSubTotal()));
        params = new HashMap<String, String>();
        params.put(RemoveAllSkuFromCartCommandImpl.CMD_KEY, "CC_TEST2");
        new RemoveAllSkuFromCartCommandImpl(ctx(), params)
                .execute(shoppingCart);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal());
        assertTrue(shoppingCart.getCartItemList().isEmpty());
    }
}
