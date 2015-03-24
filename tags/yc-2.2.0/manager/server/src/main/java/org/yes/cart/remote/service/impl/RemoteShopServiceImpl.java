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
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteShopService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteShopServiceImpl
        extends AbstractRemoteService<ShopDTO>
        implements RemoteShopService {

    private final FederationFacade federationFacade;

    /**
     * Construct remote service.
     *
     * @param dtoShopService dto service to use.
     * @param federationFacade federation facade
     */
    public RemoteShopServiceImpl(final DtoShopService dtoShopService,
                                 final FederationFacade federationFacade) {
        super(dtoShopService);
        this.federationFacade = federationFacade;
    }

    /**
     * {@inheritDoc}
     */
    public List<ShopDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ShopDTO> all = new ArrayList<ShopDTO>(super.getAll());
        federationFacade.applyFederationFilter(all, ShopDTO.class);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    public ShopDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(id, ShopDTO.class)) {
            return super.getById(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ShopDTO update(final ShopDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(instance.getShopId(), ShopDTO.class)) {
            return super.update(instance);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        return ((DtoShopService) getGenericDTOService()).getAllSupportedCurrenciesByShops();
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedCurrencies(final long shopId) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoShopService) getGenericDTOService()).getSupportedCurrencies(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateSupportedCurrencies(final long shopId, final String currencies) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoShopService) getGenericDTOService()).updateSupportedCurrencies(shopId, currencies);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedShippingCountries(final long shopId) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoShopService) getGenericDTOService()).getSupportedShippingCountries(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateSupportedShippingCountries(final long shopId, final String countries) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoShopService) getGenericDTOService()).updateSupportedShippingCountries(shopId, countries);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedBillingCountries(final long shopId) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoShopService) getGenericDTOService()).getSupportedBillingCountries(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateSupportedBillingCountries(final long shopId, final String countries) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoShopService) getGenericDTOService()).updateSupportedBillingCountries(shopId, countries);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedLanguages(final long shopId) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            return ((DtoShopService) getGenericDTOService()).getSupportedLanguages(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateSupportedLanguages(final long shopId, final String languages) {
        if (federationFacade.isManageable(shopId, ShopDTO.class)) {
            ((DtoShopService) getGenericDTOService()).updateSupportedLanguages(shopId, languages);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ShopDTO getShopDtoByDomainName(final String serverDomainName) {
        return ((DtoShopService) getGenericDTOService()).getShopDtoByDomainName(serverDomainName);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(entityPk, ShopDTO.class)) {
            return ((DtoShopService) getGenericDTOService()).getEntityAttributes(entityPk);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }
}
