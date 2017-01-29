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
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.PromotionService;

import java.util.List;

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
    @Cacheable(value = "promotionService-promotionsByShopCode")
    public List<Promotion> getPromotionsByShopCode(final String shopCode, final String currency, final boolean active) {
        return promotionService.getPromotionsByShopCode(shopCode, currency, active);
    }

    /** {@inheritDoc} */
    public List<Promotion> findByParameters(final String code,
                                            final String shopCode,
                                            final String currency,
                                            final String tag,
                                            final String type,
                                            final String action,
                                            final Boolean enabled) {
        return promotionService.findByParameters(code, shopCode, currency, tag, type, action, enabled);
    }

    /**
     * {@inheritDoc}
     */
    public Long findPromotionIdByCode(final String code) {
        return promotionService.findPromotionIdByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Promotion> findAll() {
        return promotionService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Promotion findById(final long pk) {
        return promotionService.findById(pk);
    }


    /** {@inheritDoc} */
    @CacheEvict(value = {
            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public Promotion create(final Promotion instance) {
        return promotionService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public Promotion update(final Promotion instance) {
        return promotionService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "promotionService-promotionsByShopCode",
            "promotionService-factoryGetInstance",
            "promotionService-groovyCache"
    }, allEntries = true)
    public void delete(final Promotion instance) {
        promotionService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<Promotion> findByCriteria(final Criterion... criterion) {
        return promotionService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return promotionService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return promotionService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public Promotion findSingleByCriteria(final Criterion... criterion) {
        return promotionService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<Promotion, Long> getGenericDao() {
        return promotionService.getGenericDao();
    }
}
