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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.dto.ProductSkuSearchResultPageDTO;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    @Override
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        return productSkuService.getAllProductSkus(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku findProductSkuBySkuCode(final String skuCode) {
        return productSkuService.findProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productSkuService-productSkuBySkuCode")
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return productSkuService.getProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productSkuService-productSkuSearchResultDTOByQuery")
    public ProductSkuSearchResultPageDTO getProductSkuSearchResultDTOByQuery(final NavigationContext navigationContext) {
        return productSkuService.getProductSkuSearchResultDTOByQuery(navigationContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, SkuPrice>> getAllPrices(final long productId) {
        return productSkuService.getAllPrices(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, SkuWarehouse>> getAllInventory(final long productId) {
        return productSkuService.getAllInventory(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void removeAllInventory(final long productId) {
        productSkuService.removeAllInventory(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void removeAllInventory(final ProductSku sku) {
        productSkuService.removeAllInventory(sku);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllWishLists(final ProductSku sku) {
        productSkuService.removeAllWishLists(sku);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllOptions(final long productId) {
        productSkuService.removeAllOptions(productId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllOptions(final ProductSku sku) {
        productSkuService.removeAllOptions(sku);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSku> findProductSkus(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return productSkuService.findProductSkus(start, offset, sort, sortDescending, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findProductSkuCount(final Map<String, List> filter) {
        return productSkuService.findProductSkuCount(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSku> findAll() {
        return productSkuService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<ProductSku> callback) {
        productSkuService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<ProductSku> callback) {
        productSkuService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku findById(final long pk) {
        return productSkuService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
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
    @Override
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
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse",
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
    @Override
    public List<ProductSku> findByCriteria(final String eCriteria, final Object... parameters) {
        return productSkuService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return productSkuService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return productSkuService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<ProductSku, Long> getGenericDao() {
        return productSkuService.getGenericDao();
    }

}
