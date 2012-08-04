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

package org.yes.cart.web.service.ws.impl;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.junit.Test;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.web.service.ws.BackdoorService;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Actually not a test but client
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/29/12
 * Time: 10:25 AM
 */
public class BackdoorServiceImplTest implements CallbackHandler {

    @Test
    public void testReindexAllProducts() throws Exception {

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out, true)));
        factory.getOutInterceptors().add(new LoggingOutInterceptor(new PrintWriter(System.out, true)));
        factory.setServiceClass(BackdoorService.class);
        factory.setAddress("http://localhost:8080/yes-shop/services/backdoor");

        BackdoorService backdoorService = (BackdoorService) factory.create();

        Client client = ClientProxy.getClient(backdoorService);
        Endpoint endpoint = client.getEndpoint();

        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(new HashMap<String, Object>() {
            {
                put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);

                put(WSHandlerConstants.USER, "admin@yes-cart.com");

                put(WSHandlerConstants.PW_CALLBACK_CLASS, "org.yes.cart.web.service.ws.impl.BackdoorServiceImplTest");


            }
        });
        endpoint.getOutInterceptors().add(wssOut);

        // TODO: why is this test written in such a way? are we expecting an exception? why just not test for that?
        // javax.xml.ws.WebServiceException maybe?

        try {
            backdoorService.reindexAllProducts();

        }    catch (Exception ex) {
            ex.printStackTrace();
            //nothing al all
        }



    }


    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

            String pass = "1234567";
            if (pass != null) {
                pc.setPassword(pass);
                return;
            }
        }
    }
}
