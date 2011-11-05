package org.yes.cart.service.domain.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.context.ApplicationContext;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 17:18:07
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        Test_AddressServiceImpl.class,
        Test_AssociationServiceImpl.class,
        Test_AttributeServiceImpl.class,
        Test_CarrierSlaServiceImpl.class,
        Test_CustomerOrderServiceImpl.class,
        Test_CustomerServiceImpl.class,
        Test_CustomerWishListServiceImpl.class,
        Test_SkuWarehouseServiceImpl.class,
        TestAttributeServiceImpl.class,
        TestCategoryServiceImpl.class,
        TestPriceServiceImpl.class,
        TestProductServiceImpl.class,
        TestShopServiceImpl.class,
        TestWarehouseServiceImpl.class
})
public class DomainTestSuite {

    static ApplicationContext sharedContext = null;

}
