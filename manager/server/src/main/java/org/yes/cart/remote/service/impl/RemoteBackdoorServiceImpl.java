package org.yes.cart.remote.service.impl;

import flex.messaging.FlexContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.yes.cart.remote.service.RemoteBackdoorService;
import org.yes.cart.web.service.ws.BackdoorService;
import org.yes.cart.web.service.ws.client.BackdoorServiceClientFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import java.util.List;

/**
 * User: iga Igor Azarny
 * Date: 4 - feb - 12
 * Time: 5:44 PM
 */
public class RemoteBackdoorServiceImpl implements RemoteBackdoorService {


    /** {@inheritDoc} */
    public int reindexAllProducts() {
        return getBackdoorService().reindexAllProducts();
    }

    /** {@inheritDoc} */
    public int reindexProduct(final long productPk) {
        return getBackdoorService().reindexProduct(productPk);
    }

    /** {@inheritDoc} */
    public int reindexProducts(final long[] productPks) {
        return getBackdoorService().reindexProducts(productPks);
    }

    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final String query) {
        return getBackdoorService().sqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final String query) {
        return getBackdoorService().hsqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final String query) {
        return getBackdoorService().luceneQuery(query);
    }

    private BackdoorServiceClientFactory backdoorServiceClientFactory = null;

    private synchronized BackdoorServiceClientFactory getBackdoorServiceClientFactory() {

        if (backdoorServiceClientFactory == null) {
            backdoorServiceClientFactory = new BackdoorServiceClientFactory();
        }

        return backdoorServiceClientFactory;

    }

    /**
     * Bad idea, but i have no chance with new spring security, to get the credentials, because it erased from authentificated session.
     *
     * @param password
     * @return {@link BackdoorService}
     */
    private BackdoorService getBackdoorService(final String password) {


        String userName =  ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getName();
        //String password = (String) ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getCredentials();

        return getBackdoorServiceClientFactory().getBackdoorService(
                userName,
                password,
                "http://localhost:8080/yes-shop/services/backdoor");

    }


}
