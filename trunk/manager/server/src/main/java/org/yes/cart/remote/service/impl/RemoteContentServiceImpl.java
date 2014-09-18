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
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteContentService;
import org.yes.cart.service.dto.DtoContentService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class RemoteContentServiceImpl
        extends AbstractRemoteService<CategoryDTO>
        implements RemoteContentService {


    private final FederationFacade federationFacade;

    /**
     * Construct remote service.
     *
     * @param dtoContentService dto service.
     * @param federationFacade  federation service
     */
    public RemoteContentServiceImpl(
            final DtoContentService dtoContentService,
            final FederationFacade federationFacade) {
        super(dtoContentService);
        this.federationFacade = federationFacade;
    }



    /**
     * {@inheritDoc}
     */
    public void createContentRoot(final long shopId) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoContentService) getGenericDTOService()).createContentRoot(shopId);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoContentService) getGenericDTOService()).getAllByShopId(shopId);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllWithAvailabilityFilter(final long shopId, final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoContentService) getGenericDTOService()).getAllWithAvailabilityFilter(shopId, withAvailabilityFiltering);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CategoryDTO> all = new ArrayList<CategoryDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, CategoryDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            return super.getById(id);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            return super.getById(id, converters);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO create(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getParentId(), CategoryDTO.class)) {
            return super.create(instance);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO update(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getCategoryId(), CategoryDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            super.remove(id);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, CategoryDTO.class)) {
            return ((DtoContentService) getGenericDTOService()).getEntityAttributes(entityPk);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityContentAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, CategoryDTO.class)) {
            return ((DtoContentService) getGenericDTOService()).getEntityContentAttributes(entityPk);
        } else {
            throw new AccessDeniedException("ACCESS DENIED");
        }
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoContentService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


}
