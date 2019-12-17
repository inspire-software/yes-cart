/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ShopService extends GenericService<Shop> {

    /**
     * Get the {@link Shop} by given server name.
     *
     * @param serverName the server name
     *
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByDomainName(String serverName);

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<Shop> getAll();

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<Shop> getNonSubShops();


    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    Map<Long, Set<Long>> getAllShopsAndSubs();


    /**
     * Find shop by id
     *
     * @param shopId  given id
     *
     * @return     shop if found, otherwise null
     */
    Shop getById(long shopId);

    /**
     * Get the {@link Shop} by given order guid.
     *
     * @param orderGuid the guid of order
     *
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderGuid(String orderGuid);


    /**
     * Get shop by given shop code.
     *
     * @param shopCode  given shop code.
     *
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByCode(String shopCode);

    /**
     * Get shops by given shop master.
     *
     * @param masterId master shop
     *
     * @return instance of Shop if it has given server name, otherwise null
     */
    List<Shop> getSubShopsByMaster(long masterId);

    /**
     * Get shop by given shop code.
     *
     * @param shopName  given shop code.
     * @param masterId master shop
     *
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getSubShopByNameAndMaster(String shopName, long masterId);

    /**
     * Get the {@link Shop} by given order number.
     *
     * @param orderNum given order number
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderNum(String orderNum);

    /**
     * Get all categories and content including child categories, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopsAllCategoriesIds(Collection<Long> shopId);

    /**
     * Get all categories and content including child categories, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopAllCategoriesIds(long shopId);

    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopsCategoriesIds(Collection<Long> shopId);

    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopCategoriesIds(long shopId);

    /**
     * Get all content including child content, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopsContentIds(Collection<Long> shopId);

    /**
     * Get all content including child content, that belong to given shop.
     *
     * @param shopId given shop
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopContentIds(long shopId);

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shopId given shop
     *
     * @return ordered by rank list of assigned top level categories
     */
    Set<Category> getTopLevelCategories(Long shopId);

    /**
     * Get all assigned to shop categories.
     *
     * @param shopId shop id
     *
     * @return list of assigned categories
     */
    Set<Category> findAllByShopId(long shopId);

    /**
     * Get the default category for navigation configuration for shop.
     *
     * @param shopId PK of shop
     *
     * @return category with default navigation.
     */
    Category getDefaultNavigationCategory(long shopId);

    /**
     * Get all supported currencies by all shops.
     *
     * @return all supported currencies.
     */
    List<String> findAllSupportedCurrenciesByShops();

    /**
     * Get the category parent ID for given shop.
     *
     * @param shopId shop id
     * @param categoryId given category PK
     *
     * @return parent ID (or symlink parent)
     */
    Long getShopCategoryParentId(long shopId, long categoryId);


    /**
     * Get the "template variation" template (No fail over).
     *
     * @param shopId shop id
     * @param categoryId given category PK
     *
     * @return Template variation
     */
    String getShopCategoryTemplate(long shopId, long categoryId);


    /**
     * Get the "template variation" template (No fail over).
     *
     * @param shopId shop id
     * @param categoryId given category PK
     *
     * @return Template variation
     */
    String getShopCategorySearchTemplate(long shopId, long categoryId);

    /**
     * Get category type with failover mechanism. Uses category product type if set, otherwise
     * looks up parent category product type, until one is found
     *
     * @param shopId shop id
     * @param categoryId category id
     *
     * @return product type for this category
     */
    Long getShopCategoryProductTypeId(long shopId, long categoryId);


    /**
     * Get shop id by given code
     *
     * @param code given code
     *
     * @return shop id if found otherwise null
     */
    Long findShopIdByCode(String code);

    /**
     * Set attribute value. New attribute value will be created,
     * if attribute has not value for given shop.
     *
     * @param entityId       entity pk value
     * @param attributeKey   attribute key
     * @param attributeValue attribute value.
     */
    void updateAttributeValue(long entityId, String attributeKey, String attributeValue);


}
