package org.yes.cart.web.application;

import com.google.common.collect.MapMaker;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 7:11 PM
 */

@ManagedBean(name = WebParametersKeys.APPLICATION_DYNAMYC_CACHE)
@ApplicationScoped
public class ApplicationDynamicCache {

    private ConcurrentMap<String, Shop> urlShopCache;
    private ConcurrentMap<Long, Shop> idShopCache;

    /**
     * Construct cache.
     */
    public ApplicationDynamicCache() {

        urlShopCache = new MapMaker().concurrencyLevel(16).softValues().expiration(3, TimeUnit.MINUTES).makeMap();
        idShopCache  = new MapMaker().concurrencyLevel(16).softValues().expiration(3, TimeUnit.MINUTES).makeMap();

    }

    public ConcurrentMap<String, Shop> getUrlShopCache() {
        return urlShopCache;
    }


    public ConcurrentMap<Long, Shop> getIdShopCache() {
        return idShopCache;
    }

}
