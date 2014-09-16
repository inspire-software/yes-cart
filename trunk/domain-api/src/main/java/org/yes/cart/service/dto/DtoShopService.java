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

import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoShopService extends GenericDTOService<ShopDTO>, GenericAttrValueService {

    /**
     * Get supported currencies by given shop.
     *
     * @param shopId given shop id.
     * @return comma separated list of supported currency codes. Example USD,EUR
     */
    String getSupportedCurrencies(long shopId);

    /**
     * Get all supported currencies by all shops.
     *
     * @return all supported currencies.
     */
    Collection<String> getAllSupportedCurrenciesByShops();

    /**
     * Set supported currencies by given shop.
     *
     * @param shopId     shop id
     * @param currencies comma separated list of supported currency codes. Example USD,EUR
     */
    void updateSupportedCurrencies(long shopId, String currencies);


    /**
     * Get shop by server domain name.
     * @param serverDomainName given domain name.
     * @return shop dto if found otherwise null.
     */
    ShopDTO getShopDtoByDomainName(String serverDomainName);
}
