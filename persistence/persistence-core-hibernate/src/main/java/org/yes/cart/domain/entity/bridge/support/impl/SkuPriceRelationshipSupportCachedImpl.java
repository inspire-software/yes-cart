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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.search.dao.support.SkuPriceRelationshipSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 13:49
 */
public class SkuPriceRelationshipSupportCachedImpl implements SkuPriceRelationshipSupport {

    private final SkuPriceRelationshipSupport support;

    public SkuPriceRelationshipSupportCachedImpl(final SkuPriceRelationshipSupport support) {
        this.support = support;
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-allShopsMap")
    public Map<Long, Set<Shop>> getAllShopsAndSubs() {
        return support.getAllShopsAndSubs();
    }


    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-allShops")
    public List<Shop> getAll() {
        return support.getAll();
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-allNonSubShops")
    public List<Shop> getAllNonSub() {
        return support.getAllNonSub();
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "shopService-subShopsByMaster")
    public List<Shop> getAllMastered(final long masterId) {
        return support.getAllMastered(masterId);
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuPrice> getSkuPrices(final String sku) {
        return support.getSkuPrices(sku);
    }
}
