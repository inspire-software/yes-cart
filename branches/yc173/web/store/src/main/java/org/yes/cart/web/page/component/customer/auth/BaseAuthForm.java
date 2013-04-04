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

package org.yes.cart.web.page.component.customer.auth;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 3:29 PM
 */
public class BaseAuthForm extends Form {

    protected static final int MIN_LEN = 6;
    protected static final int MAX_LEN = 256;


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ATTRIBUTE_SERVICE)
    private AttributeService attributeService;

    //@SpringBean(name = WebParametersKeys.SESSION_OBJECT_NAME)
    //private RequestRuntimeContainer requestRuntimeContainer;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    public BaseAuthForm(String id) {
        super(id);
    }


    /**
     * Check is customer already registered.
     *
     * @param customerEmail email to check
     * @return true in case if email unique.
     */
    protected boolean isCustomerExists(final String customerEmail) {
        return customerService.isCustomerExists(customerEmail);
    }

    /**
     * Check is provided password for customer valid.
     *
     * @param customerEmail email to check
     * @param password      password
     * @return true in case if email unique.
     */
    protected boolean isPasswordValid(final String customerEmail, final String password) {
        return customerService.isPasswordValid(customerEmail, password);
    }


    /**
     * Get {@link CustomerService} service.
     *
     * @return {@link CustomerService} service
     */
    protected CustomerService getCustomerService() {
        return customerService;
    }

    /**
     * Get attribute service.
     *
     * @return {@link AttributeService}
     */
    protected AttributeService getAttributeService() {
        return attributeService;
    }



    /**
     * Sign in user if possible.
     *
     * @param username The username
     * @param password The password
     * @return True if signin was successful
     */
    protected boolean signIn(final String username, final String password) {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

}