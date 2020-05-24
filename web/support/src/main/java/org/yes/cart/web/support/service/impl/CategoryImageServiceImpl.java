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

package org.yes.cart.web.support.service.impl;

import org.springframework.cache.CacheManager;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:42 PM
 */
public class CategoryImageServiceImpl extends AbstractImageServiceImpl implements AttributableImageService {

    public CategoryImageServiceImpl(final CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * {@inheritDoc}
     * @param attributableOrStrategy
     */
    @Override
    protected String getRepositoryUrlPattern(final Object attributableOrStrategy) {
        return Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN;
    }


    /**
     * {@inheritDoc}
     * @param attributableOrStrategy
     */
    @Override
    protected String getAttributePrefix(final Object attributableOrStrategy) {
        return AttributeNamesKeys.Category.CATEGORY_IMAGE_PREFIX;
    }

}
