/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 3:29 PM
 */
public class BaseAuthForm extends Form<Object> {

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    public BaseAuthForm(String id) {
        super(id);
    }


    /**
     * Check is customer already registered.
     *
     * @param login login to check
     * @return true in case if email unique.
     */
    protected boolean isCustomerExists(final String login) {
        return customerServiceFacade.isCustomerRegistered(ApplicationDirector.getCurrentShop(), login);
    }

    /**
     * Get {@link CustomerServiceFacade} service.
     *
     * @return {@link CustomerServiceFacade} service
     */
    protected CustomerServiceFacade getCustomerServiceFacade() {
        return customerServiceFacade;
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