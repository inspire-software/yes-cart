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

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCategoryService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCategoryServiceImpl
        extends AbstractRemoteService<CategoryDTO>
        implements RemoteCategoryService {


    private final FederationFacade federationFacade;

    /**
     * Construct remote service.
     *
     * @param dtoCategoryService dto service.
     * @param federationFacade  federation service
     */
    public RemoteCategoryServiceImpl(
            final DtoCategoryService dtoCategoryService,
            final FederationFacade federationFacade) {
        super(dtoCategoryService);
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoCategoryService) getGenericDTOService()).getAllByShopId(shopId);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllWithAvailabilityFilter(final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CategoryDTO> all = new ArrayList<CategoryDTO>(((DtoCategoryService) getGenericDTOService()).getAllWithAvailabilityFilter(withAvailabilityFiltering));
        federationFacade.applyFederationFilter(all, CategoryDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CategoryDTO> all = new ArrayList<CategoryDTO>(super.getAll());
        for (final CategoryDTO root : all) {
            root.setChildren(new ArrayList<CategoryDTO>(root.getChildren()));
            federationFacade.applyFederationFilter(root.getChildren(), CategoryDTO.class);
        }
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            return super.getById(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            return super.getById(id, converters);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO create(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getParentId(), CategoryDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO createForShop(final CategoryDTO category, final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {

            category.setParentId(((CategoryService) getService()).getRootCategory().getCategoryId());
            final CategoryDTO created = super.create(category);
            assignToShop(created.getCategoryId(), shopId);

            return created;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO update(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getCategoryId(), CategoryDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            super.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, CategoryDTO.class)) {
            return ((DtoCategoryService) getGenericDTOService()).getEntityAttributes(entityPk);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(((AttrValueCategoryDTO) attrValueDTO).getCategoryId()); // check access
        return ((DtoCategoryService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        getById(((AttrValueCategoryDTO) attrValueDTO).getCategoryId()); // check access
        return ((DtoCategoryService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public ShopCategoryDTO assignToShop(final long categoryId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCategoryService) getGenericDTOService()).assignToShop(categoryId, shopId);
    }

    /**
     * {@inheritDoc}
     */
    public void unassignFromShop(final long categoryId, final long shopId) {
        ((DtoCategoryService) getGenericDTOService()).unassignFromShop(categoryId, shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getByProductId(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CategoryDTO> all = new ArrayList<CategoryDTO>(((DtoCategoryService) getGenericDTOService()).getByProductId(productId));
        federationFacade.applyFederationFilter(all, CategoryDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


}
