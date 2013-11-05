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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;

import java.util.Collection;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ShopService extends AttributeManageGenericService<Shop> {

    /**
     * Get the {@link Shop} by given server name.
     *
     * @param serverName the server name
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByDomainName(String serverName);

    /**
     * Find shop by id
     * @param shopId  given id
     * @return     shop if found, otherwise null
     */
    Shop findById(long shopId);

    /**
     * Get the {@link Shop} by given order guid.
     *
     * @param orderGuid the guid of order
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderGuid(String orderGuid);


    /**
     * Get shop by given shop code.
     * @param shopCode  given shop code.
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByCode(String shopCode);

    /**
     * Get the {@link Shop} by given order number.
     *
     * @param orderNum given order number
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderNum(String orderNum);


    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shopId given shop
     * @return linear representation of category tree
     */
    Set<Category> getShopCategories(long shopId);

    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shopId given shop
     * @return linear representation of category tree
     */
    Set<Long> getShopCategoriesIds(long shopId);

    /**
     * Get all supported currencies by all shops.
     *
     * @return all supported currencies.
     */
    Collection<String> getAllSupportedCurrenciesByShops();


}
