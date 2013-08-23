/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.domain.entity.bridge;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;

/**
 * User: denispavlov
 * Date: 13-08-22
 * Time: 12:06 AM
 */
public class HibernateSearchBridgeStaticLocator implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static GenericDAO<Shop, Long> getShopDao() {
        return applicationContext.getBean("shopDao", GenericDAO.class);
    }

    public static GenericDAO<Category, Long> getCategoryDao() {
        return applicationContext.getBean("categoryDao", GenericDAO.class);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
