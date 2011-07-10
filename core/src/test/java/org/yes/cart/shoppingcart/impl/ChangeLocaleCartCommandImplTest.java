package org.yes.cart.shoppingcart.impl;

import org.junit.After;
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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 11:39 AM
 */
public class ChangeLocaleCartCommandImplTest   extends BaseCoreDBTestCase {


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
        new ChangeLocaleCartCommandImpl(
                ctx,
                Collections.singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, "en")
                ).execute(shoppingCart);

        assertEquals("en", shoppingCart.getCurrentLocale());


        new ChangeLocaleCartCommandImpl(
                ctx,
                Collections.singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, "uk")
                ).execute(shoppingCart);

        assertEquals("uk", shoppingCart.getCurrentLocale());

    }


}
