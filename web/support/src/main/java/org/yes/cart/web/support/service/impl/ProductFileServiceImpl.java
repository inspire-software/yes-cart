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

package org.yes.cart.web.support.service.impl;

import org.springframework.cache.CacheManager;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 * User: Denis Pavlov
 */
public class ProductFileServiceImpl extends AbstractImageServiceImpl implements AttributableImageService {

    public ProductFileServiceImpl(final CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * This service serves SKU and Products, therefore object cache key must include this information.
     *
     * @param attributable attributable entity
     *
     * @return cache key
     */
    @Override
    protected String getEntityObjectCacheKey(final Attributable attributable) {
        return String.valueOf(attributable.getId()).concat(attributable instanceof Product ? "_P" : "_S");
    }

    /** {@inheritDoc}
     * @param attributableOrStrategy*/
    protected String getRepositoryUrlPattern(final Object attributableOrStrategy) {
        return Constants.PRODUCT_FILE_REPOSITORY_URL_PATTERN;
    }


    /** {@inheritDoc}
     * @param attributableOrStrategy*/
    protected String getAttributePrefix(final Object attributableOrStrategy) {
        return AttributeNamesKeys.Product.PRODUCT_FILE_ATTR_NAME_PREFIX;
    }


}
