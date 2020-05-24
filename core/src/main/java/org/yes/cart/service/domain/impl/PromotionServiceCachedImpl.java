/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:19
 */
public class PromotionServiceCachedImpl implements PromotionService {

    private final PromotionService promotionService;

    public PromotionServiceCachedImpl(final PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /** {@inheritDoc} */
    @Override
//    @Cacheable(value = "promotionService-promotionsByShopCode") /* This is already cached by context factory */
    public List<Promotion> getPromotionsByShopCode(final String shopCode, final String currency, final boolean active) {
        return promotionService.getPromotionsByShopCode(shopCode, currency, active);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion findPromotionByCode(final String code, final boolean active) {
        return promotionService.findPromotionByCode(code, active);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findPromotionIdByCode(final String code) {
        return promotionService.findPromotionIdByCode(code);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> findPromotions(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return promotionService.findPromotions(start, offset, sort, sortDescending, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findPromotionCount(final Map<String, List> filter) {
        return promotionService.findPromotionCount(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> findAll() {
        return promotionService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Promotion> callback) {
        promotionService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Promotion> callback) {
        promotionService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion findById(final long pk) {
        return promotionService.findById(pk);
    }


    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
//            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public Promotion create(final Promotion instance) {
        return promotionService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
//            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public Promotion update(final Promotion instance) {
        return promotionService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
//            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public void delete(final Promotion instance) {
        promotionService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> findByCriteria(final String eCriteria, final Object... parameters) {
        return promotionService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return promotionService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return promotionService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<Promotion, Long> getGenericDao() {
        return promotionService.getGenericDao();
    }
}
