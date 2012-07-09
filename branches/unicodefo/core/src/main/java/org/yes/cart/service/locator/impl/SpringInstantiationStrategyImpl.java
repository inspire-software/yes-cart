package org.yes.cart.service.locator.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.util.ShopCodeContext;

/**
 * Used to locate services in local spring context.
 * When no protocol provided in the service url.
 * <p/>
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SpringInstantiationStrategyImpl implements InstantiationStrategy, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    public <T> T getInstance(final String serviceUrl,
                             final Class<T> iface,
                             final String loginName, 
                             final String password) throws RuntimeException {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Get " + serviceUrl + " as " + iface.getName());
        }
        return applicationContext.getBean(serviceUrl, iface);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
