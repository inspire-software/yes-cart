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

package org.yes.cart.web.service.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.ro.TokenRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.shoppingcart.support.ShoppingCartPersister;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 08:42
 */
@Service("restCartMixin")
public class CartMixin {

    @Autowired
    @Qualifier("shoppingCartPersister")
    private ShoppingCartPersister shoppingCartPersister;


    /**
     * Retrieve current cart.
     *
     * @return cart object
     */
    public ShoppingCart getCurrentCart() {
        return ApplicationDirector.getShoppingCart();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop object
     */
    public Shop getCurrentShop() {
        return ApplicationDirector.getCurrentShop();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop object
     */
    public Shop getCurrentCustomerShop() {
        return ApplicationDirector.getCurrentCustomerShop();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop PK
     */
    public long getCurrentShopId() {
        return getCurrentCart().getShoppingContext().getShopId();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop code
     */
    public String getCurrentShopCode() {
        return getCurrentCart().getShoppingContext().getShopCode();
    }


    /**
     * Retrieve current shop.
     *
     * @return shop PK
     */
    public long getCurrentCustomerShopId() {
        return getCurrentCart().getShoppingContext().getCustomerShopId();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop code
     */
    public String getCurrentCustomerShopCode() {
        return getCurrentCart().getShoppingContext().getCustomerShopCode();
    }


    /**
     * Generate token ro for current state of the cart.
     *
     * @return token
     */
    public TokenRO persistShoppingCart(final HttpServletRequest request, final HttpServletResponse response) {

        final ShoppingCart cart = getCurrentCart();

        // ALWAYS persist as we are using token based persistence and we need it in REST response
        shoppingCartPersister.persistShoppingCart(request, response, cart);

        return new TokenRO(cart.getGuid());

    }

    /**
     * Simple login check on cart object.
     *
     * @throws org.springframework.security.core.AuthenticationException thrown if user is not logged in or login expired
     */
    public void throwSecurityExceptionIfNotLoggedIn() throws AuthenticationException {

        final int state = getCurrentCart().getLogonState();
        if (state != ShoppingCart.LOGGED_IN) {
            if (state == ShoppingCart.SESSION_EXPIRED) {
                throw new CredentialsExpiredException("Session expired");
            }
            throw new BadCredentialsException("User not logged in");
        }

    }

    /**
     * Check if shop has login required setting and then perform simple login check on cart object.
     * If login is not required by shop then no check is made
     *
     * @throws org.springframework.security.core.AuthenticationException thrown if user is not logged in or login expired
     */
    public void throwSecurityExceptionIfRequireLoggedIn() throws AuthenticationException {

        if (getCurrentShop().isSfRequireCustomerLogin()) {
            throwSecurityExceptionIfNotLoggedIn();
        }

    }

}
