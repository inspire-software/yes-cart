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

package org.yes.cart.web.support.shoppingcart.tokendriven.impl;

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.CartDetuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizer;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 23:14
 */
public class TokenTuplizerImpl implements CartTuplizer {

    private final TuplizerSetting tuplizerSetting;

    private final CartRepository cartRepository;


    /**
     * Default Constructor.
     *
     * @param cartRepository       cart repository
     * @param cookieName           cookie prefix
     * @param expirySeconds        seconds after which cookie expires
     * @param cookiePath           path for which the cookie will be saved
     */
    public TokenTuplizerImpl(final CartRepository cartRepository,
                             final String cookieName,
                             final int expirySeconds,
                             final String cookiePath) {
        this.cartRepository = cartRepository;

        this.tuplizerSetting = new TuplizerSetting(cookieName, expirySeconds, cookiePath);

        if (this.tuplizerSetting.key == null || this.tuplizerSetting.expiry == null) {
            final String errMsg = "cookie tuplizer misconfigured";
            ShopCodeContext.getLog(this).error(errMsg);
            throw new RuntimeException(errMsg);
        }

    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader(tuplizerSetting.key) != null) {
            return true;
        }
        final Cookie[] cookies = httpServletRequest.getCookies();
        for (final Cookie cookie : cookies) {
            if (cookie.getName().equals(tuplizerSetting.key)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart detuplize(final HttpServletRequest httpServletRequest) throws CartDetuplizationException {

        String token = httpServletRequest.getHeader(tuplizerSetting.key);
        if (token == null) {
            final Cookie[] cookies = httpServletRequest.getCookies();
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(tuplizerSetting.key)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {

            return cartRepository.getShoppingCart(token);

        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void tuplize(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final ShoppingCart shoppingCart) throws CartTuplizationException {

        final String token = this.cartRepository.storeShoppingCart(shoppingCart);

        final Cookie cookie = createNewCookie(tuplizerSetting.key, token, tuplizerSetting.expiry, tuplizerSetting.path);
        httpServletResponse.addCookie(cookie);
        httpServletResponse.addHeader(tuplizerSetting.key, token);

    }


    private Cookie createNewCookie(final String name, final String value, final int maxAgeInSeconds, final String path) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setPath(path);
        cookie.setVersion(1); // allow to have base64 encoded value in cookie
        return cookie;
    }


    /**
     * Convenience class for meta data of object.
     */
    private class TuplizerSetting {

        private final String key;
        private final Integer expiry;
        private final String path;

        TuplizerSetting(final String key, final Integer expiry, final String path) {
            this.key = key;
            this.expiry = expiry;
            this.path = path;
        }
    }


}
