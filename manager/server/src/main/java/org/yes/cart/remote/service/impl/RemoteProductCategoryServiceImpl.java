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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteProductCategoryService;
import org.yes.cart.service.dto.DtoProductCategoryService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductCategoryServiceImpl
        extends AbstractRemoteService<ProductCategoryDTO>
        implements RemoteProductCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger( RemoteProductCategoryServiceImpl.class );


    private final ReindexService reindexService;
    private final FederationFacade federationFacade;



    /**
     * Construct remote service.
     *
     * @param dtoProductCategoryService dto service to use.
     * @param federationFacade facade
     */
    public RemoteProductCategoryServiceImpl(
            final DtoProductCategoryService dtoProductCategoryService,
            final ReindexService reindexService, final FederationFacade federationFacade) {
        super(dtoProductCategoryService);
        this.reindexService = reindexService;
        this.federationFacade = federationFacade;
    }


    /**
     * {@inheritDoc}
     */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        if (federationFacade.isManageable(categoryId, CategoryDTO.class)) {
            ((DtoProductCategoryService) getGenericDTOService()).removeByCategoryProductIds(categoryId, productId);
            reindexService.reindexProduct(productId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeByProductIds(final long productId) {
        if (federationFacade.isManageable(productId, ProductDTO.class)) {
            ((DtoProductCategoryService) getGenericDTOService()).removeByProductIds(productId);
            reindexService.reindexProduct(productId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
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
            if (federationFacade.isManageable(instance.getCategoryId(), CategoryDTO.class)) {

                ProductCategoryDTO rez = super.create(instance);
                reindexService.reindexProduct(rez.getProductId());
                return rez;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }

        throw new UnableToCreateInstanceException("Product Already assigned to this category ", null);

    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO update(ProductCategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getCategoryId(), CategoryDTO.class)) {
            ProductCategoryDTO rez = super.update(instance);
            reindexService.reindexProduct(rez.getProductId());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
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
    public ProductCategoryDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ProductCategoryDTO cat = super.getById(id);
        if (cat != null) {
            if (federationFacade.isManageable(cat.getCategoryId(), CategoryDTO.class)) {
                return cat;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ProductCategoryDTO cat = super.getById(id, converters);
        if (cat != null) {
            if (federationFacade.isManageable(cat.getCategoryId(), CategoryDTO.class)) {
                return cat;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final long productId = this.getById(id).getProductId();  // checks access
        super.remove(id);
        reindexService.reindexProduct(productId);

    }
}
