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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoCategoryService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public class VoCategoryServiceImpl implements VoCategoryService {

    private final DtoCategoryService dtoCategoryService;

    private final FederationFacade federationFacade;

    private final VoAssemblySupport voAssemblySupport;

    /**
     * Construct service.
     * @param dtoCategoryService dto service to use.
     * @param federationFacade  access.
     * @param voAssemblySupport vo assembly
     */
    public VoCategoryServiceImpl(final DtoCategoryService dtoCategoryService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport) {
        this.dtoCategoryService = dtoCategoryService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    /** {@inheritDoc} */
    public List<VoCategory> getAll() throws Exception {
        final List<CategoryDTO> categoryDTOs = dtoCategoryService.getAll();
        for (final CategoryDTO root : categoryDTOs) {
            applyFilterToCategoryTree(root);
        }
        final List<VoCategory> voCategories = new ArrayList<>(categoryDTOs.size());
        adaptCategories(categoryDTOs, voCategories);
        return voCategories;
    }

    private boolean applyFilterToCategoryTree(final CategoryDTO root) {
        if (!federationFacade.isManageable(root.getCategoryId(), CategoryDTO.class)) {
            // This is not a manageable directory (but maybe children are)
            if (CollectionUtils.isNotEmpty(root.getChildren())) {
                final List<CategoryDTO> all = new ArrayList<CategoryDTO>(root.getChildren());
                final Iterator<CategoryDTO> catIt = all.iterator();
                while (catIt.hasNext()) {
                    final CategoryDTO cat = catIt.next();
                    if (applyFilterToCategoryTree(cat)) {
                        catIt.remove();
                    }
                }
                root.setChildren(all);
                return all.isEmpty(); // Id we have at least one accessible descendant, we should see it

            }
            // This is not manageable
            return true;
        }
        // Manageable
        return false;
    }

    /**
     * Adapt dto to vo recursively.
     * @param categoryDTOs list of dto
     * @param voCategories list of vo
     */
    private void adaptCategories(List<CategoryDTO> categoryDTOs, List<VoCategory> voCategories) {
        for(CategoryDTO dto : categoryDTOs) {
            VoCategory voCategory =
                    voAssemblySupport.assembleVo(VoCategory.class, CategoryDTO.class, new VoCategory(), dto);
            voCategories.add(voCategory);
            voCategory.setChildren(new ArrayList<VoCategory>(dto.getChildren().size()));
            adaptCategories(dto.getChildren(), voCategory.getChildren());
        }
    }


    /** {@inheritDoc} */
    public VoCategory getById(long id) throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getById(id);
        if (categoryDTO != null && federationFacade.isManageable(id, CategoryDTO.class)){
            return voAssemblySupport.assembleVo(VoCategory.class, CategoryDTO.class, new VoCategory(), categoryDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /** {@inheritDoc} */
    public VoCategory create(VoCategory voCategory)  throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getNew();
        if (voCategory != null && federationFacade.isManageable(voCategory.getParentId(), CategoryDTO.class)){
            final CategoryDTO persistent = dtoCategoryService.create(
                    voAssemblySupport.assembleDto(CategoryDTO.class, VoCategory.class, categoryDTO, voCategory)
            );
            return getById(persistent.getId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
