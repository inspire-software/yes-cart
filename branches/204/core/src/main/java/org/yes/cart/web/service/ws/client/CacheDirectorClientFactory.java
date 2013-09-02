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

package org.yes.cart.web.service.ws.client;

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

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Factory responsible to get back door service.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/29/12
 * Time: 7:56 PM
 */
public class CacheDirectorClientFactory extends BaseClientFactory implements CallbackHandler {

    private final static ConcurrentHashMap<String, String> concurrentHashMap;

    private final static JaxWsProxyFactoryBean factory;

    static {
        concurrentHashMap = new ConcurrentHashMap<String, String>();
        factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(CacheDirector.class);
    }



    /**
     * Get back door service.
     *
     * @param userName user name
     * @param password password
     * @param url      url
     * @return {@link org.yes.cart.web.service.ws.CacheDirector}
     */
    public CacheDirector getCacheDirector(final String userName,
                                              final String password,
                                              final String url,
                                              final long timeout) {

        final CacheDirector cacheDirector;

        synchronized (factory) {
            factory.setAddress(url);
            cacheDirector = (CacheDirector) factory.create();
            concurrentHashMap.put(userName, password);
        }

        final Client client = ClientProxy.getClient(cacheDirector);
        configureClient(userName, timeout, client, this.getClass().getName());
        return cacheDirector;

    }


    /**
     * {@inheritDoc}
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
            pc.setPassword(concurrentHashMap.get(pc.getIdentifier()));
            return;
        }
    }
}
