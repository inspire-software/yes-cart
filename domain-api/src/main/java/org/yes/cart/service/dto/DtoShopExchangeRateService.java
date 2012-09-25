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
package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ShopExchangeRateDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 1:42 PM
 */
public interface DtoShopExchangeRateService  extends GenericDTOService<ShopExchangeRateDTO> {


    /**
     * Get all exchange rates, that belongs to given shop id
     *
     * @param shopId pk value of shop
     * @return list of shop's currency exchange rates.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<ShopExchangeRateDTO> getAllByShopId(long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Recalculate derived prices. Derived prices - prices not in default currency, for example default shop currency is
     * USD and shop support EUR also, but has not price lists for EUR currency and used currency exchange rate instead.
     * Use delete / insert paragigm instead of insert/update.
     * All prices in not default currency  will be updated.
     *
     * @param shopId            shop
     * @return quantity of created records.
     */
    int updateDerivedPrices(long shopId);

}
