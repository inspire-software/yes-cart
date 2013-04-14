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

import org.yes.cart.domain.dto.ShopExchangeRateDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteShopExchangeRateService;
import org.yes.cart.service.dto.DtoShopExchangeRateService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 2:15 PM
 */
public class RemoteShopExchangeRateServiceImpl
        extends  AbstractRemoteService<ShopExchangeRateDTO>
        implements RemoteShopExchangeRateService  {


    /**
     * Create remote exchange rate service.
     * @param genericDTOService dto generic service to use
     */
    public RemoteShopExchangeRateServiceImpl(final GenericDTOService<ShopExchangeRateDTO> genericDTOService) {
        super(genericDTOService);
    }

    /** {@inheritDoc} */
    public List<ShopExchangeRateDTO> getAllByShopId(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoShopExchangeRateService) getGenericDTOService()).getAllByShopId(shopId);
    }

    /** {@inheritDoc} */
    public int updateDerivedPrices(final long shopId) {

        return ((DtoShopExchangeRateService) getGenericDTOService()).updateDerivedPrices(shopId);

    }
}
