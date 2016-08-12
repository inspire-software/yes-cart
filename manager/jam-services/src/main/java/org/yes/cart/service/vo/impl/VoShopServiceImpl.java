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
import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.impl.AttrValueShopDTOImpl;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoShopUrlService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.misc.CurrencyService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoShopService;
import org.yes.cart.util.ShopCodeContext;

import java.util.*;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

    private final DtoShopService dtoShopService;
    private final DtoShopUrlService dtoShopUrlService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private final LanguageService languageService;
    private final CurrencyService currencyService;
    private final CountryService countryService;

    private Set<String> skipAttributesInView = Collections.emptySet();

    /**
     * Construct service.
     * @param languageService languages
     * @param currencyService currencies
     * @param countryService  locations
     * @param dtoShopUrlService underlying service to work with shop urls.
     * @param dtoShopService    underlying service to use.
     * @param dtoAttributeService attribute service
     * @param federationFacade  access.
     * @param voAssemblySupport vo support
     * @param voIOSupport vo support
     */
    public VoShopServiceImpl(final LanguageService languageService,
                             final CurrencyService currencyService,
                             final CountryService countryService,
                             final DtoShopUrlService dtoShopUrlService,
                             final DtoShopService dtoShopService,
                             final DtoAttributeService dtoAttributeService,
                             final FederationFacade federationFacade,
                             final VoAssemblySupport voAssemblySupport,
                             final VoIOSupport voIOSupport) {
        this.currencyService = currencyService;
        this.countryService = countryService;
        this.dtoShopUrlService = dtoShopUrlService;
        this.dtoShopService = dtoShopService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.languageService = languageService;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;
    }

    /**
     * {@inheritDoc}
     */
    public List<VoShop> getAll() throws Exception {
        final List<ShopDTO> all = dtoShopService.getAll();
        federationFacade.applyFederationFilter(all, ShopDTO.class);
        return voAssemblySupport.assembleVos(VoShop.class, ShopDTO.class, all);
    }

    /**
     * {@inheritDoc}
     */
    public VoShop getById(long id) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(id);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            return voAssemblySupport.assembleVo(VoShop.class, ShopDTO.class, new VoShop(), shopDTO);
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
            dtoShopService.update(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShop.class, shopDTO, vo)
            );
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
            shopDTO = dtoShopService.create(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShop.class, shopDTO, vo)
            );
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
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            return voAssemblySupport.assembleVo(VoShopLocale.class, ShopDTO.class, new VoShopLocale(), shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocale update(final VoShopLocale vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.update(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShopLocale.class, shopDTO, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getShopLocale(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    public VoShopUrl getShopUrls(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final List<ShopUrlDTO> shopUrlDTO = dtoShopUrlService.getAllByShopId(shopId);
            final VoShopUrl voShopUrl = new VoShopUrl();
            voShopUrl.setUrls(voAssemblySupport.assembleVos(VoShopUrlDetail.class, ShopUrlDTO.class, shopUrlDTO));
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
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            final List<ShopUrlDTO> originalShopUrlDTOs = dtoShopUrlService.getAllByShopId(vo.getShopId());
            final Set<Long> updated = new HashSet<>();
            for (VoShopUrlDetail urlDetail : vo.getUrls()) {
                ShopUrlDTO shopUrlDTO =
                        voAssemblySupport.assembleDto(ShopUrlDTO.class, VoShopUrlDetail.class, new ShopUrlDTOImpl(), urlDetail);
                shopUrlDTO.setShopId(vo.getShopId());
                if (urlDetail.getUrlId() == 0) {  //new one insert
                    dtoShopUrlService.create(shopUrlDTO);
                } else { //update
                    dtoShopUrlService.update(shopUrlDTO);
                    updated.add(shopUrlDTO.getStoreUrlId());
                }
            }
            for (ShopUrlDTO dto : originalShopUrlDTOs) {
                if (!updated.contains(dto.getId())) {
                    dtoShopUrlService.remove(dto.getId());
                }
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
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            VoShopSupportedCurrencies ssc = new VoShopSupportedCurrencies();
            ssc.setShopId(shopId);
            ssc.setAll(currencyService.getSupportedCurrencies());
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
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
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
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
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
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
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
    public VoShopLocations getShopLocations(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            final VoShopLocations shopLocations = new VoShopLocations();

            String billing = dtoShopService.getSupportedBillingCountries(shopId);
            String shipping = dtoShopService.getSupportedShippingCountries(shopId);
            shopLocations.setSupportedBilling(billing == null ? Collections.<String>emptyList() : Arrays.asList(billing.split(",")));
            shopLocations.setSupportedShipping(shipping == null ? Collections.<String>emptyList() : Arrays.asList(shipping.split(",")));

            final List<Country> countries = countryService.findAll();
            final List<MutablePair<String, String>> all = new ArrayList<>();
            for (final Country country : countries) {
                all.add(MutablePair.of(
                        country.getCountryCode(),
                        country.getName() + (StringUtils.isNotBlank(country.getDisplayName()) ? " (" + country.getDisplayName() + ")" : "")));
            }

            shopLocations.setAll(all);
            shopLocations.setShopId(shopId);
            return shopLocations;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoShopLocations update(VoShopLocations vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            dtoShopService.updateSupportedBillingCountries(vo.getShopId(),
                    StringUtils.join(vo.getSupportedBilling().toArray(), ","));
            dtoShopService.updateSupportedShippingCountries(vo.getShopId(),
                    StringUtils.join(vo.getSupportedShipping().toArray(), ","));
            return getShopLocations(vo.getShopId());
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



    /**
     * {@inheritDoc}
     */
    public List<VoAttrValueShop> getShopAttributes(final long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {

            final ShopDTO shop = dtoShopService.getById(shopId);
            final List<AttrValueShopDTO> attributes = (List) dtoShopService.getEntityAttributes(shopId);

            final List<VoAttrValueShop> all = voAssemblySupport.assembleVos(VoAttrValueShop.class, AttrValueShopDTO.class, attributes);
            // Filter out special attributes that are managed by specialised editors
            final Iterator<VoAttrValueShop> allIt = all.iterator();
            while (allIt.hasNext()) {
                final VoAttrValueShop next = allIt.next();
                if (skipAttributesInView.contains(next.getAttribute().getCode())) {
                    allIt.remove();
                } else if (next.getAttrvalueId() > 0L && "Image".equals(next.getAttribute().getEtypeName())) {
                    if (StringUtils.isNotBlank(next.getVal())) {
                        next.setValBase64Data(
                                voIOSupport.getImageAsBase64(next.getVal(), shop.getCode(), Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN)
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
    public List<VoAttrValueShop> update(final List<MutablePair<VoAttrValueShop, Boolean>> vo) throws Exception {

        long shopId = 0L;
        ShopDTO shop = null;
        final VoAssemblySupport.VoAssembler<VoAttrValueShop, AttrValueShopDTO> asm =
                voAssemblySupport.with(VoAttrValueShop.class, AttrValueShopDTO.class);

        Map<Long, AttrValueShopDTO> existing = Collections.emptyMap();
        for (final MutablePair<VoAttrValueShop, Boolean> item : vo) {
            if (shopId == 0L) {
                shopId = item.getFirst().getShopId();
                shop = dtoShopService.getById(shopId);
                if (!federationFacade.isShopAccessibleByCurrentManager(shopId)) {
                    throw new AccessDeniedException("Access is denied");
                }
                existing = mapAvById((List) dtoShopService.getEntityAttributes(shopId));
            } else if (shopId != item.getFirst().getShopId()) {
                throw new AccessDeniedException("Access is denied");
            }

            if (skipAttributesInView.contains(item.getFirst().getAttribute().getCode())) {
                ShopCodeContext.getLog(this).warn("Shop attribute {} value cannot be updated using general AV update ... skipped", item.getFirst().getAttribute().getCode());
                continue;
            }

            if (Boolean.valueOf(item.getSecond())) {
                // delete mode
                dtoShopService.deleteAttributeValue(item.getFirst().getAttrvalueId());
            } else if (item.getFirst().getAttrvalueId() > 0L) {
                // update mode
                final AttrValueShopDTO dto = existing.get(item.getFirst().getAttrvalueId());
                if (dto != null) {
                    if ("Image".equals(dto.getAttributeDTO().getEtypeName())) {
                        final String existingImage = voIOSupport.
                                getImageAsBase64(dto.getVal(), shop.getCode(), Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN);
                        if (existingImage == null || !existingImage.equals(item.getFirst().getValBase64Data())) {
                            String formattedFilename = item.getFirst().getVal();
                            formattedFilename = voIOSupport.
                                    addImageToRepository(
                                            formattedFilename,
                                            shop.getCode(),
                                            dto.getAttributeDTO().getCode(),
                                            item.getFirst().getValBase64Data(),
                                            Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN
                                    );
                            item.getFirst().setVal(formattedFilename);
                            // TODO Image SEO
                        }
                    }
                    asm.assembleDto(dto, item.getFirst());
                    dtoShopService.updateEntityAttributeValue(dto);
                } else {
                    ShopCodeContext.getLog(this).warn("Update skipped for inexistent ID {}", item.getFirst().getAttrvalueId());
                }
            } else {
                // insert mode
                final AttrValueShopDTO dto = new AttrValueShopDTOImpl();
                dto.setShopId(shopId);
                dto.setAttributeDTO(dtoAttributeService.getById(item.getFirst().getAttribute().getAttributeId()));
                if ("Image".equals(dto.getAttributeDTO().getEtypeName())) {
                    String formattedFilename = item.getFirst().getVal();
                    formattedFilename = voIOSupport.
                            addImageToRepository(
                                    formattedFilename,
                                    shop.getCode(),
                                    dto.getAttributeDTO().getCode(),
                                    item.getFirst().getValBase64Data(),
                                    Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN
                            );
                    item.getFirst().setVal(formattedFilename);
                    // TODO Image SEO
                }
                asm.assembleDto(dto, item.getFirst());
                dtoShopService.createEntityAttributeValue(dto);
            }

        }

        return getShopAttributes(shopId);
    }

    private Map<Long, AttrValueShopDTO> mapAvById(final List<AttrValueShopDTO> entityAttributes) {
        Map<Long, AttrValueShopDTO> map = new HashMap<Long, AttrValueShopDTO>();
        for (final AttrValueShopDTO dto : entityAttributes) {
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
