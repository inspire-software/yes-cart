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

package org.yes.cart.web.service.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.ro.AddressRO;
import org.yes.cart.domain.ro.RegisterRO;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 11:41
 */
public abstract class AbstractSuiteTest extends AbstractTestDAO {


    @Resource(name = "shopResolverFilter")
    private Filter shopResolverFilter;

    @Resource(name = "shoppingCartFilter")
    private Filter shoppingCartFilter;

    @Resource(name = "requestLocaleResolverFilter")
    private Filter requestLocaleResolverFilter;

    @Resource
    private WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @Override
    protected ApplicationContext createContext() {
        return webApplicationContext;
    }

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(shopResolverFilter)
                .addFilter(shoppingCartFilter)
                .addFilter(requestLocaleResolverFilter)
                .build();

    }


    /**
     * Perform reindex.
     */
    protected void reindex() {
        PlatformTransactionManager transactionManager =   ctx().getBean("transactionManager", PlatformTransactionManager.class);
        TransactionTemplate tx = new TransactionTemplate(transactionManager);


        tx.execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFullTextSearchCapableDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO)).fullTextSearchReindex(false, 1000);

            }
        });
    }

    /**
     * Map object to JSON bytes
     *
     * @param object object to map
     * @return bytes for request body
     *
     * @throws Exception
     */
    protected byte[] toJsonBytes(final Object object) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }


    /**
     * JSON bytes for registration request object
     *
     * @param email customer email
     * @return bytes for request body
     *
     * @throws Exception
     */
    protected byte[] toJsonBytesRegistrationDetails(final String email) throws Exception {

        final RegisterRO register = new RegisterRO();
        register.setEmail(email);
        register.setCustomerType(AttributeNamesKeys.Cart.CUSTOMER_TYPE_REGULAR);
        register.setCustom(new HashMap<String, String>());
        register.getCustom().put("firstname", "Bob");
        register.getCustom().put("lastname", "Doe");
        register.getCustom().put("CUSTOMER_PHONE", "123123123");

        return toJsonBytes(register);

    }

    /**
     * JSON bytes for registration request object
     *
     * @param email customer email
     * @return bytes for request body
     *
     * @throws Exception
     */
    protected byte[] toJsonBytesSubRegistrationDetails(final String email) throws Exception {

        final RegisterRO register = new RegisterRO();
        register.setEmail(email);
        register.setCustomerType(AttributeNamesKeys.Cart.CUSTOMER_TYPE_REGULAR);
        register.setOrganisation("Sub Gadget universe");
        register.setCustom(new HashMap<String, String>());
        register.getCustom().put("firstname", "Bob");
        register.getCustom().put("lastname", "Doe");
        register.getCustom().put("CUSTOMER_PHONE", "123123123");

        return toJsonBytes(register);

    }

    /**
     * JSON bytes for registration request object
     *
     * @param email customer email
     * @return bytes for request body
     *
     * @throws Exception
     */
    protected byte[] toJsonBytesGuestDetails(final String email) throws Exception {

        final RegisterRO register = new RegisterRO();
        register.setEmail(email);
        register.setCustomerType(AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST);
        register.setCustom(new HashMap<String, String>());
        register.getCustom().put("firstname", "Bob");
        register.getCustom().put("lastname", "Doe");

        return toJsonBytes(register);

    }


    /**
     * JSON bytes for address request object
     *
     * @param state state code
     * @param country country code
     * @return bytes for request body
     *
     * @throws Exception
     */
    protected byte[] toJsonBytesAddressDetails(final String state, final String country) throws Exception {

        final AddressRO address = new AddressRO();
        address.setAddrline1("In the middle of");
        address.setCity("Nowhere");
        address.setPostcode("0001");
        address.setStateCode(state);
        address.setCountryCode(country);

        return toJsonBytes(address);

    }




}
