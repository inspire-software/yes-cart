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
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.VoAttrValueBrand;
import org.yes.cart.domain.vo.VoBrand;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoBrandService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoBrandService;
import org.yes.cart.service.vo.VoIOSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 14:29
 */
public class VoBrandServiceImpl implements VoBrandService {

    private final DtoBrandService dtoBrandService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private Set<String> skipAttributesInView = Collections.emptySet();

    private final VoAttributesCRUDTemplate<VoAttrValueBrand, AttrValueBrandDTO> voAttributesCRUDTemplate;

    public VoBrandServiceImpl(final DtoBrandService dtoBrandService,
                              final DtoAttributeService dtoAttributeService,
                              final FederationFacade federationFacade,
                              final VoAssemblySupport voAssemblySupport,
                              final VoIOSupport voIOSupport) {
        this.dtoBrandService = dtoBrandService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueBrand, AttrValueBrandDTO>(
                        VoAttrValueBrand.class,
                        AttrValueBrandDTO.class,
                        this.dtoBrandService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
        {
            @Override
            protected boolean skipAttributesInView(final String code, final boolean includeSecure) {
                return skipAttributesInView.contains(code);
            }

            @Override
            protected long determineObjectId(final VoAttrValueBrand vo) {
                return vo.getBrandId();
            }

            @Override
            protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {
                boolean accessible = true; // federationFacade.isCurrentUserSystemAdmin(); allow brand attributes to be visible by roles
                if (!accessible) {
                    return new Pair<>(false, null);
                }
                final BrandDTO brand = dtoBrandService.getById(objectId);
                return new Pair<>(true, brand.getName().replace(' ', '-'));
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoSearchResult<VoBrand> getFilteredBrands(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoBrand> result = new VoSearchResult<>();
        final List<VoBrand> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                Math.min(filter.getSize(), 100),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter"
        );


        final SearchResult<BrandDTO> batch = dtoBrandService.findBrands(searchContext);
        if (!batch.getItems().isEmpty()) {
            results.addAll(voAssemblySupport.assembleVos(VoBrand.class, BrandDTO.class, batch.getItems()));
        }

        result.setTotal(batch.getTotal());

        return result;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoBrand getBrandById(final long id) throws Exception {
        final BrandDTO brandDTO = dtoBrandService.getById(id);
        if (brandDTO != null && federationFacade.isCurrentUserSystemAdmin()) {
            return voAssemblySupport.assembleVo(VoBrand.class, BrandDTO.class, new VoBrand(), brandDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoBrand updateBrand(final VoBrand vo) throws Exception {
        final BrandDTO brandDTO = dtoBrandService.getById(vo.getBrandId());
        if (brandDTO != null && federationFacade.isCurrentUserSystemAdmin()) {
            dtoBrandService.update(
                    voAssemblySupport.assembleDto(BrandDTO.class, VoBrand.class, brandDTO, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getBrandById(vo.getBrandId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoBrand createBrand(final VoBrand vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            BrandDTO brandDTO = dtoBrandService.getNew();
            brandDTO = dtoBrandService.create(
                    voAssemblySupport.assembleDto(BrandDTO.class, VoBrand.class, brandDTO, vo)
            );
            return getBrandById(brandDTO.getBrandId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBrand(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoBrandService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueBrand> getBrandAttributes(final long brandId) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(brandId, true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueBrand> updateBrandAttributes(final List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception {

        final long brandId = voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, true);

        return getBrandAttributes(brandId);
    }

    /**
     * Spring IoC
     *
     * @param attributes attributes to skip
     */
    public void setSkipAttributesInView(List<String> attributes) {
        this.skipAttributesInView = new HashSet<>(attributes);
    }
}
