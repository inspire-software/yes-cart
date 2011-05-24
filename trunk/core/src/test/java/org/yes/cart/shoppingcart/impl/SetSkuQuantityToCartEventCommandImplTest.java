package org.yes.cart.shoppingcart.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetSkuQuantityToCartEventCommandImplTest extends BaseCoreDBTestCase {

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

    @After
    public void tearDown() {
        shopService = null;
        priceService = null;
        productService = null;
        super.tearDown();
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
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST4");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1");

        SetSkuQuantityToCartEventCommandImpl setSkuQuantityToCartEventCommand = new SetSkuQuantityToCartEventCommandImpl(ctx, params);
        setSkuQuantityToCartEventCommand.execute(shoppingCart);

        assertTrue("Expected 123.00", (new BigDecimal("123.00")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));

        params = new HashMap<String, String>();
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST4");
        params.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "5");
        setSkuQuantityToCartEventCommand = new SetSkuQuantityToCartEventCommandImpl(ctx, params);
        setSkuQuantityToCartEventCommand.execute(shoppingCart);

        assertTrue("Expected 499.55 but got " + shoppingCart.getCartSubTotal(shoppingCart.getCartItemList()),
                (new BigDecimal("499.95")).equals(shoppingCart.getCartSubTotal(shoppingCart.getCartItemList())));
    }
}
