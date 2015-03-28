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

package org.yes.cart.service.locator.impl;

import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.util.ShopCodeContext;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Service locator use particular strategy, that depends from protocol in service url, to
 * instantiate service. At thi moment tree strategies available - web service , jnp an spring local.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ServiceLocatorImpl implements ServiceLocator {


    private final Map<String, InstantiationStrategy> protocolStrategyMap;


    /**
     * Construct the service locator.
     *
     * @param protocolStrategyMap strategy  map to instantiate service.
     */
    public ServiceLocatorImpl(final Map<String, InstantiationStrategy> protocolStrategyMap) {
        this.protocolStrategyMap = protocolStrategyMap;
    }


    /**
     * Get {@link InstantiationStrategy} by given service url.
     *
     * @param serviceUrl given service url

     * @return {@link InstantiationStrategy} to create particular service instance.
     */
    InstantiationStrategy getInstantiationStrategy(final String serviceUrl) {
        final String strategyKey = getStrategyKey(serviceUrl);
        final InstantiationStrategy instantiationStrategy = protocolStrategyMap.get(strategyKey);
        if (instantiationStrategy == null) {
            throw new RuntimeException(
                    MessageFormat.format(
                            "Instantiation strategy can not be found for key {0} from url {1}",
                            strategyKey,
                            serviceUrl
                    )
            );
        }
        return instantiationStrategy;
    }

    /**
     * Get protocol from url. Possible values - http,https,jnp.
     * Null will be returned for spring.
     *
     * @param url given url
     * @return protocol
     */
    String getStrategyKey(final String url) {
        if (url.indexOf(':') > -1) {
            return url.substring(0, url.indexOf(':'));
        }
        return null;
    }


    /** {@inheritDoc} */
    public <T> T getServiceInstance(final String serviceUrl,
                                    final Class<T> iface,
                                    final String loginName,
                                    final String password) {

        ShopCodeContext.getLog(this).debug("Get {} as {}", serviceUrl, iface.getName());

        try {
            return getInstantiationStrategy(serviceUrl).getInstance(serviceUrl, iface, loginName, password);
        } catch (Exception e) {
            throw new RuntimeException(
                    MessageFormat.format
                            ("Can not create {0} instance. Given interface is {1}. See root cause for more detail",
                                    serviceUrl, iface.getName()), e);
        }
    }

}
