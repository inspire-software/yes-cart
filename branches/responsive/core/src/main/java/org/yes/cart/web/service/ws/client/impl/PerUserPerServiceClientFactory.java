package org.yes.cart.web.service.ws.client.impl;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.yes.cart.web.service.ws.CacheDirector;
import org.yes.cart.web.service.ws.client.WsClientFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: igora
 * Date: 9/2/13
 * Time: 5:14 PM
 */
public class PerUserPerServiceClientFactory<T> implements WsClientFactory<T> {

    private final JaxWsProxyFactoryBean factory;
    private final String userName;
    private final String password;
    private final String url;
    private final long timeout;

    private final Map<Integer, T> pool = new ConcurrentHashMap<Integer, T>();
    private final Map<Integer, Boolean> semaphore = new ConcurrentHashMap<Integer, Boolean>();

    protected PerUserPerServiceClientFactory(Class<T> serviceInterface,
                                             final String userName,
                                             final String password,
                                             final String url,
                                             final long timeout) {
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.timeout = timeout;
        this.factory = new JaxWsProxyFactoryBean();
        this.factory.setServiceClass(serviceInterface);
        this.factory.setAddress(this.url);
    }

    /** {@inheritDoc} */
    @Override
    public T getService() {

        // Try pool first
        for (final Map.Entry<Integer, T> service : pool.entrySet()) {
            if (!semaphore.get(service.getKey())) {
                synchronized (pool) {
                    if (!semaphore.get(service.getKey())) {
                        semaphore.put(service.getKey(), Boolean.TRUE);
                        return service.getValue();
                    }
                }
            }
        }

        // create new instance for pool
        final T service;

        synchronized (pool) {

            service = (T) factory.create();
            final Client client = ClientProxy.getClient(service);
            configureClient(userName, password, timeout, client);
            semaphore.put(client.hashCode(), Boolean.TRUE);
            pool.put(client.hashCode(), service);

        }

        return service;

    }

    /** {@inheritDoc} */
    @Override
    public void release(final T service) {

        final Client client = ClientProxy.getClient(service);

        synchronized (pool) {

            if (!pool.containsKey(client.hashCode())) {
                throw new IllegalArgumentException("This WS client factory does not have provided service in pool:" + service);
            }

            semaphore.put(client.hashCode(), Boolean.FALSE);

        }

    }

    private void configureClient(final String userName,
                                 final String passw,
                                 final long timeout,
                                 final Client client) {

        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(timeout);
        httpClientPolicy.setAllowChunking(false);
        httpClientPolicy.setReceiveTimeout(timeout);

        ((HTTPConduit) client.getConduit()).setClient(httpClientPolicy);

        final Endpoint endpoint = client.getEndpoint();

        final WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(new HashMap<String, Object>() {{
            put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
            put(WSHandlerConstants.USER, userName);
            put(WSHandlerConstants.PW_CALLBACK_REF, new PWCallbackHandler(passw));
        }});
        endpoint.getOutInterceptors().add(wssOut);
    }

    private static class PWCallbackHandler implements CallbackHandler {

        private final String password;

        private PWCallbackHandler(final String password) {
            this.password = password;
        }

        /**
         * {@inheritDoc}
         */
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (int i = 0; i < callbacks.length; i++) {
                if (callbacks[i] instanceof WSPasswordCallback) {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                    pc.setPassword(password);
                }
            }
        }

    }

}
