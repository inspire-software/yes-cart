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
import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemotePriceListService;
import org.yes.cart.service.dto.DtoPriceListsService;
import org.yes.cart.service.dto.support.PriceListFilter;
import org.yes.cart.service.federation.FederationFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 12-12-03
 * Time: 6:52 PM
 */
public class RemotePriceListServiceImpl implements RemotePriceListService {

    private final DtoPriceListsService dtoPriceListsService;
    private final FederationFacade federationFacade;

    public RemotePriceListServiceImpl(final DtoPriceListsService dtoPriceListsService,
                                      final FederationFacade federationFacade) {
        this.dtoPriceListsService = dtoPriceListsService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    public List<ShopDTO> getShops() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ShopDTO> shops = new ArrayList<ShopDTO>(dtoPriceListsService.getShops());
        federationFacade.applyFederationFilter(shops, ShopDTO.class);
        return shops;
    }

    /** {@inheritDoc} */
    public List<String> getShopCurrencies(final ShopDTO shop) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (federationFacade.isManageable(shop.getShopId(), ShopDTO.class)) {
            return dtoPriceListsService.getShopCurrencies(shop);
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    public List<PriceListDTO> getPriceList(final PriceListFilter filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (filter.getShop() != null && federationFacade.isManageable(filter.getShop().getShopId(), ShopDTO.class)) {
            return dtoPriceListsService.getPriceList(filter);
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    public PriceListDTO createPrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (price.getShopCode() != null && federationFacade.isManageable(price.getShopCode(), ShopDTO.class)) {
            return dtoPriceListsService.createPrice(price);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    public PriceListDTO updatePrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (price.getShopCode() != null && federationFacade.isManageable(price.getShopCode(), ShopDTO.class)) {
            return dtoPriceListsService.updatePrice(price);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public void removePrice(final long skuPriceId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoPriceListsService.removePrice(skuPriceId);
    }

}
