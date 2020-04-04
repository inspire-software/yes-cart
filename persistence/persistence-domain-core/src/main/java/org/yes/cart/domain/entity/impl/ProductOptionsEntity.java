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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductOption;
import org.yes.cart.domain.entity.ProductOptions;
import org.yes.cart.domain.entity.ProductSku;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 17/02/2020
 * Time: 08:38
 */
public class ProductOptionsEntity implements ProductOptions, java.io.Serializable {

    private Product product;
    private boolean configurable = false;
    private Set<ProductOption> configurationOptionInternal = new HashSet<>(0);

    public Product getProduct() {
        return product;
    }

    public void setProduct(final Product product) {
        this.product = product;
    }

    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public void setConfigurable(final boolean configurable) {
        this.configurable = configurable;
    }

    @Override
    public List<ProductOption> getConfigurationOptionForSKU(final String sku) {

        if (this.configurable && CollectionUtils.isNotEmpty(this.product.getSku())) {
            for (final ProductSku pSku : this.product.getSku()) {
                if (sku.equals(pSku.getCode())) {
                    return getConfigurationOptionForSKU(pSku);
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<ProductOption> getConfigurationOptionForSKU(final ProductSku sku) {

        if (this.configurable && sku.getProduct().getProductId() == this.product.getProductId()) {

            final List<ProductOption> options = this.getConfigurationOptionInternal().stream()
                    .filter(option -> option.getSkuCode() == null || sku.getCode().equals(option.getSkuCode()))
                    .collect(Collectors.toList());

            options.sort(Comparator.comparingInt(ProductOption::getRank));

            return options;
        }

        return Collections.emptyList();
    }

    public Set<ProductOption> getConfigurationOptionInternal() {
        return configurationOptionInternal;
    }

    public void setConfigurationOptionInternal(final Set<ProductOption> configurationOptionInternal) {
        this.configurationOptionInternal = configurationOptionInternal;
    }

    @Override
    public Set<ProductOption> getConfigurationOption() {
        if (this.configurable) {
            return this.getConfigurationOptionInternal();
        }
        return Collections.emptySet();
    }

    @Override
    public ProductOption createOrGetConfigurationOption(final String attribute, final String sku) {

        final Optional<ProductOption> opt = this.configurationOptionInternal.stream().filter(po -> attribute.equals(po.getAttributeCode())
                && (StringUtils.isBlank(sku) && StringUtils.isBlank(po.getSkuCode())
                || StringUtils.isNotBlank(sku) && sku.equals(po.getSkuCode()))).findFirst();

        if (opt.isPresent()) {
            return opt.get();
        }

        final ProductOptionEntity poe = new ProductOptionEntity();
        poe.setProduct(this.product);
        poe.setAttributeCode(attribute);
        poe.setSkuCode(sku);
        this.configurationOptionInternal.add(poe);
        return poe;
    }

    @Override
    public boolean removeConfigurationOption(final String attribute, final String sku) {
        return this.configurationOptionInternal.removeIf(next -> attribute.equals(next.getAttributeCode())
                && (StringUtils.isBlank(sku) && StringUtils.isBlank(next.getSkuCode())
                || StringUtils.isNotBlank(sku) && sku.equals(next.getSkuCode())));
    }
    
}
