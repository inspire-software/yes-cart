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
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoShopUrlService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoShopUrlServiceImpl
        extends AbstractDtoServiceImpl<ShopUrlDTO, ShopUrlDTOImpl, ShopUrl>
        implements DtoShopUrlService {

    private final GenericService<Shop> shopService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param shopUrlGenericService       {@link org.yes.cart.service.domain.ShopUrlService}
     * @param shopService {@link org.yes.cart.service.domain.ShopService}
     */
    public DtoShopUrlServiceImpl(
            final GenericService<ShopUrl> shopUrlGenericService,
            final GenericService<Shop> shopService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, shopUrlGenericService, adaptersRepository);
        this.shopService = shopService;
    }

    /** {@inheritDoc}     */
    public ShopUrlDTO create(final ShopUrlDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ShopUrl shopUrl = getEntityFactory().getByIface(ShopUrl.class);
        assembler.assembleEntity(instance, shopUrl, getAdaptersRepository(), dtoFactory);
        shopUrl.setShop(shopService.findById(instance.getShopId()));
        shopUrl = service.create(shopUrl);        
        return getById(shopUrl.getStoreUrlId());
    }


    /** {@inheritDoc} */
    public List<ShopUrlDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Shop shop = shopService.findById(shopId);
        final List<ShopUrlDTO> shopUrlDTOs = new ArrayList<ShopUrlDTO>(shop.getShopUrl().size());
        fillDTOs(shop.getShopUrl(), shopUrlDTOs);
        return shopUrlDTOs;
    }



    /** {@inheritDoc}     */
    public Class<ShopUrlDTO> getDtoIFace() {
        return ShopUrlDTO.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopUrlDTOImpl> getDtoImpl() {
        return ShopUrlDTOImpl.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopUrl> getEntityIFace() {
        return ShopUrl.class;
    }
}
