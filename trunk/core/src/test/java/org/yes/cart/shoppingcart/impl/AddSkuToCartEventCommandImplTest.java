package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AddSkuToCartEventCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeCurrencyEventCommandImpl(ctx, singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR"))
                .execute(shoppingCart);
        new SetShopCartCommandImpl(ctx, singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()));
        Map<String, String> params = new HashMap<String, String>();
        params.put(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1");
        AddSkuToCartEventCommandImpl command;
        command = new AddSkuToCartEventCommandImpl(ctx, params);
        command.execute(shoppingCart);
        assertTrue("Expected 19.99", (new BigDecimal("19.99")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));
        command = new AddSkuToCartEventCommandImpl(ctx, params);
        command.execute(shoppingCart);
        assertTrue("Expected 39.98", (new BigDecimal("39.98")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));
        command = new AddSkuToCartEventCommandImpl(ctx, params);
        command.execute(shoppingCart);
        assertTrue("Expected 57.00", (new BigDecimal("57.00")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));
    }
}