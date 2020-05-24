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

package org.yes.cart.domain.entity.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.entity.ProductAttributesModel;
import org.yes.cart.domain.entity.ProductAttributesModelAttribute;
import org.yes.cart.domain.entity.ProductAttributesModelGroup;

import java.util.*;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:51
 */
public class ProductAttributesModelImpl implements ProductAttributesModel {

    private final long productId;
    private final String productCode;
    private final long skuId;
    private final String skuCode;
    private final long productType;
    private final List<ProductAttributesModelGroup> groups = new ArrayList<>();
    private final Map<String, ProductAttributesModelGroup> map = new HashMap<>();

    public ProductAttributesModelImpl(final long productId, final String productCode, final long skuId, final String skuCode, final long productType) {
        this.productId = productId;
        this.productCode = productCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.productType = productType;
    }

    public ProductAttributesModelImpl(final long productId, final String productCode, final long skuId, final String skuCode, final long productType, final List<ProductAttributesModelGroup> groups) {
        this.productId = productId;
        this.productCode = productCode;
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.productType = productType;
        if (CollectionUtils.isNotEmpty(groups)) {
            for (final ProductAttributesModelGroup group : groups) {
                this.map.put(group.getCode(), group);
                this.groups.add(group);
            }
        }
    }



    /** {@inheritDoc} */
    @Override
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    @Override
    public String getProductCode() {
        return productCode;
    }

    /** {@inheritDoc} */
    @Override
    public long getSkuId() {
        return skuId;
    }

    /** {@inheritDoc} */
    @Override
    public String getSkuCode() {
        return skuCode;
    }

    /** {@inheritDoc} */
    @Override
    public long getProductType() {
        return productType;
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductAttributesModelGroup> getGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAttributesModelGroup getGroup(final String code) {
        return this.map.get(code);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductAttributesModelAttribute> getAttributes(final String code) {
        final List<ProductAttributesModelAttribute> attrs = new ArrayList<>();
        for (final ProductAttributesModelGroup group : this.groups) {
            final ProductAttributesModelAttribute attr = group.getAttribute(code);
            if (attr != null) {
                attrs.add(attr);
            }
        }
        return attrs;
    }

}
