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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.bridge.support.SkuPriceRelationshipSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 29/05/2015
 * Time: 08:02
 */
public class SkuPriceRelationshipSupportImpl implements SkuPriceRelationshipSupport {

    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;

    public SkuPriceRelationshipSupportImpl(final GenericDAO<Shop, Long> shopDao,
                                           final GenericDAO<SkuPrice, Long> skuPriceDao) {
        this.shopDao = shopDao;
        this.skuPriceDao = skuPriceDao;
    }

    /** {@inheritDoc} */
    public Map<Long, Set<Shop>> getAllShopsAndSubs() {
        final List<Shop> all = this.self().getAll();
        final Map<Long, Set<Shop>> shopsMap = new HashMap<Long, Set<Shop>>();
        for (final Shop shop : all) {
            if (shop.getMaster() == null) {
                final Set<Shop> subs = shopsMap.get(shop.getShopId());
                if (subs == null) {
                    shopsMap.put(shop.getShopId(), new HashSet<Shop>());
                }
            } else {
                Set<Shop> subs = shopsMap.get(shop.getMaster().getShopId());
                if (subs == null) {
                    subs = new HashSet<Shop>();
                    shopsMap.put(shop.getMaster().getShopId(), subs);
                }
                subs.add(shop);
            }
        }
        return shopsMap;
    }


    /** {@inheritDoc} */
    public List<Shop> getAll() {
        return this.shopDao.findAll();
    }

    /** {@inheritDoc} */
    public List<Shop> getAllNonSub() {
        return this.shopDao.findByNamedQuery("SHOP.NONSUB.ONLY");
    }

    /** {@inheritDoc} */
    public List<Shop> getAllMastered(final long masterId) {
        return this.shopDao.findByNamedQuery("SHOP.BY.MASTER.ID", masterId);
    }

    /** {@inheritDoc} */
    public List<SkuPrice> getSkuPrices(final String sku) {
        return skuPriceDao.findByNamedQuery("SKUPRICE.BY.SKUCODE.ALL", sku);
    }


    private SkuPriceRelationshipSupport self;

    private SkuPriceRelationshipSupport self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public SkuPriceRelationshipSupport getSelf() {
        return null;
    }
}
