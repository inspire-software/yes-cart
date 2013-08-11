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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.shoppingcart.ShoppingCart;

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
    private static ThreadLocal<ShoppingCart> shoppingCartThreadLocal = new ThreadLocal<ShoppingCart>();
    private static ThreadLocal<String> mailTemplatePathThreadLocal = new ThreadLocal<String>();

    private Cache urlShopCache;

    /**
     * Get app director instance.
     *
     * @return app directorr instance.
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
        Shop shop = null;
        Element elem = urlShopCache.get(serverDomainName);
        if (elem != null) {
            shop = (Shop) elem.getObjectValue();
        }

        if (shop == null) {
            shop = shopService.getShopByDomainName(serverDomainName);
            if (shop != null) {
                urlShopCache.put(new Element(serverDomainName, shop));
                //getIdShopCache().put(shop.getId(), shop);
            }
        }
        return shop;
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

    /**
     * @return current shop's theme
     */
    public static String getCurrentTheme() {
        final Shop shop = shopThreadLocal.get();
        if (shop == null) {
            return "default";
        }
        return shop.getFspointer();
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
     * Is Google checkout enabled.
     *
     * @return true if google checkout enabled.
     */
    public boolean isGoogleCheckoutEnabled() {
        return systemService.isGoogleCheckoutEnabled();
    }


    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.shopService = applicationContext.getBean("shopService", ShopService.class);
        this.systemService = applicationContext.getBean("systemService", SystemService.class);
        final CacheManager cacheManager = applicationContext.getBean("cacheManager", CacheManager.class);
        this.urlShopCache = cacheManager.getCache("org.yes.cart.web.urlShopCache");
    }
}
