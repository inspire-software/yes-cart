/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoShopCategoryService;

import java.util.List;

/**
 * Created by Igor_Azarny on 5/3/2016.
 */
public class VoShopCategoryServiceImpl implements VoShopCategoryService {

    private final DtoCategoryService dtoCategoryService;
    private final DtoShopService dtoShopService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoShopCategoryServiceImpl(final DtoCategoryService dtoCategoryService,
                                     final DtoShopService dtoShopService,
                                     final FederationFacade federationFacade,
                                     final VoAssemblySupport voAssemblySupport) {
        this.dtoCategoryService = dtoCategoryService;
        this.federationFacade = federationFacade;
        this.dtoShopService = dtoShopService;
        this.voAssemblySupport = voAssemblySupport;
    }



    /** {@inheritDoc} */
    @Override
    public List<VoCategory> getAllByShopId(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            final List<CategoryDTO> categoryDTOs = dtoCategoryService.getAllByShopId(shopId);
            return voAssemblySupport.assembleVos(VoCategory.class, CategoryDTO.class, categoryDTOs);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<VoCategory> update(long shopId, List<VoCategory> voCategories) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            List<VoCategory> oldAssigned = getAllByShopId(shopId);
            for (VoCategory cat : oldAssigned) {
                dtoCategoryService.unassignFromShop(cat.getCategoryId(), shopId);
            }
            for (VoCategory cat : voCategories) {
                dtoCategoryService.assignToShop(cat.getCategoryId(), shopId);
            }
            return getAllByShopId(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

}
