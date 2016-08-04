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

import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.vo.*;
import org.yes.cart.domain.vo.converter.Converters;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoShopUrlService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.misc.CurrencyService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.service.vo.VoShopService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

    private final DtoShopService dtoShopService;
    private final DtoShopUrlService dtoShopUrlService;

    private final FederationFacade federationFacade;

    private final LanguageService languageService;
    private final CurrencyService currencyService;

    /**
     * Construct service.
     *
     * @param languageService languages
     * @param currencyService currencies
     * @param dtoShopUrlService underlying service to work with shop urls.
     * @param dtoShopService    underlying service to use.
     * @param federationFacade  access.
     */
    public VoShopServiceImpl(final LanguageService languageService,
                             final CurrencyService currencyService,
                             final DtoShopUrlService dtoShopUrlService,
                             final DtoShopService dtoShopService,
                             final FederationFacade federationFacade) {
        this.currencyService = currencyService;
        this.dtoShopUrlService = dtoShopUrlService;
        this.dtoShopService = dtoShopService;
        this.federationFacade = federationFacade;
        this.languageService = languageService;
    }

    /**
     * {@inheritDoc}
     */
    public List<VoShop> getAll() throws Exception {
        final List<ShopDTO> all = dtoShopService.getAll();
        federationFacade.applyFederationFilter(all, ShopDTO.class);
        final List<VoShop> rez = new ArrayList<>(all.size());
        DTOAssembler.newAssembler(VoShop.class, ShopDTO.class).assembleDtos(rez, all, null, null);
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public VoShop getById(long id) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(id);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            final VoShop voShop = new VoShop();
            DTOAssembler.newAssembler(VoShop.class, ShopDTO.class).assembleDto(voShop, shopDTO, null, null);
            return voShop;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShop update(VoShop vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            DTOAssembler.newAssembler(VoShop.class, ShopDTO.class).assembleEntity(vo, shopDTO, null, null);
            dtoShopService.update(shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getById(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    public VoShop create(VoShop vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            ShopDTO shopDTO = dtoShopService.getNew();
            DTOAssembler.newAssembler(VoShop.class, ShopDTO.class).assembleEntity(vo, shopDTO, null, null);
            shopDTO = dtoShopService.create(shopDTO);
            return getById(shopDTO.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoShopService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocale getShopLocale(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        final VoShopLocale voShopLocale = new VoShopLocale();
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            DTOAssembler.newAssembler(VoShopLocale.class, ShopDTO.class).assembleDto(voShopLocale, shopDTO, Converters.BASIC, null);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return voShopLocale;
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocale update(final VoShopLocale vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            DTOAssembler.newAssembler(VoShopLocale.class, ShopDTO.class).assembleEntity(vo, shopDTO, Converters.BASIC, null);
            dtoShopService.update(shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getShopLocale(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    public VoShopUrl getShopUrls(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            final List<ShopUrlDTO> shopUrlDTO = dtoShopUrlService.getAllByShopId(shopId);
            final List<VoShopUrlDetail> voShopUrlDetails = new ArrayList<>(shopUrlDTO.size());
            final VoShopUrl voShopUrl = new VoShopUrl();
            DTOAssembler.newAssembler(VoShopUrlDetail.class, ShopUrlDTO.class).assembleDtos(voShopUrlDetails, shopUrlDTO, null, null);
            voShopUrl.setUrls(voShopUrlDetails);
            voShopUrl.setShopId(shopId);
            return voShopUrl;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopUrl update(VoShopUrl vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            final List<ShopUrlDTO> originalShopUrlDTOs = dtoShopUrlService.getAllByShopId(vo.getShopId());
            for (VoShopUrlDetail urlDetail : vo.getUrls()) {
                ShopUrlDTO shopUrlDTO = new ShopUrlDTOImpl();
                DTOAssembler.newAssembler(VoShopUrlDetail.class, ShopUrlDTO.class).assembleEntity(urlDetail, shopUrlDTO, null, null);
                shopUrlDTO.setShopId(vo.getShopId());
                if (urlDetail.getUrlId() == 0) {  //new one insert
                    dtoShopUrlService.create(shopUrlDTO);
                } else { //update
                    dtoShopUrlService.update(shopUrlDTO);
                    removeById(originalShopUrlDTOs, shopUrlDTO.getStoreUrlId());
                }
            }
            for (ShopUrlDTO dto : originalShopUrlDTOs) {
                dtoShopUrlService.remove(dto.getId());
            }
            return getShopUrls(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopSupportedCurrencies getShopCurrencies(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            VoShopSupportedCurrencies ssc = new VoShopSupportedCurrencies();
            ssc.setShopId(shopId);
            ssc.setAll(getAvailableCurrencies());
            String curr = dtoShopService.getSupportedCurrencies(shopId);
            ssc.setSupported(
                    curr == null ? Collections.<String>emptyList() : Arrays.asList(curr.split(","))
            );
            return ssc;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopSupportedCurrencies update(VoShopSupportedCurrencies vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.updateSupportedCurrencies(
                    vo.getShopId(),
                    StringUtils.join(vo.getSupported().toArray(), ",")
            );
            return getShopCurrencies(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLanguages getShopLanguages(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            final VoShopLanguages voShopLanguages = new VoShopLanguages();
            String lng = dtoShopService.getSupportedLanguages(shopId);
            voShopLanguages.setSupported(lng == null ? Collections.<String>emptyList() : Arrays.asList(lng.split(",")));
            voShopLanguages.setAll(VoUtils.adaptMapToPairs(languageService.getLanguageName()));
            voShopLanguages.setShopId(shopId);
            return voShopLanguages;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLanguages update(VoShopLanguages vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.updateSupportedLanguages(vo.getShopId(),
                    StringUtils.join(vo.getSupported().toArray(), ","));
            return getShopLanguages(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShop updateDisabledFlag(final long shopId, final boolean disabled) throws Exception {
        final ShopDTO dto = dtoShopService.updateDisabledFlag(shopId, disabled);
        if (dto != null) {
            return getById(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private void removeById(List<ShopUrlDTO> originalShopUrlDTOs, long storeUrlId) {
        for (ShopUrlDTO dto : originalShopUrlDTOs) {
            if (dto.getStoreUrlId() == storeUrlId) {
                originalShopUrlDTOs.remove(dto);
                break;
            }
        }
    }

    private List<String> getAvailableCurrencies() {

        return currencyService.getSupportedCurrencies();
    }


}
