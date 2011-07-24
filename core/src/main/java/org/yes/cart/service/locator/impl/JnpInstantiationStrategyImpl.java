package org.yes.cart.service.locator.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.locator.InstantiationStrategy;

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

    private static final Logger LOG = LoggerFactory.getLogger(JnpInstantiationStrategyImpl.class);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get " + serviceUrl + " as " + iface.getName());
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
