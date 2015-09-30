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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private ThemeService themeService;
    private static ApplicationDirector applicationDirector;

    private static ThreadLocal<String> domainThreadLocal = new ThreadLocal<String>();
    private static ThreadLocal<Shop> shopThreadLocal = new ThreadLocal<Shop>();
    private static ThreadLocal<List<String>> currentThemeChainThreadLocal = new ThreadLocal<List<String>>();
    private static ThreadLocal<ShoppingCart> shoppingCartThreadLocal = new ThreadLocal<ShoppingCart>();
    private static ThreadLocal<String> shopperIPAddressThreadLocal = new ThreadLocal<String>();

    private static final Map<String, List<String>> chainCache = new ConcurrentHashMap<String, List<String>>();

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
     * Get current shop from local thread.
     *
     * @return {@link Shop} instance
     */
    public static Shop getCurrentShop() {
        return shopThreadLocal.get();
    }

    /**
     * @return current shop's theme
     */
    public static List<String> getCurrentThemeChain() {
        List<String> chain = currentThemeChainThreadLocal.get();
        if (chain == null) {

            ThemeService service = getInstance().themeService;
            if (service == null) {
                throw new IllegalStateException("ApplicationDirector.themeService is not initialised");
            }

            final Shop shop = shopThreadLocal.get();
            chain = service.getThemeChainByShopId(shop == null ? null : shop.getShopId(), domainThreadLocal.get());
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
        domainThreadLocal.set(null);
        shopThreadLocal.set(null);
        shoppingCartThreadLocal.set(null);
        shopperIPAddressThreadLocal.set(null);
        currentThemeChainThreadLocal.set(null);
    }




    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.shopService = applicationContext.getBean("shopService", ShopService.class);
        this.systemService = applicationContext.getBean("systemService", SystemService.class);
        this.themeService = applicationContext.getBean("themeService", ThemeService.class);
    }
}
