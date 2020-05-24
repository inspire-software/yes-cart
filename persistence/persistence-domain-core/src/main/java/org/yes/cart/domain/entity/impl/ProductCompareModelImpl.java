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

package org.yes.cart.domain.entity.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.ProductCompareModel;
import org.yes.cart.domain.entity.ProductCompareModelAttribute;
import org.yes.cart.domain.entity.ProductCompareModelGroup;

import java.util.*;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:51
 */
public class ProductCompareModelImpl implements ProductCompareModel {

    private final List<ProductCompareModelGroup> groups = new ArrayList<>();
    private final Map<String, ProductCompareModelGroup> map = new HashMap<>();

    public ProductCompareModelImpl() {
    }

    public ProductCompareModelImpl(final List<ProductCompareModelGroup> groups) {
        if (CollectionUtils.isNotEmpty(groups)) {
            for (final ProductCompareModelGroup group : groups) {
                addGroup(group);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCompareModelGroup> getGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCompareModelGroup getGroup(final String code) {
        return this.map.get(code);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCompareModelAttribute> getAttributes(final String code) {
        final List<ProductCompareModelAttribute> attrs = new ArrayList<>();
        for (final ProductCompareModelGroup group : this.groups) {
            final ProductCompareModelAttribute attr = group.getAttribute(code);
            if (attr != null) {
                attrs.add(attr);
            }
        }
        return attrs;
    }

    public void addGroup(final ProductCompareModelGroup group) {
        this.map.put(group.getCode(), group);
        this.groups.add(group);
    }

}
