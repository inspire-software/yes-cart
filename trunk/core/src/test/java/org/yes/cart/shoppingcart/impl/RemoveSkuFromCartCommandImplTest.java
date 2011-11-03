package org.yes.cart.shoppingcart.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveSkuFromCartCommandImplTest extends BaseCoreDBTestCase {
    ShopService shopService = null;
    PriceService priceService = null;
    ProductService productService = null;


    @Before
    public void setUp() throws Exception {
        super.setUp();
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
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST3");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "3");

        SetSkuQuantityToCartEventCommandImpl setSkuQuantityToCartEventCommand = new SetSkuQuantityToCartEventCommandImpl(ctx, params);
        setSkuQuantityToCartEventCommand.execute(shoppingCart);

        assertTrue("Expected 21.00 but was " + shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()), (new BigDecimal("21.00")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));

        params = new HashMap<String, String>();
        params.put(RemoveSkuFromCartCommandImpl.CMD_KEY, "CC_TEST3");
        RemoveSkuFromCartCommandImpl command = new RemoveSkuFromCartCommandImpl(ctx, params);

        command.execute(shoppingCart);

        assertTrue("Expected 15.98 but was " + shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()), (new BigDecimal("15.98")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));
        assertEquals(2, shoppingCart.getCartItemsCount());


    }
}
