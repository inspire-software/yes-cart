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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoShopService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoShopServiceImpl
        extends AbstractDtoServiceImpl<ShopDTO, ShopDTOImpl, Shop>
        implements DtoShopService {

    private final CustomerService customerService;


    public DtoShopServiceImpl(
            final ShopService shopService,
            final CustomerService customerService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, shopService, adaptersRepository);

        this.customerService = customerService;

    }


    /** {@inheritDoc} */
    public String getSupportedCurrencies(final long shopId) {
        return service.getById(shopId).getSupportedCurrencies();
    }

    /** {@inheritDoc} */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        return ((ShopService)service).getAllSupportedCurrenciesByShops();
    }

    /**
     * Set supported currencies by given shop.
     * @param shopId shop id
     * @param currencies comma separated list of supported currency codes. Example USD,EUR
     */
    public void updateSupportedCurrencies(final long shopId, final String currencies) {
        ((ShopService) service).updateAttributeValue(
                shopId,
                AttributeNamesKeys.SUPPORTED_CURRENCIES,
                currencies);
    }

    /** {@inheritDoc} */
    public ShopDTO getShopDtoByDomainName(final String serverDomainName) {
        final Shop shop =((ShopService)service).getShopByDomainName(serverDomainName);
        final ShopDTO dto = (ShopDTO) dtoFactory.getByIface(getDtoIFace());
        getAssembler().assembleDto(dto, shop, getAdaptersRepository(), getDtoFactory());
        return dto;
    }

    /** {@inheritDoc} */
    public List<ShopDTO> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<Shop> rez = new ArrayList<Shop>();
        for (CustomerShop customerShop : customerService.getById(customerId).getShops()) {
            rez.add(customerShop.getShop());
        }
        return getDTOs(rez);

    }


    /** {@inheritDoc} */
    public Class<ShopDTO> getDtoIFace() {
        return ShopDTO.class;
    }

    /** {@inheritDoc} */
    public Class<ShopDTOImpl> getDtoImpl() {
        return ShopDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Shop> getEntityIFace() {
        return Shop.class;
    }


}
