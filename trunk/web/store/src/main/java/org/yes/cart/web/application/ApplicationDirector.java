package org.yes.cart.web.application;

import com.google.common.collect.MapMaker;
import org.springframework.context.ApplicationContext;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * Storefornt director class responsible for data caching,
 *  common used operations, etc.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 7:11 PM
 */

@ManagedBean(name = WebParametersKeys.APPLICATION_DYNAMYC_CACHE)
@ApplicationScoped
public class ApplicationDirector {

    @ManagedProperty(ManagedBeanELNames.EL_SHOP_SERVICE)
    private ShopService shopService;

    private final ConcurrentMap<String, Shop> urlShopCache;
    private final ConcurrentMap<Long, Shop> idShopCache;

    /**
     * Construct cache.
     */
    public ApplicationDirector() {

        urlShopCache = new MapMaker().concurrencyLevel(16).softValues().expiration(3, TimeUnit.MINUTES).makeMap();
        idShopCache = new MapMaker().concurrencyLevel(16).softValues().expiration(3, TimeUnit.MINUTES).makeMap();

    }

    private ConcurrentMap<String, Shop> getUrlShopCache() {
        return urlShopCache;
    }


    private ConcurrentMap<Long, Shop> getIdShopCache() {
        return idShopCache;
    }

    /**
     * Get {@link Shop} from cache by his id.
     * @param shopId given shop id
     * @return {@link Shop}
     */
    public Shop getShopById(final Long shopId) {
        Shop shop = getIdShopCache().get(shopId);
        if (shop == null) {
            shop = shopService.findById(shopId);
            if (shop != null) {
                getIdShopCache().put(shopId, shop);
            }
        }
        return shop;


    }


    /**
     * Get {@link Shop} from cache by given domain address.
     * @param serverDomainName given given domain address.
     * @return {@link Shop}
     */
    public Shop getShopByDomainName(final String serverDomainName) {
        Shop shop = getUrlShopCache().get(serverDomainName);
        if (shop == null) {
            shop = shopService.getShopByDomainName(serverDomainName);
            if (shop != null) {
                getUrlShopCache().put(serverDomainName, shop);
                getIdShopCache().put(shop.getId(), shop);
            }
        }
        return shop;
    }


    /**
     * Set shop service.
     * @param shopService  shop service to use.
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Get spring application context.
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(
                (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()
        );
    }


}
