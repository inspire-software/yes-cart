/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
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

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerWishListServiceImplTest extends BaseCoreDBTestCase {

    private CustomerWishListService service;
    private CustomerService customerService;
    private ProductSkuService productSkuService;
    private ShopService shopService;

    @Before
    public void setUp() {
        service = (CustomerWishListService) ctx().getBean(ServiceSpringKeys.CUSTOMER_WISH_LIST_SERVICE);
        customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetByCustomerId() {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail("bender001@domain.com");
        customer.setFirstname("Bender001");
        customer.setLastname("Rodriguez001");
        customer.setPassword("rawpassword");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
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
