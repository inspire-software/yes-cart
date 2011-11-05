package org.yes.cart.shoppingcart.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
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

    ShopService shopService;
    PriceService priceService;
    ProductService productService;

    @Before
    public void setUp() throws Exception {
        productService = (ProductService) ctx.getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        priceService = (PriceService) ctx.getBean(ServiceSpringKeys.PRICE_SERVICE);
        shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    @Test
    public void testExecute() {

        ShoppingCart shoppingCart = new ShoppingCartImpl();
        new ChangeCurrencyEventCommandImpl(
                ctx,
                Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "EUR")
        ).execute(shoppingCart);

        new SetShopCartCommandImpl(ctx, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);

        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()));

        Map<String, String> params = new HashMap<String, String>();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST2");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "10");

        SetSkuQuantityToCartEventCommandImpl setSkuQuantityToCartEventCommand = new SetSkuQuantityToCartEventCommandImpl(ctx, params);
        setSkuQuantityToCartEventCommand.execute(shoppingCart);

        assertTrue("Expected 221.70 but was " + shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()), (new BigDecimal("221.70")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));

        params = new HashMap<String, String>();
        params.put(RemoveAllSkuFromCartCommandImpl.CMD_KEY, "CC_TEST2");
        RemoveAllSkuFromCartCommandImpl command = new RemoveAllSkuFromCartCommandImpl(ctx, params);

        command.execute(shoppingCart);

        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()));
        assertTrue(shoppingCart.getCartItemList().isEmpty());
    }
}
