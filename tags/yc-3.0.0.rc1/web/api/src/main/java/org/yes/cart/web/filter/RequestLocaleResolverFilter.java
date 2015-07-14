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

package org.yes.cart.web.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.application.ApplicationDirector;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Default locale resolution.
 *
 * User: denispavlov
 * Date: 17/03/2015
 * Time: 00:06
 */
public class RequestLocaleResolverFilter extends  AbstractFilter implements Filter, ServletContextAware {

    private final LanguageService languageService;
    private final ShoppingCartCommandFactory cartCommandFactory;

    private ServletContext servletContext;

    public RequestLocaleResolverFilter(final LanguageService languageService,
                                       final ShoppingCartCommandFactory cartCommandFactory) {
        this.languageService = languageService;
        this.cartCommandFactory = cartCommandFactory;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        if (cart != null && StringUtils.isBlank(cart.getCurrentLocale())) {

            final String currentLang;
            final String lang = servletRequest.getLocale().getLanguage();

            final List<String> supported = languageService.getSupportedLanguages(cart.getShoppingContext().getShopCode());
            if (supported.contains(lang)) {

                currentLang = lang;

            } else {

                currentLang = supported.get(0);

            }

            cartCommandFactory.execute(cart, new HashMap<String, Object>() {{
                put(ShoppingCartCommand.CMD_CHANGELOCALE, currentLang);
            }});

        }

        return servletRequest;
    }



    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        // nothing
    }

    /** {@inheritDoc} */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
