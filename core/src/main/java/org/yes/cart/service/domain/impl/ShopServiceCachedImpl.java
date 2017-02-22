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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:23
 */
public class ShopServiceCachedImpl implements ShopService {

    private final ShopService shopService;

    public ShopServiceCachedImpl(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderGuid(final String orderGuid) {
        return shopService.getShopByOrderGuid(orderGuid);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopByCode")
    public Shop getShopByCode(final String shopCode) {
        return shopService.getShopByCode(shopCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-allNonSubShops")
    public List<Shop> getNonSubShops() {
        return shopService.getNonSubShops();
    }


    /** {@inheritDoc} */
    @Cacheable(value = "shopService-allShopsMap")
    public Map<Long, Set<Long>> getAllShopsAndSubs() {
        return shopService.getAllShopsAndSubs();
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-subShopsByMaster")
    public List<Shop> getSubShopsByMaster(final long masterId) {
        return shopService.getSubShopsByMaster(masterId);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getSubShopByNameAndMaster(final String shopName, final long masterId) {
        return shopService.getSubShopByNameAndMaster(shopName, masterId);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderNum(final String orderNum) {
        return shopService.getShopByOrderNum(orderNum);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopById")
    public Shop getById(final long shopId) {
        return shopService.getById(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return shopService.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopByDomainName")
    public Shop getShopByDomainName(final String serverName) {
        return shopService.getShopByDomainName(serverName);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return shopService.getShopCategoriesIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopContentIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopContentIds(final long shopId) {
        return shopService.getShopContentIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopAllCategoriesIds"/*, key ="shop.getShopId()"*/)
    public Set<Long> getShopAllCategoriesIds(final long shopId) {
        return shopService.getShopAllCategoriesIds(shopId);
    }

    /** {@inheritDoc} */
    public List<String> findAllSupportedCurrenciesByShops() {
        return shopService.findAllSupportedCurrenciesByShops();
    }

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shopId given shop
     * @return list of assigned top level categories
     */
    @Cacheable(value = "categoryService-topLevelCategories"/*, key="shop.shopId"*/)
    public Set<Category> getTopLevelCategories(final Long shopId) {
        return shopService.getTopLevelCategories(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Category> findAllByShopId(final long shopId) {
        return shopService.findAllByShopId(shopId);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoryParentId")
    public Long getShopCategoryParentId(final long shopId, final long categoryId) {
        return shopService.getShopCategoryParentId(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoryTemplate")
    public String getShopCategoryTemplate(final long shopId, final long categoryId) {
        return shopService.getShopCategoryTemplate(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategorySearchTemplate")
    public String getShopCategorySearchTemplate(final long shopId, final long categoryId) {
        return shopService.getShopCategorySearchTemplate(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "shopService-shopCategoryProductTypeId")
    public Long getShopCategoryProductTypeId(final long shopId, final long categoryId) {
        return shopService.getShopCategoryProductTypeId(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public Long findShopIdByCode(final String code) {
        return shopService.findShopIdByCode(code);
    }

    /**
     * Set attribute value. New attribute value will be created, if attribute has not value for given shop.
     *
     * @param shopId shop id
     * @param attributeKey attribute key
     * @param attributeValue attribute value.
     */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "addressBookService-allCountries",
            "mailSenderBuilder-buildJavaMailSender"
    }, allEntries = true)
    public void updateAttributeValue(final long shopId, final String attributeKey, final String attributeValue) {
        shopService.updateAttributeValue(shopId, attributeKey, attributeValue);
    }

    /**
     * {@inheritDoc}
     */
    public List<Shop> findAll() {
        return shopService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Shop findById(final long pk) {
        return shopService.findById(pk);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopCategoriesIds",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopFederationStrategy-admin",
            "shopFederationStrategy-shop",
            "shopFederationStrategy-shopId",
            "shopFederationStrategy-shopCode"
    }, allEntries = true)
    public Shop create(final Shop instance) {
        return shopService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "themeService-themeChainByShopId",
            "themeService-markupChainByShopId",
            "themeService-mailTemplateChainByShopId",
            "themeService-reportsTemplateChainByShopId",
            "mailSenderBuilder-buildJavaMailSender"
    }, allEntries = true)
    public Shop update(final Shop instance) {
        return shopService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-shopCategoriesIds",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds",
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "themeService-themeChainByShopId",
            "themeService-markupChainByShopId",
            "themeService-mailTemplateChainByShopId",
            "themeService-reportsTemplateChainByShopId",
            "mailSenderBuilder-buildJavaMailSender"
    }, allEntries = true)
    public void delete(final Shop instance) {
        shopService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<Shop> findByCriteria(final Criterion... criterion) {
        return shopService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return shopService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return shopService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public Shop findSingleByCriteria(final Criterion... criterion) {
        return shopService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<Shop, Long> getGenericDao() {
        return shopService.getGenericDao();
    }
}
