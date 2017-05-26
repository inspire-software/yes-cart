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

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.AttrValueSystemDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAttrValueSystem;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoSystemService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoSystemPreferencesService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final VoAttributesCRUDTemplate<VoAttrValueSystem, AttrValueSystemDTO> voAttributesCRUDTemplate;

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

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueSystem, AttrValueSystemDTO>(
                        VoAttrValueSystem.class,
                        AttrValueSystemDTO.class,
                        Constants.SYSTEM_IMAGE_REPOSITORY_URL_PATTERN,
                        Constants.SYSTEM_FILE_REPOSITORY_URL_PATTERN,
                        Constants.SYSTEM_SYSFILE_REPOSITORY_URL_PATTERN,
                        this.dtoSystemService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
        {
            @Override
            protected boolean skipAttributesInView(final String code) {
                return skipAttributesInView.contains(code);
            }

            @Override
            protected long determineObjectId(final VoAttrValueSystem vo) {
                return vo.getSystemId();
            }

            @Override
            protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId) throws Exception {
                boolean accessible = federationFacade.isCurrentUserSystemAdmin();
                if (!accessible) {
                    return new Pair<>(false, null);
                }
                return new Pair<>(true, SYSTEM_CODE);
            }
        };
    }



    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueSystem> getSystemPreferences() throws Exception {

        return this.voAttributesCRUDTemplate.verifyAccessAndGetAttributes(100L);

    }


    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueSystem> update(final List<MutablePair<VoAttrValueSystem, Boolean>> vo) throws Exception {

        this.voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo);

        return getSystemPreferences();

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
