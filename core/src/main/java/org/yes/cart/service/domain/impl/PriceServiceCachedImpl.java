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
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.PriceService;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 17:33
 */
public class PriceServiceCachedImpl implements PriceService {

    private final PriceService priceService;

    public PriceServiceCachedImpl(final PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-minimalPrice")
    public SkuPrice getMinimalPrice(final Long productId,
                                    final String selectedSku,
                                    final long customerShopId,
                                    final Long masterShopId,
                                    final String currencyCode,
                                    final BigDecimal quantity,
                                    final boolean enforceTier,
                                    final String pricingPolicy) {
        return priceService.getMinimalPrice(productId, selectedSku, customerShopId, masterShopId, currencyCode, quantity, enforceTier, pricingPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-allCurrentPrices")
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy) {
        return priceService.getAllCurrentPrices(productId, selectedSku, customerShopId, masterShopId, currencyCode, pricingPolicy);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "priceService-allPrices")
    public List<SkuPrice> getAllPrices(final Long productId, final String selectedSku, final String currencyCode) {
        return priceService.getAllPrices(productId, selectedSku, currencyCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<FilteredNavigationRecord> getPriceNavigationRecords(final PriceTierTree priceTierTree, final String currency, final Shop customerShop) {
        return priceService.getPriceNavigationRecords(priceTierTree, currency, customerShop);
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> findAll() {
        return priceService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public SkuPrice findById(final long pk) {
        return priceService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice create(final SkuPrice instance) {
        return priceService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice update(final SkuPrice instance) {
        return priceService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public void delete(final SkuPrice instance) {
        priceService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<SkuPrice> findByCriteria(final Criterion... criterion) {
        return priceService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return priceService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return priceService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public SkuPrice findSingleByCriteria(final Criterion... criterion) {
        return priceService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<SkuPrice, Long> getGenericDao() {
        return priceService.getGenericDao();
    }
}
