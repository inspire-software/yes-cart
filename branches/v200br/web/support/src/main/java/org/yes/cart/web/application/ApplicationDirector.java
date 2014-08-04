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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Storefront  director class responsible for data caching,
 * common used operations, etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 7:11 PM
 */

public class ApplicationDirector implements ApplicationContextAware {

    private ShopService shopService;
    private SystemService systemService;
    private static ApplicationDirector applicationDirector;

    private static ThreadLocal<Shop> shopThreadLocal = new ThreadLocal<Shop>();
    private static ThreadLocal<List<String>> currentThemeChainThreadLocal = new ThreadLocal<List<String>>();
    private static ThreadLocal<ShoppingCart> shoppingCartThreadLocal = new ThreadLocal<ShoppingCart>();
    private static ThreadLocal<String> mailTemplatePathThreadLocal = new ThreadLocal<String>();
    private static ThreadLocal<String> shopperIPAddressThreadLocal = new ThreadLocal<String>();

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
     * Get app director instance.
     *
     * @return app director instance.
     */
    public static ApplicationDirector getInstance() {
        return applicationDirector;
    }

    /**
     * Construct application director.
     */
    public ApplicationDirector() {
        applicationDirector = this;
    }



    /**
     * Get {@link Shop} from cache by given domain address.
     *
     * @param serverDomainName given given domain address.
     * @return {@link Shop}
     */
    public Shop getShopByDomainName(final String serverDomainName) {
        return shopService.getShopByDomainName(serverDomainName);
    }

    /**
     * Get current mail template folder.
     *
     * @return current mail template folder.
     */
    public static String getCurrentMailTemplateFolder() {
        return mailTemplatePathThreadLocal.get();
    }

    /**
     * Set current mail template folder.
     *
     * @param currentMailTemplateFolder current mail template folder.
     */
    public static void setCurrentMailTemplateFolder(final String currentMailTemplateFolder) {
        mailTemplatePathThreadLocal.set(currentMailTemplateFolder);
    }



    /**
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentShop() {
        return shopThreadLocal.get();
    }

    private static final String[] DEFAULT = new String[] { "default" };

    /**
     * @return current shop's theme
     */
    public static List<String> getCurrentThemeChain() {
        List<String> chain = currentThemeChainThreadLocal.get();
        if (chain == null) {

            final Shop shop = shopThreadLocal.get();
            if (shop == null) {
                chain = Arrays.asList(DEFAULT);
            } else {
                final String themeCfg = shop.getFspointer();
                if (StringUtils.isBlank(themeCfg)) {
                    chain = Arrays.asList(DEFAULT);
                } else if (themeCfg.indexOf(';') == -1) {
                    chain = Arrays.asList(themeCfg);
                } else {
                    chain = new ArrayList<String>(Arrays.asList(StringUtils.split(shop.getFspointer(), ';')));
                    if (!chain.contains(DEFAULT[0])) {
                        chain.add(DEFAULT[0]);
                    }
                }
            }
            currentThemeChainThreadLocal.set(chain);

        }
        return chain;

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
     * Get shopping cart from local thread storage.
     *
     * @return {@link ShoppingCart}
     */
    public static ShoppingCart getShoppingCart() {
        return shoppingCartThreadLocal.get();
    }

    /**
     * Set shopping cart to storage.
     *
     * @param shoppingCart current cart.
     */
    public static void setShoppingCart(final ShoppingCart shoppingCart) {
        shoppingCartThreadLocal.set(shoppingCart);
    }

    /**
     * Clear thread locals at the end of the request
     */
    public static void clear() {
        shopThreadLocal.set(null);
        shoppingCartThreadLocal.set(null);
        mailTemplatePathThreadLocal.set(null);
        shopperIPAddressThreadLocal.set(null);
        currentThemeChainThreadLocal.set(null);
    }




    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.shopService = applicationContext.getBean("shopService", ShopService.class);
        this.systemService = applicationContext.getBean("systemService", SystemService.class);
    }
}
