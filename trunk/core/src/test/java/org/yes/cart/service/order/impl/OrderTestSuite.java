package org.yes.cart.service.order.impl;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/14/11
 * Time: 3:04 PM
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DefaultOrderNumberGeneratorImplTest.class,
        DeliveryAssemblerImplTest.class,
        OrderAssemblerImplTest.class,
        OrderStateManagerImplTest.class

})
public class OrderTestSuite {
}
