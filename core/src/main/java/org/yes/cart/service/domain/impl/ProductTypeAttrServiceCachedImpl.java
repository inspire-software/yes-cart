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

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.service.domain.ProductTypeAttrService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:15
 */
public class ProductTypeAttrServiceCachedImpl implements ProductTypeAttrService {

    private final ProductTypeAttrService productTypeAttrService;

    public ProductTypeAttrServiceCachedImpl(final ProductTypeAttrService productTypeAttrService) {
        this.productTypeAttrService = productTypeAttrService;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "productTypeAttrService-viewGroupsByProductTypeId")
    public List<ProdTypeAttributeViewGroup> getViewGroupsByProductTypeId(final long productTypeId) {
        return productTypeAttrService.getViewGroupsByProductTypeId(productTypeId);
    }

    /** {@inheritDoc} */
    @Cacheable(value = "productTypeAttrService-byProductTypeId")
    public List<ProductTypeAttr> getByProductTypeId(final long productTypeId) {
        return productTypeAttrService.getByProductTypeId(productTypeId);
    }

    /** {@inheritDoc} */
    @Cacheable(value = "productTypeAttrService-navigatableByProductTypeId")
    public List<ProductTypeAttr> getNavigatableByProductTypeId(final long productTypeId) {
        return productTypeAttrService.getNavigatableByProductTypeId(productTypeId);
    }

    /** {@inheritDoc} */
    public List<ProductTypeAttr> findAll() {
        return productTypeAttrService.findAll();
    }

    /** {@inheritDoc} */
    public ProductTypeAttr findById(final long pk) {
        return productTypeAttrService.findById(pk);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={
            "productTypeAttrService-byProductTypeId",
            "productTypeAttrService-navigatableByProductTypeId",
            "productTypeAttrService-viewGroupsByProductTypeId"
    } , allEntries = true )
    public ProductTypeAttr create(final ProductTypeAttr instance) {
        return productTypeAttrService.create(instance);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={
            "productTypeAttrService-byProductTypeId",
            "productTypeAttrService-navigatableByProductTypeId",
            "productTypeAttrService-viewGroupsByProductTypeId"
    } , allEntries = true )
    public ProductTypeAttr update(final ProductTypeAttr instance) {
        return productTypeAttrService.update(instance);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={
            "productTypeAttrService-byProductTypeId",
            "productTypeAttrService-navigatableByProductTypeId",
            "productTypeAttrService-viewGroupsByProductTypeId"
    } , allEntries = true )
    public void delete(final ProductTypeAttr instance) {
        productTypeAttrService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<ProductTypeAttr> findByCriteria(final Criterion... criterion) {
        return productTypeAttrService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return productTypeAttrService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return productTypeAttrService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public ProductTypeAttr findSingleByCriteria(final Criterion... criterion) {
        return productTypeAttrService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<ProductTypeAttr, Long> getGenericDao() {
        return productTypeAttrService.getGenericDao();
    }
}
