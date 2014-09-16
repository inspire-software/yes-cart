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

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteShopService;
import org.yes.cart.service.dto.DtoShopService;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteShopServiceImpl
        extends AbstractRemoteService<ShopDTO>
        implements RemoteShopService {

    /**
     * Construct remote service.
     *
     * @param dtoShopService dto service to use.
     */
    public RemoteShopServiceImpl(final DtoShopService dtoShopService) {
        super(dtoShopService);
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
        return ((DtoShopService) getGenericDTOService()).getSupportedCurrencies(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public void updateSupportedCurrencies(final long shopId, final String currencies) {
        ((DtoShopService) getGenericDTOService()).updateSupportedCurrencies(shopId, currencies);
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
        return ((DtoShopService) getGenericDTOService()).getEntityAttributes(entityPk);
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
