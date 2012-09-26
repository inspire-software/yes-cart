/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteProductCategoryService;
import org.yes.cart.service.dto.DtoProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductCategoryServiceImpl
        extends AbstractRemoteService<ProductCategoryDTO>
        implements RemoteProductCategoryService {


    private final ReindexService reindexService;


    /**
     * Construct remote service.
     *
     * @param dtoProductCategoryService dto service to use.
     * @param reindexService            product reindex service
     */
    public RemoteProductCategoryServiceImpl(
            final DtoProductCategoryService dtoProductCategoryService,
            final ReindexService reindexService) {
        super(dtoProductCategoryService);
        this.reindexService = reindexService;
    }


    /**
     * {@inheritDoc}
     */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        ((DtoProductCategoryService) getGenericDTOService()).removeByCategoryProductIds(categoryId, productId);
    }

    /**
     * {@inheritDoc}
     */
    public void removeByProductIds(final long productId) {
        ((DtoProductCategoryService) getGenericDTOService()).removeByProductIds(productId);
    }

    /**
     * {@inheritDoc}
     */
    public int getNextRank(final long categoryId) {
        return ((DtoProductCategoryService) getGenericDTOService()).getNextRank(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO create(ProductCategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (!isAssignedCategoryProductIds(instance.getCategoryId(), instance.getProductId())) {
            ProductCategoryDTO rez = super.create(instance);
            reindexService.reindexProduct(rez.getProductId());
            return rez;
        }

        throw new UnableToCreateInstanceException("Product Already assigned to this category ", null);

    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO update(ProductCategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategoryDTO rez = super.update(instance);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAssignedCategoryProductIds(final long categoryId, final long productId) {

        return ((DtoProductCategoryService) getGenericDTOService()).isAssignedCategoryProductIds(categoryId, productId);

    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) {
        super.remove(id);    //Todo reindex
    }
}
