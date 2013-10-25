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

package org.yes.cart.web.application;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 9:49 PM
 */
public class StorefrontWebSession extends AuthenticatedWebSession {

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = "languageService")
    private LanguageService languageService;

    /**
     * Construct.
     *
     * @param request The current request object
     */
    public StorefrontWebSession(Request request) {
        super(request);
        ((StorefrontApplication) getApplication()).getSpringComponentInjector().inject(this);   // allow to use @SpringBean
    }

    /**
     * {@inheritDoc}
     */
    public boolean authenticate(final String username, final String password) {
        if (customerServiceFacade.authenticate(username, password)) {
            executeLoginCommand(
                    ApplicationDirector.getShoppingCart(),
                    customerServiceFacade.findCustomer(username));
            return true;
        }
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGOUT, ApplicationDirector.getShoppingCart(),
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT));
        return false;
    }


    /**
     * Execute login command.
     *
     * @param shoppingCart shopping cart
     * @param customer     customer.
     */
    protected void executeLoginCommand(final ShoppingCart shoppingCart, final Customer customer) {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGIN, shoppingCart,
                new HashMap<String, Object>() {{
                    put("email", customer.getEmail());
                    put("name", customer.getFirstname() + " " + customer.getLastname());
                    put(ShoppingCartCommand.CMD_LOGIN, ShoppingCartCommand.CMD_LOGIN);
                }}
        );
    }


    /**
     * {@inheritDoc}
     */
    public Roles getRoles() {
        if (isSignedIn()) {
            return new Roles(Roles.USER);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        final Locale locale = super.getLocale();
        if (locale == null) {
            return new Locale(languageService.getSupportedLanguages(ShopCodeContext.getShopCode()).get(0));
        }
        return locale;
    }

    /** {@inheritDoc} */
    @Override
    public void setLocale(final Locale locale) {
        final List<String> supported = languageService.getSupportedLanguages(ShopCodeContext.getShopCode());
        if (supported.contains(locale.getLanguage())) {
            super.setLocale(locale);
        } else {
            super.setLocale(new Locale(supported.get(0)));
        }
    }
}
