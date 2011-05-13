package org.yes.cart.service.dto.impl;

import org.springframework.context.ApplicationContext;

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
        DtoAssociationServiceImplTest.class,
        DtoAttributeGroupServiceImplTest.class,
        DtoAttributeServiceImplTest.class,
        DtoAvailabilityServiceImplTest.class,
        DtoBrandServiceImplTest.class,
        DtoCategoryServiceImplTest.class,
        DtoCustomerOrderServiceImplTest.class,
        DtoCustomerServiceImplTest.class,
        DtoEtypeServiceImplTest.class,
        DtoImageServiceImplTest.class,
        DtoProductAssociationServiceImplTest.class,
        DtoProductServiceImplTest.class,
        DtoProductSkuServiceImplTest.class,
        DtoProductTypeAttrServiceImplTest.class,
        DtoSeoServiceImplTest.class,
        DtoWarehouseServiceImplTest.class,
        UserManagmentServiceImplTest.class
})
public class DtoTestSuite {

    //static ApplicationContext sharedContext = null;

}
