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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.*;
import org.yes.cart.exception.ObjectNotFoundException;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToWrapObjectException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteProductService;
import org.yes.cart.service.dto.DtoProductCategoryService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductServiceImpl
        extends AbstractRemoteService<ProductDTO>
        implements RemoteProductService {
    
    private final ReindexService reindexService;
    private final DtoProductCategoryService dtoProductCategoryService;
    private final FederationFacade federationFacade;


    /**
     * Construct remote service.
     *
     * @param dtoProductService dto service.
     * @param reindexService product reindex service
     * @param dtoProductCategoryService cat service
     * @param federationFacade federation facade
     */
    public RemoteProductServiceImpl(
            final DtoProductService dtoProductService,
            final ReindexService reindexService,
            final DtoProductCategoryService dtoProductCategoryService,
            final FederationFacade federationFacade) {
        super(dtoProductService);
        this.reindexService = reindexService;
        this.dtoProductCategoryService = dtoProductCategoryService;
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSkuDTO getProductSkuByCode(final String skuCode)
            throws ObjectNotFoundException, UnableToWrapObjectException {
        final ProductSkuDTO sku = ((DtoProductService) getGenericDTOService()).getProductSkuByCode(skuCode);
        if (sku != null) {
            if (federationFacade.isManageable(sku.getProductId(), ProductDTO.class)) {
                return sku;
            } else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, ProductDTO.class)) {
            AttrValueDTO rez = ((DtoProductService) getGenericDTOService()).createAndBindAttrVal(entityPk, attrName, attrValue);
            reindexService.reindexProduct(entityPk);
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCategory(final long categoryId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(categoryId, CategoryDTO.class)) {
            return ((DtoProductService) getGenericDTOService()).getProductByCategory(categoryId);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCategoryWithPaging(final long categoryId, final int firtsResult, final int maxResults)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ProductDTO> all = new ArrayList<ProductDTO>(((DtoProductService) getGenericDTOService()).getProductByCategoryWithPaging(categoryId, firtsResult, maxResults));
        federationFacade.applyFederationFilter(all, ProductDTO.class);
        return all;
    }


    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCodeNameBrandType(
            final String code,
            final String name,
            final long brandId,
            final long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ProductDTO> all = new ArrayList<ProductDTO>(((DtoProductService) getGenericDTOService()).getProductByCodeNameBrandType(code, name, brandId, productTypeId));
        federationFacade.applyFederationFilter(all, ProductDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, ProductDTO.class)) {
            return ((DtoProductService) getGenericDTOService()).getEntityAttributes(entityPk);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(((AttrValueProductDTO) attrValueDTO).getProductId(), ProductDTO.class)) {
            AttrValueProductDTO rez = (AttrValueProductDTO) ((DtoProductService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
            reindexService.reindexProduct(rez.getProductId());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(((AttrValueProductDTO) attrValueDTO).getProductId(), ProductDTO.class)) {
            AttrValueProductDTO rez = (AttrValueProductDTO)  ((DtoProductService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
            reindexService.reindexProduct(rez.getProductId());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final long productId = ((DtoProductService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
        reindexService.reindexProduct(productId);
        return productId;
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO create(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        // We do not yet try access as this is newly added product and it is not yet associated with categories
        ProductDTO rez =  super.create(instance);
        if (CollectionUtils.isNotEmpty(instance.getProductCategoryDTOs())) {
            final ProductCategoryDTO cat = instance.getProductCategoryDTOs().iterator().next();
            cat.setProductId(rez.getProductId());
            if (federationFacade.isManageable(cat.getCategoryId(), CategoryDTO.class)) {
                // adding this to category is it is manageable
                dtoProductCategoryService.create(cat);
            }
        }
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getProductId(), ProductDTO.class)) {
            ProductDTO rez =  super.update(instance);
            reindexService.reindexProduct(rez.getProductId());
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, ProductDTO.class)) {
            super.remove(id);
            reindexService.reindexProduct(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
