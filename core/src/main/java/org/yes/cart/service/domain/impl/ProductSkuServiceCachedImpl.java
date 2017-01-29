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

package org.yes.cart.service.domain.impl;

import org.apache.lucene.search.Query;
import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:07
 */
public class ProductSkuServiceCachedImpl implements ProductSkuService {

    private final ProductSkuService productSkuService;

    public ProductSkuServiceCachedImpl(final ProductSkuService productSkuService) {
        this.productSkuService = productSkuService;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        return productSkuService.getAllProductSkus(productId);
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku findProductSkuBySkuCode(final String skuCode) {
        return productSkuService.findProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productSkuService-productSkuBySkuCode")
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return productSkuService.getProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productSkuService-productSkuSearchResultDTOByQuery")
    public List<ProductSkuSearchResultDTO> getProductSkuSearchResultDTOByQuery(final Query query) {
        return productSkuService.getProductSkuSearchResultDTOByQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, SkuPrice>> getAllPrices(final long productId) {
        return productSkuService.getAllPrices(productId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, SkuWarehouse>> getAllInventory(final long productId) {
        return productSkuService.getAllInventory(productId);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public void removeAllPrices(final long productId) {
        productSkuService.removeAllPrices(productId);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public void removeAllPrices(final ProductSku sku) {
        productSkuService.removeAllPrices(sku);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
            "skuWarehouseService-productOnWarehouse"
    }, allEntries = true)
    public void removeAllInventory(final long productId) {
        productSkuService.removeAllInventory(productId);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
            "skuWarehouseService-productOnWarehouse"
    }, allEntries = true)
    public void removeAllInventory(final ProductSku sku) {
        productSkuService.removeAllInventory(sku);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductSku> findAll() {
        return productSkuService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku findById(final long pk) {
        return productSkuService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
            "skuWarehouseService-productOnWarehouse",
            "productSkuService-productSkuBySkuCode",
            "productService-skuById",
            "productService-productById"
    }, allEntries = true)
    public ProductSku create(final ProductSku instance) {
        return productSkuService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public ProductSku update(final ProductSku instance) {
        return productSkuService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
            "skuWarehouseService-productOnWarehouse",
            "productSkuService-productSkuBySkuCode",
            "productService-skuById",
            "productService-productById"
    }, allEntries = true)
    public void delete(final ProductSku instance) {
        productSkuService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductSku> findByCriteria(final Criterion... criterion) {
        return productSkuService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return productSkuService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return productSkuService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku findSingleByCriteria(final Criterion... criterion) {
        return productSkuService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<ProductSku, Long> getGenericDao() {
        return productSkuService.getGenericDao();
    }

}
