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

package org.yes.cart.service.federation.impl;

import org.springframework.cache.CacheManager;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 16:25
 */
public class ContentImpexFederationFilterImpl extends CacheableImpexFederationFacadeImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final ShopService shopService;

    public ContentImpexFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                            final List<String> roles,
                                            final ShopService shopService,
                                            final CacheManager cacheManager,
                                            final List<String> cachesToFlushForTransientEntities) {
        super(shopFederationStrategy, roles, cacheManager, cachesToFlushForTransientEntities);
        this.shopFederationStrategy = shopFederationStrategy;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        if (!hasAccessRole()) {
            return false;
        }

        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        final Set<Long> manageableContentIds = new HashSet<>();
        for (final Long shopId : manageableShopIds) {
            manageableContentIds.addAll(shopService.getShopContentIds(shopId));
        }

        final Content cn = (Content) object;
        final Long cnId = cn.getContentId();
        final Long parentCatId = cn.getParentId();

        final boolean isNew = isTransientEntity(cn);

        // Must have access to parent and if persistent the to category as well (e.g. if changing parent during import)
        final boolean accessible = (isNew && manageableContentIds.contains(parentCatId)) ||
                (!isNew && manageableContentIds.contains(cnId));

        // Must flush caches since there may be other categories that are children to currently added one
        flushCachesIfTransient(cn);

        return accessible;

    }

}
