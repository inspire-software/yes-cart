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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
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
    @Override
    public Shop getShopByOrderGuid(final String orderGuid) {
        return shopService.getShopByOrderGuid(orderGuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopByCode")
    public Shop getShopByCode(final String shopCode) {
        return shopService.getShopByCode(shopCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-allNonSubShops")
    public List<Shop> getNonSubShops() {
        return shopService.getNonSubShops();
    }


    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-allShopsMap")
    public Map<Long, Set<Long>> getAllShopsAndSubs() {
        return shopService.getAllShopsAndSubs();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-subShopsByMaster")
    public List<Shop> getSubShopsByMaster(final long masterId) {
        return shopService.getSubShopsByMaster(masterId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shop getSubShopByNameAndMaster(final String shopName, final long masterId) {
        return shopService.getSubShopByNameAndMaster(shopName, masterId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shop getShopByOrderNum(final String orderNum) {
        return shopService.getShopByOrderNum(orderNum);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopById")
    public Shop getById(final long shopId) {
        return shopService.getById(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return shopService.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopByDomainName")
    public Shop getShopByDomainName(final String serverName) {
        return shopService.getShopByDomainName(serverName);
    }

    /**
     * {@inheritDoc}
     */
    // @Cacheable(value = "shopService-shopCategoriesIds") already cached at shopCategorySupport
    @Override
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return shopService.getShopCategoriesIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    // @Cacheable(value = "shopService-shopContentIds")  already cached at shopCategorySupport
    @Override
    public Set<Long> getShopContentIds(final long shopId) {
        return shopService.getShopContentIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopAllCategoriesIds")
    public Set<Long> getShopAllCategoriesIds(final long shopId) {
        return shopService.getShopAllCategoriesIds(shopId);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findAllSupportedCurrenciesByShops() {
        return shopService.findAllSupportedCurrenciesByShops();
    }

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shopId given shop
     * @return list of assigned top level categories
     */
    @Override
    @Cacheable(value = "categoryService-topLevelCategories"/*, key="shop.shopId"*/)
    public Set<Category> getTopLevelCategories(final Long shopId) {
        return shopService.getTopLevelCategories(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Category> findAllByShopId(final long shopId) {
        return shopService.findAllByShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "categoryService-defaultNavigationCategory")
    public Category getDefaultNavigationCategory(final long shopId) {
        return shopService.getDefaultNavigationCategory(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopCategoryParentId")
    public Long getShopCategoryParentId(final long shopId, final long categoryId) {
        return shopService.getShopCategoryParentId(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopCategoryTemplate")
    public String getShopCategoryTemplate(final long shopId, final long categoryId) {
        return shopService.getShopCategoryTemplate(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopCategorySearchTemplate")
    public String getShopCategorySearchTemplate(final long shopId, final long categoryId) {
        return shopService.getShopCategorySearchTemplate(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "shopService-shopCategoryProductTypeId")
    public Long getShopCategoryProductTypeId(final long shopId, final long categoryId) {
        return shopService.getShopCategoryProductTypeId(shopId, categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
    public List<Shop> findAll() {
        return shopService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Shop> callback) {
        shopService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Shop> callback) {
        shopService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shop findById(final long pk) {
        return shopService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value ={
            "shopService-allCategoriesIdsMap",
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
    @Override
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
    @Override
    @CacheEvict(value ={
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-allCategoriesIdsMap",
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
    @Override
    public List<Shop> findByCriteria(final String eCriteria, final Object... parameters) {
        return shopService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return shopService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Shop findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return shopService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Shop, Long> getGenericDao() {
        return shopService.getGenericDao();
    }
}
