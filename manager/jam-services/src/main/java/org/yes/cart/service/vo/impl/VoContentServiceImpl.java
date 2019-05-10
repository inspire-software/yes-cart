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

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoContentService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoContentService;
import org.yes.cart.service.vo.VoIOSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 07/09/2016
 * Time: 18:28
 */
public class VoContentServiceImpl implements VoContentService {

    private final DtoContentService dtoContentService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private Set<String> skipAttributesInView = Collections.emptySet();
    private String skipContentAttributesInView = "";

    private final VoAttributesCRUDTemplate<VoAttrValueContent, AttrValueContentDTO> voAttributesCRUDTemplate;

    public VoContentServiceImpl(final DtoContentService dtoContentService,
                                 final DtoAttributeService dtoAttributeService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport,
                                 final VoIOSupport voIOSupport) {
        this.dtoContentService = dtoContentService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueContent, AttrValueContentDTO>(
                        VoAttrValueContent.class,
                        AttrValueContentDTO.class,
                        this.dtoContentService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
                {
                    @Override
                    protected boolean skipAttributesInView(final String code, final boolean includeSecure) {
                        return skipAttributesInView.contains(code) || code.startsWith(skipContentAttributesInView);
                    }

                    @Override
                    protected long determineObjectId(final VoAttrValueContent vo) {
                        return vo.getContentId();
                    }

                    @Override
                    protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {
                        boolean accessible = federationFacade.isManageable(objectId, CategoryDTO.class);
                        if (!accessible) {
                            return new Pair<>(false, null);
                        }
                        final ContentDTO category = dtoContentService.getById(objectId);
                        if (StringUtils.isNotBlank(category.getUri())) {
                            return new Pair<>(true, category.getUri());
                        }
                        return new Pair<>(true, category.getGuid());
                    }
                };
    }

    @Override
    public List<VoContent> getAll(final long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final List<ContentDTO> content = dtoContentService.getAllByShopId(shopId);
            final List<VoContent> voContent = new ArrayList<>(content.size());
            adaptContent(content, voContent);
            return voContent;
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public List<VoContent> getBranch(final long shopId, final long contentId, final List<Long> expanded) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final List<ContentDTO> contentDTOs = dtoContentService.getBranchById(shopId, contentId, expanded);
            final List<VoContent> vos = new ArrayList<>();
            contentDTOs.removeIf(categoryDTO -> !federationFacade.isManageable(categoryDTO.getContentId(), ContentDTO.class));
            adaptContent(contentDTOs, vos);
            return vos;
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Long> getBranchPaths(final long shopId, final long contentId) throws Exception {
        final List<Long> path = new ArrayList<>();
        if (federationFacade.isShopAccessibleByCurrentManager(shopId) && federationFacade.isManageable(contentId, CategoryDTO.class)) {

            path.add(contentId);

            final long parentId = dtoContentService.getById(contentId).getParentId();

            if (contentId != parentId && parentId > 0) {
                path.addAll(getBranchPaths(shopId, parentId));
            }
        }
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public List<Long> getBranchesPaths(final long shopId, final List<Long> contentIds) throws Exception {
        final List<Long> path = new ArrayList<>();
        if (contentIds != null) {
            for (final Long categoryId : contentIds) {
                path.addAll(getBranchPaths(shopId, categoryId));
            }
        }
        return path;
    }

    /**
     * Adapt dto to vo recursively.
     * @param content list of dto
     * @param voContent list of vo
     */
    private void adaptContent(List<ContentDTO> content, List<VoContent> voContent) {
        for(ContentDTO dto : content) {
            VoContent voCategory =
                    voAssemblySupport.assembleVo(VoContent.class, ContentDTO.class, new VoContent(), dto);
            voContent.add(voCategory);
            if (dto.getChildren() != null) {
                voCategory.setChildren(new ArrayList<>(dto.getChildren().size()));
                adaptContent(dto.getChildren(), voCategory.getChildren());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<VoContent> getFiltered(final long shopId, final String filter, final int max) throws Exception {
        if (federationFacade.isManageable(shopId, ShopDTO.class)){

            final List<ContentDTO> contentDTO = dtoContentService.findBy(shopId, filter, 0, max);
            return voAssemblySupport.assembleVos(VoContent.class, ContentDTO.class, contentDTO);

        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public void fillShopSummaryDetails(final VoShopSummary summary, final long shopId, final String lang) throws Exception {
        if (federationFacade.isManageable(summary.getShopId(), ShopDTO.class)){

            for (final MutablePair<String, Boolean> shopEmail : summary.getEmailTemplatesShop()) {

                final String code = StringUtils.isBlank(summary.getMasterCode()) ? summary.getCode() : summary.getMasterCode();
                final String uri = code.concat("_mail_").concat(shopEmail.getFirst()).concat(".html");

                final boolean noOverride = dtoContentService.isUriAvailableForContent(uri, 0L);
                shopEmail.setSecond(!noOverride);


            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoContentWithBody getById(final long id) throws Exception {
        final ContentDTO content = dtoContentService.getById(id);
        if (content != null && federationFacade.isManageable(id, CategoryDTO.class)){
            final VoContentWithBody contentWithBody = voAssemblySupport.assembleVo(VoContentWithBody.class, ContentDTO.class, new VoContentWithBody(), content);
            contentWithBody.setContentBodies(getContentBody(id));
            return contentWithBody;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoContentWithBody update(final VoContentWithBody vo) throws Exception {
        final ContentDTO contentDTO = dtoContentService.getById(vo.getContentId());
        final long categoryId = contentDTO != null && contentDTO.getParentId() == vo.getParentId() ? vo.getContentId() : vo.getParentId();
        if (contentDTO != null && federationFacade.isManageable(categoryId, ContentDTO.class)) {
            ContentDTO persistent = voAssemblySupport.assembleDto(ContentDTO.class, VoContent.class, contentDTO, vo);
            ensureValidNullValues(persistent);
            dtoContentService.update(persistent);
            updateContent(vo.getContentBodies());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getById(vo.getContentId());
    }

    /** {@inheritDoc} */
    @Override
    public VoContentWithBody create(final VoContent voContent) throws Exception {
        final ContentDTO contentDTO = dtoContentService.getNew();
        if (voContent != null && federationFacade.isManageable(voContent.getParentId(), CategoryDTO.class)){
            ContentDTO persistent = voAssemblySupport.assembleDto(ContentDTO.class, VoContent.class, contentDTO, voContent);
            ensureValidNullValues(persistent);
            persistent = dtoContentService.create(persistent);
            return getById(persistent.getId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private void ensureValidNullValues(final ContentDTO persistent) {
        if (StringUtils.isBlank(persistent.getUri())) {
            persistent.setUri(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final long id) throws Exception {
        if (federationFacade.isManageable(id, CategoryDTO.class)) {
            dtoContentService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueContent> getContentAttributes(final long contentId) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(contentId, true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueContent> update(final List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception {

        final long contentId = voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, true);

        return getContentAttributes(contentId);
    }

    private Comparator<VoContentBody> SORT_BY_LANG = (o1, o2) -> o1.getLang().compareTo(o2.getLang());

    @Override
    public List<VoContentBody> getContentBody(final long contentId) throws Exception {
        if (federationFacade.isManageable(contentId, CategoryDTO.class)) {

            final List<AttrValueDTO> attrs = (List) dtoContentService.getEntityContentAttributes(contentId);
            final List<VoContentBody> bodies = new ArrayList<>();
            for (final AttrValueDTO attr : attrs) {
                final String lang = attr.getAttributeDTO().getCode().substring(this.skipContentAttributesInView.length());
                final VoContentBody body = new VoContentBody();
                body.setContentId(contentId);
                body.setLang(lang);
                body.setText(ensureDynamicContentIsVisible(attr.getVal()));
                bodies.add(body);
            }
            bodies.sort(SORT_BY_LANG);
            return bodies;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Override
    public List<VoContentBody> updateContent(final List<VoContentBody> vo) throws Exception {

        long contentId = 0;
        if (vo != null) {
            for (VoContentBody body : vo) {
                if (contentId == 0L) {
                    contentId = body.getContentId();
                    if (!federationFacade.isManageable(contentId, ContentDTO.class)) {
                        throw new AccessDeniedException("Access is denied");
                    }
                }
                if (contentId != body.getContentId()) {
                    throw new AccessDeniedException("Access is denied");
                }
            }

            for (VoContentBody body : vo) {

                final AttributeDTO attr = dtoAttributeService.getNew();
                attr.setCode(this.skipContentAttributesInView + body.getLang());
                final AttrValueDTO attrVal = dtoContentService.getNewAttribute(contentId);
                attrVal.setAttributeDTO(attr);
                attrVal.setVal(ensureDynamicContentIsValid(body.getText()));

                dtoContentService.updateEntityAttributeValue(attrVal);

            }
        }

        return getContentBody(contentId);

    }

    String ensureDynamicContentIsVisible(final String content) {

        return VoContentServiceUtils.ensureDynamicContentIsVisible(content);

    }

    String ensureDynamicContentIsValid(final String edited) {

        return VoContentServiceUtils.ensureDynamicContentIsValid(edited);

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
