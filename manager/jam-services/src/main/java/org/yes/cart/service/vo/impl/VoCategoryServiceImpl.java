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

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.federation.FederationFacade;
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

    private final Assembler simpleVoCategoryAssembler;

    /**
     * Construct service.
     * @param dtoCategoryService dto service to use.
     * @param federationFacade  access.
     */
    public VoCategoryServiceImpl(final DtoCategoryService dtoCategoryService,
                                 final FederationFacade federationFacade) {
        this.dtoCategoryService = dtoCategoryService;
        this.federationFacade = federationFacade;
        this.simpleVoCategoryAssembler = DTOAssembler.newAssembler(VoCategory.class, CategoryDTO.class);
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
            VoCategory voCategory = new VoCategory();
            simpleVoCategoryAssembler.assembleDto(voCategory, dto, null, null);
            voCategories.add(voCategory);
            voCategory.setChildren(new ArrayList<VoCategory>(dto.getChildren().size()));
            adaptCategories(dto.getChildren(), voCategory.getChildren());
        }
    }


    /** {@inheritDoc} */
    public VoCategory getById(long id) throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getById(id);
        final VoCategory voCategory = new VoCategory();
        simpleVoCategoryAssembler.assembleDto(voCategory, categoryDTO, null ,null);
        return voCategory;
    }


    /** {@inheritDoc} */
    public VoCategory create(VoCategory voCategory)  throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getNew();
        simpleVoCategoryAssembler.assembleEntity(voCategory, categoryDTO, null, null);
        final CategoryDTO persistent = dtoCategoryService.create(categoryDTO);
        return getById(persistent.getId());
    }
}
