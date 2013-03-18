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

package org.yes.cart.web.support.util.cookie.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.ShoppingCartPersister;
import org.yes.cart.web.support.util.cookie.UnableToCookielizeObjectException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/14/11
 * Time: 9:04 PM
 */
public class ShoppingCartPersisterImpl implements ShoppingCartPersister {

    private final CookieTuplizer cookieTuplizer;

    /**
     * Construct shopping cart persister phaze listener
     *
     * @param cookieTuplizer tuplizer to use
     */
    public ShoppingCartPersisterImpl(final CookieTuplizer cookieTuplizer) {
        this.cookieTuplizer = cookieTuplizer;
    }


    /**
     * {@inheritDoc}
     */
    public void persistShoppingCart(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final ShoppingCart shoppingCart) {
        final Cookie[] oldCookies = httpServletRequest.getCookies();
        try {
            final Cookie[] cookies = cookieTuplizer.toCookies(oldCookies, shoppingCart);
            for (Cookie cookie : cookies) {
                httpServletResponse.addCookie(cookie);
            }
        } catch (UnableToCookielizeObjectException e) {
            ShopCodeContext.getLog().error(MessageFormat.format("Unable to create cookies from {0} cart", shoppingCart), e);
        }

    }

}
