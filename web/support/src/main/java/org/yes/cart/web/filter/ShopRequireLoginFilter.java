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
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.support.CartDetuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizer;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 12/01/2017
 * Time: 07:21
 */
public class ShopRequireLoginFilter extends AbstractFilter implements Filter {

    private String loginOrRegistrationUri;

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {

        final Shop shop = ApplicationDirector.getCurrentShop();
        if (shop.isSfRequireCustomerLogin()) {
            final ShoppingCart cart = ApplicationDirector.getShoppingCart();
            if (cart.getLogonState() != ShoppingCart.LOGGED_IN) {
                ((HttpServletResponse) response).sendRedirect(
                        ((HttpServletRequest) request).getContextPath() + this.loginOrRegistrationUri);
                return null;
            }
        }

        return request;
    }


    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        // Nothing
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (StringUtils.isNotBlank(filterConfig.getInitParameter("loginOrRegistrationUri"))) {
            this.loginOrRegistrationUri = filterConfig.getInitParameter("loginOrRegistrationUri");
        } else {
            this.loginOrRegistrationUri = "/login";
        }
    }

}
