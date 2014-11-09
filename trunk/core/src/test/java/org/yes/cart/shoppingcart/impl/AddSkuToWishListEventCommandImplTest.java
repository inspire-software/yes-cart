package org.yes.cart.shoppingcart.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 02/06/2014
 * Time: 12:15
 */
public class AddSkuToWishListEventCommandImplTest extends BaseCoreDBTestCase {


    @Test
    public void testExecute() {

        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop = shopService.getById(10L);

        final Customer customer = createCustomer();

        final ProductSkuService skuService = ctx().getBean("productSkuService", ProductSkuService.class);
        final CustomerWishListService customerWishListService = ctx().getBean("customerWishListService", CustomerWishListService.class);

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.getShoppingContext().setShopCode(shop.getCode());
        shoppingCart.getShoppingContext().setShopId(shop.getShopId());
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);


        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());
        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customer.getEmail());
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");
        params.put(LoginCommandImpl.CMD_LOGIN, "1");
        commands.execute(shoppingCart, (Map) params);
        assertEquals(ShoppingCart.LOGGED_IN, shoppingCart.getLogonState());


        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETSHOP, 10));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR"));

        List<CustomerWishList> wishList = customerWishListService.getWishListByCustomerEmail(customer.getEmail());
        assertNotNull(wishList);
        assertTrue(wishList.isEmpty());

        params.clear();

        params.put(ShoppingCartCommand.CMD_ADDTOWISHLIST, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_TAGS, "mine");

        commands.execute(shoppingCart, (Map) params);

        wishList = customerWishListService.getWishListByCustomerEmail(customer.getEmail());
        assertNotNull(wishList);
        assertEquals(1, wishList.size());

        CustomerWishList item1 = wishList.get(0);
        assertEquals(CustomerWishList.PRIVATE, item1.getVisibility());
        assertEquals(CustomerWishList.SIMPLE_WISH_ITEM, item1.getWlType());
        assertEquals(skuService.getProductSkuBySkuCode("CC_TEST1").getSkuId(),  item1.getSkus().getSkuId());
        assertEquals("mine", item1.getTag());
        assertEquals(1, item1.getQuantity().intValue());
        assertEquals("19.99", item1.getRegularPriceWhenAdded().toString());


        params.put(ShoppingCartCommand.CMD_ADDTOWISHLIST, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_TAGS, "mine shared");

        commands.execute(shoppingCart, (Map) params);


        wishList = customerWishListService.getWishListByCustomerEmail(customer.getEmail());
        assertNotNull(wishList);
        assertEquals(1, wishList.size());

        item1 = wishList.get(0);
        assertEquals(CustomerWishList.PRIVATE, item1.getVisibility());
        assertEquals(CustomerWishList.SIMPLE_WISH_ITEM, item1.getWlType());
        assertEquals(skuService.getProductSkuBySkuCode("CC_TEST1").getSkuId(),  item1.getSkus().getSkuId());
        assertEquals("mine shared", item1.getTag());
        assertEquals(2, item1.getQuantity().intValue());
        assertEquals("19.99", item1.getRegularPriceWhenAdded().toString());

    }

}
