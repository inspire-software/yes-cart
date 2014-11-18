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
import org.yes.cart.domain.entity.bridge.support.NavigatableAttributesSupport;
import org.yes.cart.domain.entity.bridge.support.ShopCategoryRelationshipSupport;
import org.yes.cart.domain.entity.bridge.support.ShopWarehouseRelationshipSupport;

/**
 * User: denispavlov
 * Date: 13-08-22
 * Time: 12:06 AM
 */
public class HibernateSearchBridgeStaticLocator implements ApplicationContextAware {

    private static ShopCategoryRelationshipSupport SHOP_CATEGORY_SUPPORT;
    private static ShopWarehouseRelationshipSupport SHOP_WAREHOUSE_SUPPORT;
    private static NavigatableAttributesSupport NAVIGATABLE_ATTRIBUTES_SUPPORT;

    public static ShopCategoryRelationshipSupport getShopCategoryRelationshipSupport() {
        return SHOP_CATEGORY_SUPPORT;
    }

    public static ShopWarehouseRelationshipSupport getShopWarehouseRelationshipSupport() {
        return SHOP_WAREHOUSE_SUPPORT;
    }

    public static NavigatableAttributesSupport getNavigatableAttributesSupport() {
        return NAVIGATABLE_ATTRIBUTES_SUPPORT;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SHOP_CATEGORY_SUPPORT = applicationContext.getBean("shopCategoryRelationshipSupport", ShopCategoryRelationshipSupport.class);
        SHOP_WAREHOUSE_SUPPORT = applicationContext.getBean("shopWarehouseRelationshipSupport", ShopWarehouseRelationshipSupport.class);
        NAVIGATABLE_ATTRIBUTES_SUPPORT = applicationContext.getBean("navigatableAttributesSupport", NavigatableAttributesSupport.class);
    }
}
