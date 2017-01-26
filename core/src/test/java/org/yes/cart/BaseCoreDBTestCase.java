/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart;

import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.fail;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class BaseCoreDBTestCase extends AbstractTestDAO {

    private static ApplicationContext sharedContext;

    @Rule
    public TestName testName = new TestName();

    public String getTestName() {
        return this.getClass().getSimpleName() + "." + testName.getMethodName();
    }

    protected synchronized ApplicationContext createContext() {
        if (sharedContext == null) {
            sharedContext = new ClassPathXmlApplicationContext("testApplicationContext.xml");
        }
        return sharedContext;
    }



    @After
    public void tearDown() throws Exception {

        final CacheManager cacheManager = (CacheManager) ctx().getBean("cacheManager");
        for (final String name : cacheManager.getCacheNames()) {
            // clear all cache between the tests
            cacheManager.getCache(name).clear();
        }
        //sharedContext =  null;
    }

    protected ShoppingCart getEmptyCartByPrefix(String prefix) {
        return getEmptyCart(prefix + "jd@domain.com");
    }

    protected ShoppingCart getEmptyCart(String email) {
        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, email);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1-WAREHOUSE_1|1-WAREHOUSE_2|1");

        commands.execute(shoppingCart, (Map) params);

        return shoppingCart;
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCartWithPreorderItems(final String prefix, final int skuCodeSetIdx, final boolean multi) {
        final String [][] skuCodeSet = new String [][] {
                {"PREORDER-BACK-TO-FLOW0", "PREORDER-BACK-TO-FLOW1"},
                {"PREORDER-BACK-TO-FLOW2", "PREORDER-BACK-TO-FLOW3"},
                {"PREORDER-BACK-TO-FLOW4", "PREORDER-BACK-TO-FLOW5"},
                {"BACKORDER-BACK-TO-FLOW1", "BACKORDER-BACK-TO-FLOW2"},
        };
        final String firstCode = skuCodeSet[skuCodeSetIdx][0];
        final String secondCode = skuCodeSet[skuCodeSetIdx][1];
        ShoppingCart shoppingCart = getEmptyCartByPrefix(prefix);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // this digital product available
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, firstCode);
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);
        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, secondCode);
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);

        prepareMultiDeliveriesAndRecalculate(shoppingCart, multi);

        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCart(final boolean multi) {
        String prefix = getTestName();
        ShoppingCart shoppingCart = getEmptyCartByPrefix(prefix);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "200.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "3.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST7");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        // this digital product not available to date
        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST8");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        // this digital product available
        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        prepareMultiDeliveriesAndRecalculate(shoppingCart, multi);

        return shoppingCart;
    }

    protected ShoppingCart getShoppingCart2(final String customerEmail, final boolean multi) {
        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customerEmail);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "rawpassword");


        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1-WAREHOUSE_1|1-WAREHOUSE_2|1");

        commands.execute(shoppingCart, (Map) params);

        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));

        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST4");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST5");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "200.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST6");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "3.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST7");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST8");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, "CC_TEST9");
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);

        prepareMultiDeliveriesAndRecalculate(shoppingCart, multi);

        return shoppingCart;
    }


    protected void prepareDeliveriesAndRecalculate(ShoppingCart shoppingCart) {

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_SPLITCARTITEMS, ShoppingCartCommand.CMD_SPLITCARTITEMS);

        commands.execute(shoppingCart, (Map) params);

    }

    protected void prepareMultiDeliveriesAndRecalculate(ShoppingCart shoppingCart, boolean multi) {

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, Boolean.valueOf(multi).toString());

        commands.execute(shoppingCart, (Map) params);

    }

    protected Customer createCustomer() {
        return createCustomer("");
    }

    protected Customer createCustomer2() {
        return createCustomer("2");
    }

    protected Customer createCustomerB2B() {
        return createCustomerB2B(true, false);
    }

    protected Customer createCustomerB2B(boolean requireApproveOrder, boolean blockCheckout) {
        return createCustomerB2B("", requireApproveOrder, blockCheckout);
    }

    protected Customer createCustomerB2B(String number, boolean requireApproveOrder, boolean blockCheckout) {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        Customer customer = createCustomer("B2B" + number);
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_REF, "REF-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_CHARGE_ID, "CHARGE-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_EMPLOYEE_ID, "EMP-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE, String.valueOf(requireApproveOrder));
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.BLOCK_CHECKOUT, String.valueOf(blockCheckout));
        customerService.update(customer);
        customer = customerService.getCustomerByEmail(customer.getEmail(), shopService.getById(10L));
        return customer;
    }

    protected Customer createCustomerB2BSub(boolean requireApproveOrder, boolean blockCheckout) {
        return createCustomerB2BSub("", requireApproveOrder, blockCheckout);
    }

    protected Customer createCustomerB2BSub(String number, boolean requireApproveOrder, boolean blockCheckout) {
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        Customer customer = createCustomer("B2B" + number, 1010L);
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_REF, "REF-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_CHARGE_ID, "CHARGE-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_EMPLOYEE_ID, "EMP-001");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE, String.valueOf(requireApproveOrder));
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.BLOCK_CHECKOUT, String.valueOf(blockCheckout));
        customerService.update(customer);
        customer = customerService.getCustomerByEmail(customer.getEmail(), shopService.getById(1010L));
        return customer;
    }

    protected Customer createCustomer(String number) {
        return createCustomer(number, 10L);
    }

    protected Customer createCustomer(String number, long shopId) {
        String prefix = getTestName() + number;
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        AddressService addressService = (AddressService) ctx().getBean(ServiceSpringKeys.ADDRESS_SERVICE);
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(prefix + "jd@domain.com");
        customer.setFirstname(prefix + "John");
        customer.setLastname(prefix + "Doe");
        customer.setPassword("rawpassword");
        customerService.addAttribute(customer, AttributeNamesKeys.Customer.CUSTOMER_PHONE, "555-55-51");
        final Shop shop = shopService.getById(shopId);
        customer = customerService.create(customer, shop);
        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCity("Vancouver");
        address.setAddrline1("line1");
        address.setAddrline2("shipping addr");
        address.setCountryCode("CA");
        address.setAddressType(Address.ADDR_TYPE_SHIPPING);
        address.setCustomer(customer);
        address.setPhone1("555-55-51");
        addressService.create(address);
        address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCity("Vancouver");
        address.setAddrline1("line1");
        address.setAddrline2("billing addr");
        address.setCountryCode("CA");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);
        address.setPhone1("555-55-52");
        addressService.create(address);
//        customer = customerService.getCustomerByEmail("jd@domain.com");
        //customer = customerDao.findSingleByCriteria(Restrictions.eq("email", prefix + "jd@domain.com"));
        customer = customerService.getCustomerByEmail(prefix + "jd@domain.com", shop);
        return customer;
    }


    protected void clearCache() {
        for (Cache cache : getCacheMap().values()) {
            cache.clear();
        }
    }

    protected Map<String, Cache> getCacheMap() {
        final CacheManager cm = ctx().getBean("cacheManager", CacheManager.class);
        final Collection<String> cacheNames = cm.getCacheNames();
        final Map<String, Cache> cacheMap = new HashMap<String, Cache> (cacheNames.size());
        for (String cacheName : cacheNames) {

            cacheMap.put(cacheName, cm.getCache(cacheName));

        }

        return cacheMap;
    }
}
