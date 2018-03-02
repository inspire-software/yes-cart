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
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.service.domain.BrandService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/02/2018
 * Time: 21:55
 */
public class BrandServiceCachedImpl implements BrandService {

    private final BrandService brandService;

    public BrandServiceCachedImpl(final BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-brandById")
    public Brand getById(final long brandId) {
        return brandService.getById(brandId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand findByNameOrGuid(final String nameOrGuid) {
        return brandService.findByNameOrGuid(nameOrGuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Brand> callback) {
        brandService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand findById(final long pk) {
        return brandService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {"productService-brandById"}, allEntries = true)
    public Brand create(final Brand instance) {
        return brandService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {"productService-brandById"}, allEntries = true)
    public Brand update(final Brand instance) {
        return brandService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {"productService-brandById"}, allEntries = true)
    public void delete(final Brand instance) {
        brandService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Brand> findByCriteria(final String eCriteria, final Object... parameters) {
        return brandService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return brandService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return brandService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<Brand, Long> getGenericDao() {
        return brandService.getGenericDao();
    }

}
