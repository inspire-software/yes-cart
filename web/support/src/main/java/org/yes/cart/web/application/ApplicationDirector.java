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

    private static final ThreadLocal<String> DOMAIN_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Shop> SHOP_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Shop> CUSTOMER_SHOP_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> CURRENT_THEME_CHAIN_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> SHOPPER_IP_ADDRESS_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Get shopper ip address.
     * @return shopper ip address
     */
    public static String getShopperIPAddress() {
        return SHOPPER_IP_ADDRESS_THREAD_LOCAL.get();
    }

    /**
     * Set shopper ip address
     * @param shopperIPAddress shopper ip address
     */
    public static void setShopperIPAddress(final String shopperIPAddress) {
        SHOPPER_IP_ADDRESS_THREAD_LOCAL.set(shopperIPAddress);
    }


    /**
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentShop() {
        return SHOP_THREAD_LOCAL.get();
    }


    /**
     * Set {@link Shop} instance to current thread.
     *
     * @param currentShop current shop.
     */
    public static void setCurrentShop(final Shop currentShop) {
        SHOP_THREAD_LOCAL.set(currentShop);
    }

    /**
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentCustomerShop() {

        return CUSTOMER_SHOP_THREAD_LOCAL.get();

    }

    /**
     * Set {@link Shop} instance to current thread.
     *
     * @param currentShop current shop.
     */
    public static void setCurrentCustomerShop(final Shop currentShop) {
        CUSTOMER_SHOP_THREAD_LOCAL.set(currentShop);
    }

    /**
     * @return current shop's theme
     */
    public static List<String> getCurrentThemeChain() {

        return CURRENT_THEME_CHAIN_THREAD_LOCAL.get();

    }

    /**
     * Set current shop's theme
     *
     * @param themeChain current shop's theme.
     */
    public static void setCurrentThemeChain(final List<String> themeChain) {

        CURRENT_THEME_CHAIN_THREAD_LOCAL.set(themeChain);

    }


    /**
     * Get current domain name from local thread.
     *
     * @return {@link Shop} URL domain for this request
     */
    public static String getCurrentDomain() {
        return DOMAIN_THREAD_LOCAL.get();
    }

    /**
     * Set domain name used to access {@link Shop} instance to current thread.
     *
     * @param currentDomain current request domain.
     */
    public static void setCurrentDomain(final String currentDomain) {
        DOMAIN_THREAD_LOCAL.set(currentDomain);
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
        DOMAIN_THREAD_LOCAL.set(null);
        SHOP_THREAD_LOCAL.set(null);
        CUSTOMER_SHOP_THREAD_LOCAL.set(null);
        SHOPPER_IP_ADDRESS_THREAD_LOCAL.set(null);
        CURRENT_THEME_CHAIN_THREAD_LOCAL.set(null);
        ShoppingCartContext.clear();
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        DOMAIN_THREAD_LOCAL.remove();
        SHOP_THREAD_LOCAL.remove();
        CUSTOMER_SHOP_THREAD_LOCAL.remove();
        SHOPPER_IP_ADDRESS_THREAD_LOCAL.remove();
        CURRENT_THEME_CHAIN_THREAD_LOCAL.remove();
        ShoppingCartContext.destroy();
    }


}
