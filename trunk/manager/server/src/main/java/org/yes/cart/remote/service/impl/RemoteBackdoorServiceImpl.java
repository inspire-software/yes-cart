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
        return getBackdoorService(300000).reindexAllProducts();
    }

    /** {@inheritDoc} */
    public int reindexProduct(final long productPk) {
        return getBackdoorService(60000).reindexProduct(productPk);
    }

    /** {@inheritDoc} */
    public int reindexProducts(final long[] productPks) {
        return getBackdoorService(60000).reindexProducts(productPks);
    }

    /** {@inheritDoc} */
    public List<Object[]> sqlQuery(final String query) {
        return getBackdoorService(60000).sqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> hsqlQuery(final String query) {
        return getBackdoorService(60000).hsqlQuery(query);
    }

    /** {@inheritDoc} */
    public List<Object[]> luceneQuery(final String query) {
        return getBackdoorService(60000).luceneQuery(query);
    }

    private BackdoorServiceClientFactory backdoorServiceClientFactory = null;

    private synchronized BackdoorServiceClientFactory getBackdoorServiceClientFactory() {

        if (backdoorServiceClientFactory == null) {
            backdoorServiceClientFactory = new BackdoorServiceClientFactory();
        }

        return backdoorServiceClientFactory;

    }

    private BackdoorService getBackdoorService(final long timeout) {


        String userName =  ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getName();
        //String password = (String) ((UsernamePasswordAuthenticationToken) FlexContext.getUserPrincipal()).getCredentials();
        String password = (String) FlexContext.getFlexSession().getAttribute("pwd");

        return getBackdoorServiceClientFactory().getBackdoorService(
                userName,
                password,
                "http://localhost:8080/yes-shop/services/backdoor", timeout);

    }


}
