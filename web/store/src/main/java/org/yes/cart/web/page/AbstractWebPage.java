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

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.i18n.I18NWebSupport;
import org.yes.cart.web.i18n.impl.I18NWebSupportImpl;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.LanguageService;
import org.yes.cart.web.support.util.cookie.ShoppingCartPersister;
import org.yes.cart.web.util.WicketUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Locale;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:22 AM
 */
public class AbstractWebPage extends WebPage {

    public static final String FEEDBACK = "feedback";

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CART_PERSISTER)
    private ShoppingCartPersister shoppingCartPersister;

    @SpringBean(name = StorefrontServiceSpringKeys.LANGUAGE_SERVICE)
    private LanguageService languageService;




    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AbstractWebPage(final PageParameters params) {
        super(params);

        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();

        if (getSession().getLocale() == null) {
            getSession().setLocale(new Locale(languageService.getSupportedLanguages().get(0)));
        }

        if(StringUtils.isBlank(shoppingCart.getCurrentLocale())) {
            new ChangeLocaleCartCommandImpl(
                    null,
                    Collections.singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, getSession().getLocale().getLanguage()))
                    .execute(shoppingCart);
        }

        getSession().setLocale(new Locale(shoppingCart.getCurrentLocale()));

        setStatelessHint(true);

    }



    /**
     * {@inheritDoc}
     */
    public void processCommands() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        if (cart.accept(
                getShoppingCartCommandFactory().create(WicketUtil.pageParametesAsMap(getPageParameters()))
        ) || needToPersists(cart)) {
            //Not sure what the best way to apply changed locale
            if (cart.getCurrentLocale() != null && !getSession().getLocale().getLanguage().equals(cart.getCurrentLocale())) {
                getSession().setLocale(new Locale(cart.getCurrentLocale()));
            }
            getShoppingCartPersister().persistShoppingCart(
                    (HttpServletRequest) getRequest().getContainerRequest(),
                    (HttpServletResponse) getResponse().getContainerResponse(),
                    cart
            );
        }
        super.onBeforeRender();

    }

    private boolean needToPersists(final ShoppingCart shoppingCart) {
       return (shoppingCart.getProcessingStartDate().getTime() <= shoppingCart.getModifiedDate().getTime());
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartCommandFactory getShoppingCartCommandFactory() {
        return shoppingCartCommandFactory;
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartPersister getShoppingCartPersister() {
        return shoppingCartPersister;
    }


    private static final I18NWebSupport SUPPORT = new I18NWebSupportImpl();

    /**
     * @return I18n support object
     */
    public I18NWebSupport getI18NSupport() {
        return SUPPORT;
    }



}
