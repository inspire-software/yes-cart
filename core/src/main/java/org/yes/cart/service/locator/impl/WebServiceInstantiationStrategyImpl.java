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

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.util.ShopCodeContext;

import javax.xml.XMLConstants;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class WebServiceInstantiationStrategyImpl implements InstantiationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private static final String NAMESPACE_URI = XMLConstants.NULL_NS_URI; // TODO is separate namespace needed ?

/**
     * {@inheritDoc}
     */
    public <T> T getInstance(final String serviceUrl,
                             final Class<T> iface,
                             final String loginName,
                             final String password)  {
    //TODO use login & password 
        if(LOG.isDebugEnabled()) {
            LOG.debug("Get " + serviceUrl + " as " + iface.getName());
        }

        try {
            /*final QName qname = new QName(NAMESPACE_URI, getServiceName(serviceUrl));
            final javax.xml.ws.Service webServ = javax.xml.ws.Service.create(qname);
            return webServ.getPort(iface);*/

            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

            factory.setServiceClass(iface);

            factory.setAddress( getServiceUrl(serviceUrl) );

            return (T) factory.create();

        } catch (Exception e) {
            final RuntimeException instantiationException = new RuntimeException("Service " +serviceUrl + " cannot be instantiated");
            instantiationException.initCause(e);
            throw instantiationException; // NOPMD
        }
    }




    String getServiceName(final String serviceUrl) {
        return serviceUrl.substring(serviceUrl.lastIndexOf('/') + 1);
    }


    /**
     * Just remove ?wsdl
     * @param serviceUrl
     * @return  service url without wsdl parameter.
     */
    String getServiceUrl(final String serviceUrl) {

        if (serviceUrl.indexOf('?') > -1) {
            return  serviceUrl.substring(0, serviceUrl.indexOf('?'));
        }

        return serviceUrl;
    }





}
