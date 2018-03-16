
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
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.service.domain.ProductAssociationService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 17:39
 */
public class ProductAssociationServiceCachedImpl implements ProductAssociationService {

    private final ProductAssociationService productAssociationService;

    public ProductAssociationServiceCachedImpl(final ProductAssociationService productAssociationService) {
        this.productAssociationService = productAssociationService;
    }

    /**
     * Get all product associations.
     *
     * @param productId product primary key
     * @return list of product associations
     */
    @Override
    public List<ProductAssociation> findProductAssociations(final Long productId) {
        return productAssociationService.findProductAssociations(productId);
    }

    /**
     * Get all product associations by association type.
     *
     * For storefront use getProductAssociationsIds() and then ProductQueryBuilderImpl with FT search
     *
     * @param productId       product primary key
     * @param associationCode association code [up, cross, etc]
     * @return list of product associations
     */
    @Override
    public List<ProductAssociation> findProductAssociations(final Long productId, final String associationCode) {
        return productAssociationService.findProductAssociations(productId, associationCode);
    }

    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param associationCode association code [up, cross, etc]
     * @return list of product associations
     */
    @Override
    @Cacheable(value = "productService-productAssociationsIds")
    public List<String> getProductAssociationsProductCodes(final Long productId, final String associationCode) {
        return productAssociationService.getProductAssociationsProductCodes(productId, associationCode);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductAssociation> findAll() {
        return productAssociationService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<ProductAssociation> callback) {
        productAssociationService.findAllIterator(callback);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAssociation findById(final long pk) {
        return productAssociationService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAssociation create(final ProductAssociation instance) {
        return productAssociationService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAssociation update(final ProductAssociation instance) {
        return productAssociationService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final ProductAssociation instance) {
        productAssociationService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductAssociation> findByCriteria(final String eCriteria, final Object... parameters) {
        return productAssociationService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return productAssociationService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public ProductAssociation findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return productAssociationService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<ProductAssociation, Long> getGenericDao() {
        return productAssociationService.getGenericDao();
    }
}
