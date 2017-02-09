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

package org.yes.cart.domain.entity.bridge.support;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 29/05/2015
 * Time: 08:00
 */
public interface SkuPriceRelationshipSupport {

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    Map<Long, Set<Shop>> getAllShopsAndSubs();

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
    List<Shop> getAllNonSub();

    /**
     * Get all entities.
     *
     * @param masterId master PK
     *
     * @return list of all entities
     */
    List<Shop> getAllMastered(long masterId);

    /**
     * Get prices for given sku on all shops.
     *
     * @param sku sku code
     *
     * @return all prices for given sku on the system
     */
    List<SkuPrice> getSkuPrices(String sku);

}
