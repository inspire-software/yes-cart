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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.util.ShopCodeContext;

/**
 * Used to locate services in local spring context.
 * When no protocol provided in the service url.
 * <p/>
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SpringInstantiationStrategyImpl implements InstantiationStrategy, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    public <T> T getInstance(final String serviceUrl,
                             final Class<T> iface,
                             final String loginName,
                             final String password) throws RuntimeException {
        ShopCodeContext.getLog(this).debug("Get {} as {}", serviceUrl, iface.getName());
        return applicationContext.getBean(serviceUrl, iface);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
