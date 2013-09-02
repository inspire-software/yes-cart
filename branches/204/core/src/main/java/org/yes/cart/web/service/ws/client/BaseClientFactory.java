package org.yes.cart.web.service.ws.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: igora
 * Date: 9/2/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseClientFactory {

    protected void configureClient(final String userName, final long timeout, final Client client, final String callbackClass) {
        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(timeout);
        httpClientPolicy.setAllowChunking(false);
        httpClientPolicy.setReceiveTimeout(timeout);

        ((HTTPConduit) client.getConduit()).setClient(httpClientPolicy);

        final Endpoint endpoint = client.getEndpoint();

        final WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(new HashMap<String, Object>() {
            {
                put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);

                put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);

                put(WSHandlerConstants.USER, userName);

                put(WSHandlerConstants.PW_CALLBACK_CLASS, callbackClass);


            }
        });
        endpoint.getOutInterceptors().add(wssOut);
    }

}
