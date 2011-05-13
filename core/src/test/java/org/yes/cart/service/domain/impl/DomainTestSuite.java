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
        AddressServiceImplTest.class,
        AssociationServiceImplTest.class,
        AttributeServiceImplTest.class,
        CarrierSlaServiceImplTest.class,
        CustomerOrderServiceImplTest.class,
        CustomerServiceImplTest.class,
        CustomerWishListServiceImplTest.class,
        MD5HashHelperImplTest.class,
        ProductSkuServiceImplTest.class,
        SkuWarehouseServiceImplTest.class,
        TestAttributeServiceImpl.class,
        TestCategoryServiceImpl.class,
        TestImageService.class,
        TestPriceServiceImpl.class,
        TestProductAssociationService.class,
        TestProductCategoryServiceImpl.class,
        TestProductServiceImpl.class,
        TestShopServiceImpl.class,
        WarehouseServiceImplTest.class
})
public class DomainTestSuite {

    static ApplicationContext ctx2 = null;

}
