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

package org.yes.cart.web.application;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShoppingCartContext;

import java.util.List;

/**
 * Storefront  director class responsible for data caching,
 * common used operations, etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 7:11 PM
 */

public class ApplicationDirector {

    private static final ThreadLocal<String> domainThreadLocal = new ThreadLocal<String>();
    private static final ThreadLocal<Shop> shopThreadLocal = new ThreadLocal<Shop>();
    private static final ThreadLocal<Shop> customerShopThreadLocal = new ThreadLocal<Shop>();
    private static final ThreadLocal<List<String>> currentThemeChainThreadLocal = new ThreadLocal<List<String>>();
    private static final ThreadLocal<String> shopperIPAddressThreadLocal = new ThreadLocal<String>();

    /**
     * Get shopper ip address.
     * @return shopper ip address
     */
    public static String getShopperIPAddress() {
        return shopperIPAddressThreadLocal.get();
    }

    /**
     * Set shopper ip address
     * @param shopperIPAddress shopper ip address
     */
    public static void setShopperIPAddress(final String shopperIPAddress) {
        shopperIPAddressThreadLocal.set(shopperIPAddress);
    }


    /**
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentShop() {
        return shopThreadLocal.get();
    }


    /**
     * Set {@link Shop} instance to current thread.
     *
     * @param currentShop current shop.
     */
    public static void setCurrentShop(final Shop currentShop) {
        shopThreadLocal.set(currentShop);
    }

    /**
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentCustomerShop() {

        return customerShopThreadLocal.get();

    }

    /**
     * Set {@link Shop} instance to current thread.
     *
     * @param currentShop current shop.
     */
    public static void setCurrentCustomerShop(final Shop currentShop) {
        customerShopThreadLocal.set(currentShop);
    }

    /**
     * @return current shop's theme
     */
    public static List<String> getCurrentThemeChain() {

        return currentThemeChainThreadLocal.get();

    }

    /**
     * Set current shop's theme
     *
     * @param themeChain current shop's theme.
     */
    public static void setCurrentThemeChain(final List<String> themeChain) {

        currentThemeChainThreadLocal.set(themeChain);

    }


    /**
     * Get current domain name from local thread.
     *
     * @return {@link Shop} URL domain for this request
     */
    public static String getCurrentDomain() {
        return domainThreadLocal.get();
    }

    /**
     * Set domain name used to access {@link Shop} instance to current thread.
     *
     * @param currentDomain current request domain.
     */
    public static void setCurrentDomain(final String currentDomain) {
        domainThreadLocal.set(currentDomain);
    }


    /**
     * Get shopping cart from local thread storage.
     *
     * @return {@link ShoppingCart}
     */
    public static ShoppingCart getShoppingCart() {
        return ShoppingCartContext.getShoppingCart();
    }

    /**
     * Set shopping cart to storage.
     *
     * @param shoppingCart current cart.
     */
    public static void setShoppingCart(final ShoppingCart shoppingCart) {
        ShoppingCartContext.setShoppingCart(shoppingCart);
    }

    /**
     * Clear thread locals at the end of the request
     */
    public static void clear() {
        domainThreadLocal.set(null);
        shopThreadLocal.set(null);
        customerShopThreadLocal.set(null);
        shopperIPAddressThreadLocal.set(null);
        currentThemeChainThreadLocal.set(null);
        ShoppingCartContext.clear();
    }


}
