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

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.bridge.support.NavigatableAttributesSupport;

import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 13:46
 */
public class NavigatableAttributesSupportCachedImpl implements NavigatableAttributesSupport {

    private final NavigatableAttributesSupport support;

    public NavigatableAttributesSupportCachedImpl(final NavigatableAttributesSupport support) {
        this.support = support;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allNavigatableAttributeCodes")
    public Set<String> getAllNavigatableAttributeCodes() {
        return support.getAllNavigatableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allSearchableAttributeCodes")
    public Set<String> getAllSearchableAttributeCodes() {
        return support.getAllSearchableAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allSearchablePrimaryAttributeCodes")
    public Set<String> getAllSearchablePrimaryAttributeCodes() {
        return support.getAllSearchablePrimaryAttributeCodes();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "attributeService-allStorableAttributeCodes")
    public Set<String> getAllStorableAttributeCodes() {
        return support.getAllStorableAttributeCodes();
    }
}
