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
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.bridge.support.NavigatableAttributesSupport;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 17:08
 */
public class NavigatableAttributesSupportImpl implements NavigatableAttributesSupport {

    private final GenericDAO<Attribute, Long> attributeDao;

    public NavigatableAttributesSupportImpl(final GenericDAO<Attribute, Long> attributeDao) {
        this.attributeDao = attributeDao;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllNavigatableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.NAVIGATION.UNIQUE", Boolean.TRUE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.BRAND_FIELD);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_PRICE);
        allowedAttributeNames.add(ProductSearchQueryBuilder.QUERY);
        allowedAttributeNames.add(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllSearchableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.SEARCH.UNIQUE", Boolean.TRUE, Boolean.FALSE);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllSearchablePrimaryAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.SEARCH.PRIMARY.UNIQUE", Boolean.TRUE, Boolean.TRUE);
        return new HashSet<String>(allowedAttributeNames);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllStorableAttributeCodes() {
        final List allowedAttributeNames = attributeDao.findQueryObjectByNamedQuery("ATTRIBUTE.CODES.STORE.UNIQUE", Boolean.TRUE);
        return new HashSet<String>(allowedAttributeNames);
    }
}
