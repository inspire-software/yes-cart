/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.dto.ShopAliasDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopAliasDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopAlias;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoShopAliasService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/02/2017
 * Time: 14:56
 */
public class DtoShopAliasServiceImpl
        extends AbstractDtoServiceImpl<ShopAliasDTO, ShopAliasDTOImpl, ShopAlias>
        implements DtoShopAliasService {

    private final GenericService<Shop> shopService;

    public DtoShopAliasServiceImpl(final GenericService<ShopAlias> service,
                                   final GenericService<Shop> shopService,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository) {
        super(dtoFactory, service, adaptersRepository);
        this.shopService = shopService;
    }

    /** {@inheritDoc}     */
    public ShopAliasDTO create(final ShopAliasDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ShopAlias shopUrl = getPersistenceEntityFactory().getByIface(ShopAlias.class);
        assembler.assembleEntity(instance, shopUrl, getAdaptersRepository(), dtoFactory);
        shopUrl.setShop(shopService.findById(instance.getShopId()));
        shopUrl = service.create(shopUrl);
        return getById(shopUrl.getStoreAliasId());
    }


    /** {@inheritDoc} */
    public List<ShopAliasDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Shop shop = shopService.findById(shopId);
        final List<ShopAliasDTO> shopAliasDTOs = new ArrayList<ShopAliasDTO>(shop.getShopUrl().size());
        fillDTOs(shop.getShopAlias(), shopAliasDTOs);
        return shopAliasDTOs;
    }



    /** {@inheritDoc}     */
    public Class<ShopAliasDTO> getDtoIFace() {
        return ShopAliasDTO.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopAliasDTOImpl> getDtoImpl() {
        return ShopAliasDTOImpl.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopAlias> getEntityIFace() {
        return ShopAlias.class;
    }
}
