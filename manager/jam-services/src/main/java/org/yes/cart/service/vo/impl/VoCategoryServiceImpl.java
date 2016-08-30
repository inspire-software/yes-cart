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
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAttrValueCategory;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoCategoryService;
import org.yes.cart.service.vo.VoIOSupport;

import java.util.*;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public class VoCategoryServiceImpl implements VoCategoryService {

    private final DtoCategoryService dtoCategoryService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private Set<String> skipAttributesInView = Collections.emptySet();
    private String skipContentAttributesInView = "";

    private final VoAttributesCRUDTemplate<VoAttrValueCategory, AttrValueCategoryDTO> voAttributesCRUDTemplate;

    public VoCategoryServiceImpl(final DtoCategoryService dtoCategoryService,
                                 final DtoAttributeService dtoAttributeService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport,
                                 final VoIOSupport voIOSupport) {
        this.dtoCategoryService = dtoCategoryService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueCategory, AttrValueCategoryDTO>(
                        VoAttrValueCategory.class,
                        AttrValueCategoryDTO.class,
                        Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN,
                        this.dtoCategoryService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
                {
                    @Override
                    protected boolean skipAttributesInView(final String code) {
                        return skipAttributesInView.contains(code) || code.startsWith(skipContentAttributesInView);
                    }

                    @Override
                    protected long determineObjectId(final VoAttrValueCategory vo) {
                        return vo.getCategoryId();
                    }

                    @Override
                    protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId) throws Exception {
                        boolean accessible = federationFacade.isManageable(objectId, CategoryDTO.class);
                        if (!accessible) {
                            return new Pair<>(false, null);
                        }
                        final CategoryDTO category = dtoCategoryService.getById(objectId);
                        return new Pair<>(true, category.getGuid());
                    }
                };
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
    public List<VoCategory> getFiltered(final String filter, final int max) throws Exception {

        final List<VoCategory> results = new ArrayList<>();

        int start = 0;
        do {
            final List<CategoryDTO> batch = dtoCategoryService.findBy(filter, filter, filter, start, max);
            if (batch.isEmpty()) {
                break;
            }
            federationFacade.applyFederationFilter(batch, CategoryDTO.class);
            results.addAll(voAssemblySupport.assembleVos(VoCategory.class, CategoryDTO.class, batch));
            start += max;
        } while (results.size() < max);
        return results.size() > max ? results.subList(0, max) : results;

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


    /**
     * {@inheritDoc}
     */
    public VoCategory update(final VoCategory vo) throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getById(vo.getCategoryId());
        final long categoryId = categoryDTO != null && categoryDTO.getParentId() == vo.getParentId() ? vo.getCategoryId() : vo.getParentId();
        if (categoryDTO != null && federationFacade.isManageable(categoryId, CategoryDTO.class)) {
            CategoryDTO persistent = voAssemblySupport.assembleDto(CategoryDTO.class, VoCategory.class, categoryDTO, vo);
            ensureValidNullValues(persistent);
            dtoCategoryService.update(persistent);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getById(vo.getCategoryId());
    }

    /** {@inheritDoc} */
    public VoCategory create(VoCategory voCategory)  throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getNew();
        if (voCategory != null && federationFacade.isManageable(voCategory.getParentId(), CategoryDTO.class)){
            CategoryDTO persistent = voAssemblySupport.assembleDto(CategoryDTO.class, VoCategory.class, categoryDTO, voCategory);
            ensureValidNullValues(persistent);
            persistent = dtoCategoryService.create(persistent);
            return getById(persistent.getId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private void ensureValidNullValues(final CategoryDTO persistent) {
        if (StringUtils.isBlank(persistent.getUri())) {
            persistent.setUri(null);
        }
        if (persistent.getProductTypeId() != null && persistent.getProductTypeId() == 0) {
            persistent.setProductTypeId(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) throws Exception {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            dtoCategoryService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueCategory> getCategoryAttributes(final long categoryId) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(categoryId);

    }

    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueCategory> update(final List<MutablePair<VoAttrValueCategory, Boolean>> vo) throws Exception {

        final long categoryId = voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo);

        return getCategoryAttributes(categoryId);
    }

    /**
     * Spring IoC
     *
     * @param attributes attributes to skip
     */
    public void setSkipAttributesInView(List<String> attributes) {
        this.skipAttributesInView = new HashSet<>(attributes);
    }

    /**
     * Spring IoC
     *
     * @param contentPrefix attributes to skip
     */
    public void setSkipContentAttributesInView(String contentPrefix) {
        this.skipContentAttributesInView = contentPrefix;
    }
}
