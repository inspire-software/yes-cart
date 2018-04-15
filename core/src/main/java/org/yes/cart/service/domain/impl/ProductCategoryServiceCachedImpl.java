
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

import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.service.domain.ProductCategoryService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 15/04/2018
 * Time: 23:00
 */
public class ProductCategoryServiceCachedImpl implements ProductCategoryService {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryServiceCachedImpl(final ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    /** {@inheritDoc} */
    @Override
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        productCategoryService.removeByCategoryProductIds(categoryId, productId);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCategory findByCategoryIdProductId(final long categoryId, final long productId) {
        return productCategoryService.findByCategoryIdProductId(categoryId, productId);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "productService-productCategoriesIds")
    public List<Long> getByProductId(final long productId) {
        return productCategoryService.getByProductId(productId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCategory> findByProductId(final long productId) {
        return productCategoryService.findByProductId(productId);
    }

    /** {@inheritDoc} */
    @Override
    public void removeByProductIds(final long productId) {
        productCategoryService.removeByProductIds(productId);
    }

    /** {@inheritDoc} */
    @Override
    public int getNextRank(final long categoryId) {
        return productCategoryService.getNextRank(categoryId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCategory> findAll() {
        return productCategoryService.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<ProductCategory> callback) {
        productCategoryService.findAllIterator(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<ProductCategory> callback) {
        productCategoryService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCategory findById(final long pk) {
        return productCategoryService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCategory create(final ProductCategory instance) {
        return productCategoryService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCategory update(final ProductCategory instance) {
        return productCategoryService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final ProductCategory instance) {
        productCategoryService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductCategory> findByCriteria(final String eCriteria, final Object... parameters) {
        return productCategoryService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return productCategoryService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public ProductCategory findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return productCategoryService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<ProductCategory, Long> getGenericDao() {
        return productCategoryService.getGenericDao();
    }
}
