package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerWishListServiceImplTest extends BaseCoreDBTestCase {

    private CustomerWishListService service = null;
    private CustomerService customerService = null;
    private ProductSkuService productSkuService = null;
    private ShopService shopService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        service = (CustomerWishListService) ctx.getBean(ServiceSpringKeys.CUSTOMER_WISH_LIST_SERVICE);
        customerService = (CustomerService) ctx.getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        productSkuService = (ProductSkuService) ctx.getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        shopService = (ShopService)  ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);
    }

    @After
    public void tearDown() {
        service = null;
        customerService = null;
        productSkuService = null;
        shopService = null;
        super.tearDown();
    }

    @Test
    public void testGetByCustomerId() {


        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail("bender@domain.com");
        customer.setFirstname("Bender");
        customer.setLastname("Rodriguez");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue (customer.getCustomerId() > 0);


        Collection<ProductSku> skus = productSkuService.getAllProductSkus(10000L); //SOBOT
        assertNotNull(skus);
        assertEquals(4, skus.size());


        CustomerWishList customerWishList = service.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        customerWishList.setCustomer(customer);
        customerWishList.setSkus(skus.iterator().next());
        customerWishList.setWlType(CustomerWishList.REMIND_WHEN_PRICE_CHANGED);

        service.create(customerWishList);

        List<CustomerWishList> list = service.getByCustomerId(customer.getCustomerId());
        assertEquals(1, list.size());




    }


}
