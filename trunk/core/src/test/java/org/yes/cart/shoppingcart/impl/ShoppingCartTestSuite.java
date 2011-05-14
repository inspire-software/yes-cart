package org.yes.cart.shoppingcart.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/14/11
 * Time: 4:08 PM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AddSkuToCartEventCommandImplTest.class,
        CartItemImplTest.class,
        ChangeCurrencyEventCommandImplTest.class,
        CleanCartCommandImplTest.class,
        ExpireCartCommandImplTest.class,
        LoginCommandImplTest.class,
        LogoutCommandImplTest.class,
        RemoveAllSkuFromCartCommandImplTest.class,
        RemoveSkuFromCartCommandImplTest.class,
        SetBillingSeparateFromShippingAddressCommandImplTest.class,
        SetCarrierSlaCartCommandImplTest.class,
        SetMultipleDeliveryCommandImplTest.class,
        SetSkuQuantityToCartEventCommandImplTest.class,
        ShoppingCartImplTest.class
})
public class ShoppingCartTestSuite {
}
