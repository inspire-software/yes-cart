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

import org.slf4j.Logger;
import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.util.ShopCodeContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.util.Properties;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class JnpInstantiationStrategyImpl implements InstantiationStrategy {

    private static final String AT_DELIMITER = "@"; // delimiter between server and jndi name

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getInstance(final String serviceUrl,
                             final Class<T> iface,
                             final String loginName,
                             final String password) {
        //TODO pass login & pwd into context during lookup operation
        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug("Get " + serviceUrl + " as " + iface.getName());
        }

        try {
            final Object obj = internalLookup(serviceUrl);
            if (serviceUrl.indexOf("/local") > -1 || serviceUrl.indexOf("java:") > -1) { /* JBoss specific */
                return (T) obj;
            } else {
                return (T) PortableRemoteObject.narrow(obj, iface);
            }
        } catch (Exception e) {
            throw new RuntimeException("Service " + serviceUrl + " cannot be instantiated", e);
        }
    }

    protected <T> T internalLookup(final String url) throws NamingException {

        final String providerUrl = getProviderUrl(url);

        final Properties prop = new Properties();  //TODO configure from spring
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory"); // jboss specific
        prop.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces"); // jboss specific
        prop.put(Context.PROVIDER_URL, providerUrl);

        final InitialContext initialContext = new InitialContext(prop);

        return (T) initialContext.lookup(getJndiName(url));
    }


    /**
     * Full reference: jnp://172.30.77.75:1099@common.service.locator.tests/TestSLejbA/local or .../TestSLejbA/remote
     * Short reference: common.service.locator.tests/TestSLejbA/local or .../TestSLejbA/remote
     * If reference is short default JNDI provider is used
     *
     * @param url ejb url
     * @return jnp url of remote server
     */
    private String getProviderUrl(final String url) {
        if (url.indexOf(AT_DELIMITER) > -1) {
            return url.split("@")[0];
        }
        return getDefaultProviderUrl();
    }

    private String getDefaultProviderUrl() {
        return System.getProperty("jboss.bind.address") + ":1099"; /* JBoss specific */
    }

    /**
     * Expected ejb url format is removeServer@serviceJndiName
     *
     * @param url ejb url
     * @return jndi name
     */
    private String getJndiName(final String url) {
        if (url.indexOf(AT_DELIMITER) > -1) {
            return url.split("@")[1];
        }
        return url;
    }


}
