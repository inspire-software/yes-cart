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
import org.yes.cart.domain.dto.ShopBackdoorUrlDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopBackdoorUrlDTOImpl;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopBackdoorUrl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoShopBackdoorUrlService;
import org.yes.cart.service.dto.DtoShopUrlService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoShopBackdoorUrlServiceImpl
        extends AbstractDtoServiceImpl<ShopBackdoorUrlDTO, ShopBackdoorUrlDTOImpl, ShopBackdoorUrl>
        implements DtoShopBackdoorUrlService {

    private final GenericService<Shop> shopService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param shopUrlGenericService       {@link org.yes.cart.service.domain.ShopUrlService}
     * @param shopService {@link org.yes.cart.service.domain.ShopService}
     */
    public DtoShopBackdoorUrlServiceImpl(
            final GenericService<ShopBackdoorUrl> shopUrlGenericService,
            final GenericService<Shop> shopService,
            final DtoFactory dtoFactory,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, shopUrlGenericService, adaptersRepository);
        this.shopService = shopService;
    }

    /** {@inheritDoc}     */
    public ShopBackdoorUrlDTO create(final ShopBackdoorUrlDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ShopBackdoorUrl shopUrl = getEntityFactory().getByIface(ShopBackdoorUrl.class);
        assembler.assembleEntity(instance, shopUrl, getAdaptersRepository(), dtoFactory);
        shopUrl.setShop(shopService.getById(instance.getShopId()));
        shopUrl = service.create(shopUrl);        
        return getById(shopUrl.getShopBackdoorUrlId());
    }


    /** {@inheritDoc} */
    public List<ShopBackdoorUrlDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Shop shop = shopService.getById(shopId);
        final List<ShopBackdoorUrlDTO> shopUrlDTOs = new ArrayList<ShopBackdoorUrlDTO>(shop.getShopBackdoorUrl().size());
        fillDTOs(shop.getShopBackdoorUrl(), shopUrlDTOs);
        return shopUrlDTOs;
    }



    /** {@inheritDoc}     */
    public Class<ShopBackdoorUrlDTO> getDtoIFace() {
        return ShopBackdoorUrlDTO.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopBackdoorUrlDTOImpl> getDtoImpl() {
        return ShopBackdoorUrlDTOImpl.class;
    }

    /** {@inheritDoc}     */
    public Class<ShopBackdoorUrl> getEntityIFace() {
        return ShopBackdoorUrl.class;
    }
}
