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
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

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
            sharedContext = new ClassPathXmlApplicationContext(
                    "testApplicationContext.xml",
                    "core-services.xml",
                    "core-aspects.xml",
                    "test-core-module-payment-base.xml",
                    "test-payment-api.xml");
        }
        return sharedContext;
    }



    @After
    public void tearDown() throws Exception {
        //sharedContext =  null;
    }

    protected ShoppingCart getEmptyCartByPrefix(String prefix) {
        return getEmptyCart(prefix + "jd@domain.com");
    }

    protected ShoppingCart getEmptyCart(String email) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, email);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_NAME, "John Doe");

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1");

        commands.execute(shoppingCart, (Map) params);

        return shoppingCart;
    }

    /**
     * @return cart with one digital available product.
     */
    protected ShoppingCart getShoppingCartWithPreorderItems(final String prefix, final int skuCodeSetIdx) {
        final String [][] skuCodeSet = new String [][] {
                {"PREORDER-BACK-TO-FLOW0", "PREORDER-BACK-TO-FLOW1"},
                {"PREORDER-BACK-TO-FLOW2", "PREORDER-BACK-TO-FLOW3"},
                {"PREORDER-BACK-TO-FLOW4", "PREORDER-BACK-TO-FLOW5"},
        };
        final String firstCode = skuCodeSet[skuCodeSetIdx][0];
        final String secondCode = skuCodeSet[skuCodeSetIdx][1];
        ShoppingCart shoppingCart = getEmptyCartByPrefix(prefix);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        // this digital product available
        Map<String, String> param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, firstCode/*firstSet ? "PREORDER-BACK-TO-FLOW0" : "PREORDER-BACK-TO-FLOW2"*/);
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "1.00");
        commands.execute(shoppingCart, (Map) param);
        param = new HashMap<String, String>();
        param.put(ShoppingCartCommand.CMD_SETQTYSKU, secondCode /*firstSet ? "PREORDER-BACK-TO-FLOW1" : "PREORDER-BACK-TO-FLOW3"*/);
        param.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, "2.00");
        commands.execute(shoppingCart, (Map) param);
        return shoppingCart;
    }

    /**
     * Get product with standard availability, back order and pre order availability both have inventory.
     * Ordered qty more than qty on warehouses, so one "wait inventory " delivery will be planned.
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCart() {
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

        return shoppingCart;
    }

    protected ShoppingCart getShoppingCart2(String customerEmail) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customerEmail);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_NAME, "John Doe");


        params.put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1");

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

        return shoppingCart;
    }

    protected Customer createCustomer() {
        return createCustomer("");
    }

    protected Customer createCustomer2() {
        return createCustomer("2");
    }

    protected Customer createCustomer(String number) {
        String prefix = getTestName() + number;
        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        CustomerService customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        AttributeService attributeService = (AttributeService) ctx().getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);
        AddressService addressService = (AddressService) ctx().getBean(ServiceSpringKeys.ADDRESS_SERVICE);
        GenericDAO<Customer, Long> customerDao = (GenericDAO<Customer, Long>) ctx().getBean("customerDao");
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(prefix + "jd@domain.com");
        customer.setFirstname(prefix + "John");
        customer.setLastname(prefix + "Doe");
        customer.setPassword("rawpassword");
        //AttrValueCustomer attrValueCustomer = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
        //attrValueCustomer.setCustomer(customer);
        //attrValueCustomer.setVal("555-55-51");
        //attrValueCustomer.setAttribute(attributeService.findByAttributeCode(AttributeNamesKeys.CUSTOMER_PHONE));
        //customer.getAttribute().add(attrValueCustomer);
        customerService.addAttribute(customer, AttributeNamesKeys.CUSTOMER_PHONE, "555-55-51");
        customer = customerService.create(customer, shopService.getById(10L));
        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCity("Vancouver");
        address.setAddrline1("line1");
        address.setAddrline2("shipping addr");
        address.setCountryCode("CA");
        address.setAddressType(Address.ADDR_TYPE_SHIPING);
        address.setCustomer(customer);
        address.setPhoneList("555-55-51");
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
        address.setPhoneList("555-55-52");
        addressService.create(address);
//        customer = customerService.getCustomerByEmail("jd@domain.com");
        //customer = customerDao.findSingleByCriteria(Restrictions.eq("email", prefix + "jd@domain.com"));
        customer = customerService.getCustomerByEmail(prefix + "jd@domain.com");
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
