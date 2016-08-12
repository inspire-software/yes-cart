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

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.AttrValueSystemDTO;
import org.yes.cart.domain.dto.impl.AttrValueSystemDTOImpl;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueSystem;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoSystemService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoSystemPreferencesService;
import org.yes.cart.util.ShopCodeContext;

import java.util.*;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 17:37
 */
public class VoSystemPreferencesServiceImpl implements VoSystemPreferencesService {

    private final String SYSTEM_CODE = "YC";

    private final DtoSystemService dtoSystemService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private Set<String> skipAttributesInView = Collections.emptySet();


    public VoSystemPreferencesServiceImpl(final DtoSystemService dtoSystemService,
                                          final DtoAttributeService dtoAttributeService,
                                          final FederationFacade federationFacade,
                                          final VoAssemblySupport voAssemblySupport,
                                          final VoIOSupport voIOSupport) {
        this.dtoSystemService = dtoSystemService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;
    }



    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueSystem> getSystemPreferences() throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {

            final List<AttrValueSystemDTO> attributes = (List) dtoSystemService.getEntityAttributes(100L);

            final List<VoAttrValueSystem> all = voAssemblySupport.assembleVos(VoAttrValueSystem.class, AttrValueSystemDTO.class, attributes);
            // Filter out special attributes that are managed by specialised editors
            final Iterator<VoAttrValueSystem> allIt = all.iterator();
            while (allIt.hasNext()) {
                final VoAttrValueSystem next = allIt.next();
                if (skipAttributesInView.contains(next.getAttribute().getCode())) {
                    allIt.remove();
                } else if (next.getAttrvalueId() > 0L && "Image".equals(next.getAttribute().getEtypeName())) {
                    if (StringUtils.isNotBlank(next.getVal())) {
                        next.setValBase64Data(
                                voIOSupport.getImageAsBase64(next.getVal(), SYSTEM_CODE, Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN)
                        );
                        // TODO: SEO data for image
                    }
                }
            }
            return all;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueSystem> update(final List<MutablePair<VoAttrValueSystem, Boolean>> vo) throws Exception {

        if (!federationFacade.isCurrentUserSystemAdmin()) {
            throw new AccessDeniedException("Access is denied");
        }

        final VoAssemblySupport.VoAssembler<VoAttrValueSystem, AttrValueSystemDTO> asm =
                voAssemblySupport.with(VoAttrValueSystem.class, AttrValueSystemDTO.class);

        Map<Long, AttrValueSystemDTO> existing = mapAvById((List) dtoSystemService.getEntityAttributes(100L));
        for (final MutablePair<VoAttrValueSystem, Boolean> item : vo) {

            if (skipAttributesInView.contains(item.getFirst().getAttribute().getCode())) {
                ShopCodeContext.getLog(this).warn("Shop attribute {} value cannot be updated using general AV update ... skipped", item.getFirst().getAttribute().getCode());
                continue;
            }

            if (Boolean.valueOf(item.getSecond())) {
                // delete mode
                dtoSystemService.deleteAttributeValue(item.getFirst().getAttrvalueId());
            } else if (item.getFirst().getAttrvalueId() > 0L) {
                // update mode
                final AttrValueSystemDTO dto = existing.get(item.getFirst().getAttrvalueId());
                if (dto != null) {
                    if ("Image".equals(dto.getAttributeDTO().getEtypeName())) {
                        final String existingImage = voIOSupport.
                                getImageAsBase64(dto.getVal(), SYSTEM_CODE, Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN);
                        if (existingImage == null || !existingImage.equals(item.getFirst().getValBase64Data())) {
                            String formattedFilename = item.getFirst().getVal();
                            formattedFilename = voIOSupport.
                                    addImageToRepository(
                                            formattedFilename,
                                            SYSTEM_CODE,
                                            dto.getAttributeDTO().getCode(),
                                            item.getFirst().getValBase64Data(),
                                            Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN
                                    );
                            item.getFirst().setVal(formattedFilename);
                            // TODO Image SEO
                        }
                    }
                    asm.assembleDto(dto, item.getFirst());
                    dtoSystemService.updateEntityAttributeValue(dto);
                } else {
                    ShopCodeContext.getLog(this).warn("Update skipped for inexistent ID {}", item.getFirst().getAttrvalueId());
                }
            } else {
                // insert mode
                final AttrValueSystemDTO dto = new AttrValueSystemDTOImpl();
                dto.setSystemId(100L);
                dto.setAttributeDTO(dtoAttributeService.getById(item.getFirst().getAttribute().getAttributeId()));
                if ("Image".equals(dto.getAttributeDTO().getEtypeName())) {
                    String formattedFilename = item.getFirst().getVal();
                    formattedFilename = voIOSupport.
                            addImageToRepository(
                                    formattedFilename,
                                    SYSTEM_CODE,
                                    dto.getAttributeDTO().getCode(),
                                    item.getFirst().getValBase64Data(),
                                    Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN
                            );
                    item.getFirst().setVal(formattedFilename);
                    // TODO Image SEO
                }
                asm.assembleDto(dto, item.getFirst());
                dtoSystemService.createEntityAttributeValue(dto);
            }

        }

        return getSystemPreferences();
    }

    private Map<Long, AttrValueSystemDTO> mapAvById(final List<AttrValueSystemDTO> entityAttributes) {
        Map<Long, AttrValueSystemDTO> map = new HashMap<Long, AttrValueSystemDTO>();
        for (final AttrValueSystemDTO dto : entityAttributes) {
            map.put(dto.getAttrvalueId(), dto);
        }
        return map;
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
