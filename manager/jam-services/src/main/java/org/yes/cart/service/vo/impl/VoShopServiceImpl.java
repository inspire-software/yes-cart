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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.ShopAliasDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.ShopUrlDTO;
import org.yes.cart.domain.dto.impl.ShopAliasDTOImpl;
import org.yes.cart.domain.dto.impl.ShopUrlDTOImpl;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoShopAliasService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.DtoShopUrlService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.misc.CurrencyService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoShopService;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by iazarnyi on 1/19/16.
 */
public class VoShopServiceImpl implements VoShopService {

    private static final Logger LOG = LoggerFactory.getLogger(VoShopServiceImpl.class);

    private final DtoShopService dtoShopService;
    private final DtoShopUrlService dtoShopUrlService;
    private final DtoShopAliasService dtoShopAliasService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private final LanguageService languageService;
    private final CurrencyService currencyService;
    private final CountryService countryService;
    private final SystemService systemService;
    private final ThemeService themeService;

    private final VoAttributesCRUDTemplate<VoAttrValueShop, AttrValueShopDTO> voAttributesCRUDTemplate;

    private Set<String> skipAttributesInView = Collections.emptySet();

    /**
     * Construct service.
     * @param languageService languages
     * @param currencyService currencies
     * @param countryService  locations
     * @param dtoShopUrlService underlying service to work with shop urls.
     * @param dtoShopAliasService underlying service to work with shop aliases.
     * @param dtoShopService    underlying service to use.
     * @param dtoAttributeService attribute service
     * @param federationFacade  access.
     * @param voAssemblySupport vo support
     * @param voIOSupport vo support
     * @param systemService system service
     * @param themeService theme service
     */
    public VoShopServiceImpl(final LanguageService languageService,
                             final CurrencyService currencyService,
                             final CountryService countryService,
                             final DtoShopUrlService dtoShopUrlService,
                             final DtoShopAliasService dtoShopAliasService,
                             final DtoShopService dtoShopService,
                             final DtoAttributeService dtoAttributeService,
                             final FederationFacade federationFacade,
                             final VoAssemblySupport voAssemblySupport,
                             final VoIOSupport voIOSupport,
                             final SystemService systemService,
                             final ThemeService themeService) {
        this.currencyService = currencyService;
        this.countryService = countryService;
        this.dtoShopUrlService = dtoShopUrlService;
        this.dtoShopAliasService = dtoShopAliasService;
        this.dtoShopService = dtoShopService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.languageService = languageService;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;
        this.systemService = systemService;
        this.themeService = themeService;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueShop, AttrValueShopDTO>(
                        VoAttrValueShop.class,
                        AttrValueShopDTO.class,
                        this.dtoShopService,
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
            protected long determineObjectId(final VoAttrValueShop vo) {
                return vo.getShopId();
            }

            @Override
            protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {
                boolean accessible = federationFacade.isShopAccessibleByCurrentManager(objectId);
                if (!accessible) {
                    return new Pair<>(false, null);
                }
                final ShopDTO shop = dtoShopService.getById(objectId);
                return new Pair<>(true, shop.getCode());
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoShop> getAll() throws Exception {
        final List<ShopDTO> all = dtoShopService.getAll();
        federationFacade.applyFederationFilter(all, ShopDTO.class);
        return voAssemblySupport.assembleVos(VoShop.class, ShopDTO.class, all);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoShop> getAllSubs(final long masterId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(masterId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {

            final List<ShopDTO> subs = dtoShopService.getAllSubs(masterId);
            return voAssemblySupport.assembleVos(VoShop.class, ShopDTO.class, subs);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
    public VoShop create(VoShop vo) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            ShopDTO shopDTO = dtoShopService.getNew();
            shopDTO = dtoShopService.create(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShop.class, shopDTO, vo)
            );

            createShopDefaults(shopDTO);

            return getById(shopDTO.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoShop createSub(final VoSubShop vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo != null ? vo.getMasterId() : 0L);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            throw new RuntimeException("This feature is only available on YCE");
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    protected void createShopDefaults(final ShopDTO shopDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        // Setup basic properties
        final List<AttrValueShopDTO> avs = (List) dtoShopService.getEntityAttributes(shopDTO.getShopId());
        final Map<String, AttrValueShopDTO> avsMap = new HashMap<>();
        for (final AttrValueShopDTO av : avs) {
            avsMap.put(av.getAttributeDTO().getCode(), av);
        }

        final AttrValueShopDTO supportedTypes = avsMap.get(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES);
        if (supportedTypes != null) {
            supportedTypes.setVal(AttributeNamesKeys.Cart.CUSTOMER_TYPE_REGULAR);
            dtoShopService.createEntityAttributeValue(supportedTypes);
        }

        final AttrValueShopDTO searchIncludeCats = avsMap.get(AttributeNamesKeys.Shop.SHOP_INCLUDE_SUBCATEGORIES_IN_SEARCH);
        if (searchIncludeCats != null) {
            searchIncludeCats.setVal(Boolean.TRUE.toString());
            dtoShopService.createEntityAttributeValue(searchIncludeCats);
        }

        final AttrValueShopDTO passwReset = avsMap.get(AttributeNamesKeys.Shop.SHOP_CUSTOMER_PASSWORD_RESET_CC);
        if (passwReset != null) {
            passwReset.setVal(UUID.randomUUID().toString());
            dtoShopService.createEntityAttributeValue(passwReset);
        }

        final AttrValueShopDTO langs = avsMap.get(AttributeNamesKeys.Shop.SUPPORTED_LANGUAGES);
        if (langs != null) {
            langs.setVal(org.apache.commons.lang.StringUtils.join(languageService.getSupportedLanguages(), ','));
            dtoShopService.createEntityAttributeValue(langs);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void fillShopSummaryDetails(final VoShopSummary summary, final long shopId, final String lang) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {

            final long currentShopId = shopDTO.getShopId();
            final String currentShopCode = shopDTO.getCode();
            final long configShopId = shopDTO.getMasterId() != null ? shopDTO.getMasterId() : shopDTO.getShopId();
            final String configShopCode = shopDTO.getMasterId() != null ? shopDTO.getMasterCode() : shopDTO.getCode();

            final Map<String, VoAttrValueShop> attrsMap = getStringVoAttrValueShopMap(configShopId, configShopCode);
            final Map<String, VoAttrValueShop> attrsMapSub = currentShopId == configShopId ? attrsMap : getStringVoAttrValueShopMap(currentShopId, currentShopCode);

            addMainInfo(summary, configShopId, shopDTO);

            addShopLocales(summary, configShopId, lang, attrsMap);

            addShopCurrencies(summary, configShopId);

            addShopLocations(summary, configShopId, lang);

            addShopUrls(summary, configShopId);

            addShopAliasess(summary, currentShopId);

            addSearchConfig(summary, lang, attrsMap);

            addCheckoutConfig(summary, lang, attrsMap);

            addCustomerConfig(summary, lang, attrsMap, attrsMapSub);

            addEmailTemplatesBasicSettings(summary, lang, attrsMap);

            addSettingsConfig(summary, lang, attrsMap);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    protected void addMainInfo(final VoShopSummary summary, final long shopId, final ShopDTO shopDTO) {
        summary.setShopId(shopDTO.getShopId());
        summary.setDisabled(shopDTO.isDisabled());
        summary.setCode(shopDTO.getCode());
        summary.setMasterId(shopDTO.getMasterId());
        summary.setMasterCode(shopDTO.getMasterCode());
        summary.setName(shopDTO.getName());
        summary.setThemeChain(StringUtils.join(themeService.getThemeChainByShopId(shopId, null), " > "));

        final StringBuilder sysFileRoot = new StringBuilder(this.systemService.getSystemFileRepositoryDirectory());
        sysFileRoot.append("shop");
        if (shopDTO.getCode() != null) {
            sysFileRoot.append(File.separator);
            sysFileRoot.append(Character.toUpperCase(shopDTO.getCode().charAt(0)));
            sysFileRoot.append(File.separator);
            sysFileRoot.append(shopDTO.getCode());
        }
        summary.setSysFileRoot(sysFileRoot.toString());
    }

    protected void addSettingsConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setSfPageTraceEnabled(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SF_PAGE_TRACE, lang, false));
    }

    protected void addCustomerConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> masterAttrsMap, final Map<String, VoAttrValueShop> subAttrsMap) {

        summary.setAdminEmail(getShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL, lang, "-"));
        final MutablePair<String, String> subAdminEmail = getShopAttributeConfig(
                subAttrsMap, AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL, lang, "-");
        if (!summary.getAdminEmail().getSecond().equals(subAdminEmail.getSecond())) {
            summary.getAdminEmail().setSecond(subAdminEmail.getSecond() + " (" + summary.getAdminEmail().getSecond() + ")");
        }

        // B2B address book
        summary.setB2bAddressbookActive(getBooleanShopAttributeConfig(
                subAttrsMap, AttributeNamesKeys.Shop.SHOP_B2B_ADDRESSBOOK, lang, false));
        // B2B sub shop specific
        summary.setB2bStrictPriceActive(getBooleanShopAttributeConfig(
                subAttrsMap, AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PRICE, lang, false));
        summary.setB2bStrictPriceRulesActive(getBooleanShopAttributeConfig(
                subAttrsMap, AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PRICE_RULES, lang, false));
        summary.setB2bStrictPromotionsActive(getBooleanShopAttributeConfig(
                subAttrsMap, AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PROMOTIONS, lang, false));

        summary.setB2bProfileActive(getBooleanShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.SHOP_B2B, lang, false));
        summary.setCookiePolicy(getBooleanShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.SHOP_COOKIE_POLICY_ENABLE, lang, false));
        summary.setAnonymousBrowsing(getBooleanShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_LOGIN, lang, true));
        summary.setManagerLogin(getBooleanShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_LOGIN_MANAGER, lang, false));

        final MutablePair<String, String> sessionExpiry = getShopAttributeConfig(
                masterAttrsMap, AttributeNamesKeys.Shop.CART_SESSION_EXPIRY_SECONDS, lang, "21600");
        int sessionExpirySeconds = NumberUtils.toInt(sessionExpiry.getFirst());
        String time = "6h";
        if (sessionExpirySeconds > 3600) { // more than hour
            time = new BigDecimal(sessionExpirySeconds).divide(new BigDecimal(60 * 60), 1, BigDecimal.ROUND_HALF_EVEN).toPlainString() + "h";
        } else if (sessionExpirySeconds > 60) {  // more than minute
            time = new BigDecimal(sessionExpirySeconds).divide(new BigDecimal(60), 1, BigDecimal.ROUND_HALF_EVEN).toPlainString() + "m";
        }
        summary.setCustomerSession(MutablePair.of(sessionExpiry.getFirst(), time));

        final Set<String> knownCustomerTypes = new HashSet<>();
        final VoAttrValueShop registrationTypesCsv = masterAttrsMap.get(AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES);
        if (registrationTypesCsv != null && StringUtils.isNotBlank(registrationTypesCsv.getVal())) {

            final String[] registrationTypes = StringUtils.split(registrationTypesCsv.getVal(), ',');
            final String[] registrationTypesNames = StringUtils.split(
                    getDisplayName(registrationTypesCsv.getDisplayVals(), registrationTypesCsv.getVal(), lang), ',');

            for (int i = 0; i < registrationTypes.length; i++) {
                final MutablePair<String, String> typeAndName = MutablePair.of(
                        registrationTypes[i].trim(),
                        registrationTypesNames.length > i ? registrationTypesNames[i].trim() : registrationTypes[i].trim()
                );
                knownCustomerTypes.add(typeAndName.getFirst());
                summary.getCustomerTypes().add(typeAndName);
            }
            if (!knownCustomerTypes.contains(AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST)) {
                knownCustomerTypes.add(AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST);
                summary.getCustomerTypes().add(MutablePair.of(AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST, "-"));
            }

        }

        // Registrations are at master level since we do not know the SUB yet
        final MutablePair<String, List<String>> ableToRegister =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_CUSTOMER_TYPES, lang);

        // Disable account deletion
        final MutablePair<String, List<String>> disableAccountDelete =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_DELETE_ACCOUNT_DISABLE, lang);

        // Email related config is at master level
        final MutablePair<String, List<String>> approveRegister =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_APPROVE, lang);
        final MutablePair<String, List<String>> notifyRegister =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_REG_NOTIFICATION, lang);

        // Tax information is at Master level
        final MutablePair<String, List<String>> seeTax =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO, lang);
        final MutablePair<String, List<String>> seeNetPrice =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET, lang);
        final MutablePair<String, List<String>> seeTaxAmount =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT, lang);
        final MutablePair<String, List<String>> changeTax =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES, lang);

        // Prices
        final MutablePair<String, List<String>> hidePrices =
                getCsvShopAttributeConfig(masterAttrsMap, AttributeNamesKeys.Shop.SHOP_PRODUCT_HIDE_PRICES, lang);

        // Allow selection of same address
        final MutablePair<String, List<String>> disableOneAddress =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_DELIVERY_ONE_ADDRESS_DISABLE, lang);

        // RFQ, approvals, checkout block, repeat order, shopping list and address book disabling is at sub level
        final MutablePair<String, List<String>> rfq =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_RFQ_CUSTOMER_TYPES, lang);
        final MutablePair<String, List<String>> approve =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_REQUIRE_ORDER_APPROVE, lang);
        final MutablePair<String, List<String>> blockCheckout =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_CANNOT_PLACE_ORDER, lang);
        final MutablePair<String, List<String>> repeatOrders =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_REPEAT_ORDER_TYPES, lang);
        final MutablePair<String, List<String>> orderLineRemarks =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_B2B_LINE_REMARKS_TYPES, lang);
        final MutablePair<String, List<String>> orderForm =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_B2B_ORDER_FORM_TYPES, lang);
        final MutablePair<String, List<String>> shoppingLists =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_SHOPPING_LIST_TYPES, lang);
        final MutablePair<String, List<String>> managedLists =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_SF_MANAGED_LIST_TYPES, lang);
        final MutablePair<String, List<String>> addressBookDisabled =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES, lang);
        final MutablePair<String, List<String>> addressBookBillingDisabled =
                getCsvShopAttributeConfig(subAttrsMap, AttributeNamesKeys.Shop.SHOP_ADDRESSBOOK_BILL_DISABLED_CUSTOMER_TYPES, lang);

        final Set<String> additionalTypes = new HashSet<>();
        additionalTypes.addAll(ableToRegister.getSecond());
        additionalTypes.addAll(disableAccountDelete.getSecond());
        additionalTypes.addAll(approveRegister.getSecond());
        additionalTypes.addAll(notifyRegister.getSecond());
        additionalTypes.addAll(seeTax.getSecond());
        additionalTypes.addAll(seeNetPrice.getSecond());
        additionalTypes.addAll(seeTaxAmount.getSecond());
        additionalTypes.addAll(changeTax.getSecond());
        additionalTypes.addAll(hidePrices.getSecond());
        additionalTypes.addAll(disableOneAddress.getSecond());
        additionalTypes.addAll(rfq.getSecond());
        additionalTypes.addAll(approve.getSecond());
        additionalTypes.addAll(blockCheckout.getSecond());
        additionalTypes.addAll(repeatOrders.getSecond());
        additionalTypes.addAll(orderLineRemarks.getSecond());
        additionalTypes.addAll(orderForm.getSecond());
        additionalTypes.addAll(shoppingLists.getSecond());
        additionalTypes.addAll(managedLists.getSecond());
        additionalTypes.addAll(addressBookDisabled.getSecond());
        additionalTypes.addAll(addressBookBillingDisabled.getSecond());
        if (CollectionUtils.isNotEmpty(additionalTypes)) {
            additionalTypes.removeAll(knownCustomerTypes);
            if (!additionalTypes.isEmpty()) {
                for (final String newType : additionalTypes) {
                    knownCustomerTypes.add(newType);
                    summary.getCustomerTypes().add(MutablePair.of(newType, newType));
                }
            }
        }

        summary.setCustomerTypesAbleToRegister(ableToRegister);
        summary.setCustomerTypesDisableAccountDelete(disableAccountDelete);
        summary.setCustomerTypesRequireRegistrationApproval(approveRegister);
        summary.setCustomerTypesRequireRegistrationNotification(notifyRegister);
        summary.setCustomerTypesSeeTax(seeTax);
        summary.setCustomerTypesSeeNetPrice(seeNetPrice);
        summary.setCustomerTypesSeeTaxAmount(seeTaxAmount);
        summary.setCustomerTypesChangeTaxView(changeTax);
        summary.setCustomerTypesHidePrices(hidePrices);
        summary.setCustomerTypesDisableOneAddress(disableOneAddress);
        summary.setCustomerTypesRfq(rfq);
        summary.setCustomerTypesOrderApproval(approve);
        summary.setCustomerTypesBlockCheckout(blockCheckout);
        summary.setCustomerTypesRepeatOrders(repeatOrders);
        summary.setCustomerTypesB2BOrderLineRemarks(orderLineRemarks);
        summary.setCustomerTypesB2BOrderForm(orderForm);
        summary.setCustomerTypesShoppingLists(shoppingLists);
        summary.setCustomerTypesManagedLists(managedLists);
        summary.setCustomerTypesAddressBookDisabled(addressBookDisabled);
        summary.setCustomerTypesAddressBookBillingDisabled(addressBookBillingDisabled);
    }

    protected void addCheckoutConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setCheckoutEnableGuest(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_CHECKOUT_ENABLE_GUEST, lang, false));
        summary.setCheckoutEnableCoupons(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_COUPONS, lang, false));
        summary.setCheckoutEnableMessage(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_UPDATE_ENABLE_ORDER_MSG, lang, false));
        summary.setCheckoutEnableQuantityPicker(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.CART_ADD_ENABLE_QTY_PICKER, lang, false));
        summary.setCheckoutEnablePreselectShipping(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_CHECKOUT_PRESELECT_SHIPPING, lang, false));
        summary.setCheckoutEnablePreselectPayment(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_CHECKOUT_PRESELECT_PAYMENT, lang, false));
    }

    protected void addSearchConfig(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {
        summary.setSearchInSubCatsEnable(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_INCLUDE_SUBCATEGORIES_IN_SEARCH, lang, false));
        summary.setSearchCompoundEnable(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SEARCH_ENABLE_COMPOUND, lang, false));
        summary.setSearchSuggestEnable(getBooleanShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SEARCH_ENABLE_SUGGEST, lang, false));
        summary.setSearchSuggestMaxResults(getIntegerShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SEARCH_SUGGEST_MAX_ITEMS, lang, 10));
        summary.setSearchSuggestMinChars(getIntegerShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SEARCH_SUGGEST_MIN_CHARS, lang, 3));
        summary.setSearchSuggestFadeOut(getIntegerShopAttributeConfig(
                attrsMap, AttributeNamesKeys.Shop.SHOP_SEARCH_SUGGEST_FADE_OUT, lang, 3000));
    }

    protected Map<String, VoAttrValueShop> getStringVoAttrValueShopMap(final long shopId, final String code) throws Exception {
        final List<VoAttrValueShop> attrs = voAttributesCRUDTemplate.getAttributes(shopId, code, false);
        final Map<String, VoAttrValueShop> attrsMap = new HashMap<>(attrs.size() * 2);
        for (final VoAttrValueShop attr : attrs) {
            attrsMap.put(attr.getAttribute().getCode(), attr);
        }
        return attrsMap;
    }

    protected void addShopUrls(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopUrl urls = getShopUrlsInternal(shopId);
        summary.setPreviewUrl(urls.getPreviewUrl());
        summary.setPreviewCss(urls.getPreviewCss());
        for (final VoShopUrlDetail url : urls.getUrls()) {
            if (url.isPrimary()) {
                summary.setPrimaryUrlAndThemeChain(
                        MutablePair.of(
                                url.getUrl(),
                                StringUtils.join(themeService.getThemeChainByShopId(shopId, url.getUrl()), " > ")
                        )
                );
            } else {
                summary.getAliasUrlAndThemeChain().add(
                        MutablePair.of(
                                url.getUrl(),
                                StringUtils.join(themeService.getThemeChainByShopId(shopId, url.getUrl()), " > ")
                        )
                );
            }
        }
    }

    protected void addShopAliasess(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopAlias aliases = getShopAliasesInternal(shopId);
        for (final VoShopAliasDetail alias : aliases.getAliases()) {
            summary.getAliases().add(alias.getAlias());
        }
    }

    protected void addShopLocations(final VoShopSummary summary, final long shopId, final String lang) throws Exception {
        final VoShopLocations loc = getShopLocationsInternal(shopId);
        for (final VoLocation country : loc.getAll()) {
            if (loc.getSupportedBilling().contains(country.getCode())) {
                summary.getBillingLocations().add(getSimpleLocationName(country, lang));
            }
            if (loc.getSupportedShipping().contains(country.getCode())) {
                summary.getShippingLocations().add(getSimpleLocationName(country, lang));
            }
        }
    }

    private MutablePair<String, String> getSimpleLocationName(final VoLocation loc, final String lang) {
        String i18n = null;
        if (loc.getDisplayNames() != null) {
            for (final MutablePair<String, String> name : loc.getDisplayNames()) {
                if (I18NModel.DEFAULT.equals(name.getFirst())) {
                    i18n = name.getSecond(); // retain default
                } else if (lang.equals(name.getFirst())) {
                    i18n = name.getSecond();
                    break; // this is exact match
                }
            }
        }
        return MutablePair.of(loc.getCode(), loc.getName() + (i18n != null ? " (" + i18n + ")" : ""));
    }

    protected void addShopCurrencies(final VoShopSummary summary, final long shopId) throws Exception {
        final VoShopSupportedCurrencies curr = getShopCurrenciesInternal(shopId);
        summary.getCurrencies().addAll(curr.getSupported());
    }

    protected void addShopLocales(final VoShopSummary summary, final long shopId, final String lang, final Map<String, VoAttrValueShop> attrsMap) throws Exception {
        final VoShopLanguages langs = getShopLanguagesInternal(shopId);
        for (final String code : langs.getSupported()) {
            for (final MutablePair langAndName : langs.getAll()) {
                if (langAndName.getFirst().equals(code)) {
                    summary.getLocales().add(langAndName);
                    final MutablePair<String, String> override = getShopAttributeConfig(attrsMap, "shop_" + langAndName.getFirst() + ".properties.xml", lang, null);
                    summary.getI18nOverrides().add(MutablePair.of(langAndName.getFirst(), StringUtils.isNotBlank(override.getSecond())));
                }
            }
        }
    }

    protected void addEmailTemplatesBasicSettings(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap) {

        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-cant-allocate-product-qty", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-contactform-request", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-customer-registered", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-newsletter-request", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-canceled", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-confirmed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-allocated", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-inprogress", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-inprogress-wait", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-packing", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-ready", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-delivery-ready-wait", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-new", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-payment-confirmed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-returned", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-shipping-completed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-order-wait-confirmation", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-failed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-shipped", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-payment-shipped-failed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-refund", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-refund-failed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-rfq-new", true);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "adm-managedlist-rejected", true);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-activation", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-change-password", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-deactivation", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-delete", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "customer-registered", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-canceled", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-confirmed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-delivery-readytoshipping", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-delivery-shipped", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-new", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-returned", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "order-shipping-completed", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "payment", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "rfq-new", true);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "shipment-complete", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "sup-order-new", false);
        addEmailTemplateBasicSettings(summary, lang, attrsMap, "managedlist-created", true);

    }

    protected void addEmailTemplateBasicSettings(final VoShopSummary summary, final String lang, final Map<String, VoAttrValueShop> attrsMap, final String template, final boolean yce) {

        summary.getEmailTemplates().add(MutablePair.of(template, Boolean.FALSE));
        summary.getEmailTemplatesYCE().add(MutablePair.of(template, yce));
        final MutablePair<String, String> shopAdmin = getShopAttributeConfig(attrsMap, AttributeNamesKeys.Shop.SHOP_ADMIN_EMAIL, lang, "");
        summary.getEmailTemplatesFrom().add(MutablePair.of(template, shopAdmin.getSecond()));
        summary.getEmailTemplatesTo().add(MutablePair.of(template, template.startsWith("adm-") ? shopAdmin.getSecond() : "-"));
        summary.getEmailTemplatesShop().add(MutablePair.of(template, Boolean.FALSE));

    }

    private String getDisplayName(final List<MutablePair<String, String>> names, final String defName, final String lang) {
        if (CollectionUtils.isNotEmpty(names)) {
            for (final MutablePair<String, String> name : names) {
                if (lang.equals(name.getFirst())) {
                    return name.getSecond();
                }
            }
        }
        return defName;
    }

    private MutablePair<String, Boolean> getBooleanShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang,  final boolean inverse) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(key, !inverse);
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, Boolean.valueOf(attr.getVal()) ? !inverse : inverse);
    }

    private MutablePair<String, Integer> getIntegerShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang,  final int def) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(key, def);
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, NumberUtils.toInt(attr.getVal(), def));
    }

    private MutablePair<String, String> getShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang, final String def) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(key, def);
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        return MutablePair.of(name, StringUtils.isNotBlank(attr.getVal()) ? attr.getVal() : def);
    }

    private MutablePair<String, List<String>> getCsvShopAttributeConfig(final Map<String, VoAttrValueShop> attrsMap, final String key, final String lang) {
        final VoAttrValueShop attr = attrsMap.get(key);
        if (attr == null) {
            return MutablePair.of(key, Collections.emptyList());
        }
        final String name = getDisplayName(attr.getAttribute().getDisplayNames(), attr.getAttribute().getName(), lang);
        final List<String> vals = new ArrayList<>();
        if (StringUtils.isNotBlank(attr.getVal())) {
            for (final String val : Arrays.asList(StringUtils.split(attr.getVal(), ','))) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(val)) {
                    vals.add(val.trim());
                }
            }
        }
        return MutablePair.of(name, Collections.unmodifiableList(vals));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopSeo getShopLocale(long shopId) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(shopId);
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            return voAssemblySupport.assembleVo(VoShopSeo.class, ShopDTO.class, new VoShopSeo(), shopDTO);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopSeo update(final VoShopSeo vo) throws Exception {
        final ShopDTO shopDTO = dtoShopService.getById(vo.getShopId());
        if (shopDTO != null && federationFacade.isShopAccessibleByCurrentManager(shopDTO.getCode())) {
            dtoShopService.update(
                    voAssemblySupport.assembleDto(ShopDTO.class, VoShopSeo.class, shopDTO, vo)
            );
        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getShopLocale(vo.getShopId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopUrl getShopUrls(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            return getShopUrlsInternal(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoShopUrl getShopUrlsInternal(long shopId) throws Exception {
        final List<ShopUrlDTO> shopUrlDTO = dtoShopUrlService.getAllByShopId(shopId);
        final VoShopUrl voShopUrl = new VoShopUrl();
        voShopUrl.setUrls(voAssemblySupport.assembleVos(VoShopUrlDetail.class, ShopUrlDTO.class, shopUrlDTO));
        voShopUrl.setShopId(shopId);

        final String previewURLTemplate = systemService.getPreviewShopURLTemplate();

        String primary = null;
        if (voShopUrl.getUrls() == null || voShopUrl.getUrls().isEmpty()) {
            primary = "localhost";
        } else {
            for (final VoShopUrlDetail url : voShopUrl.getUrls()) {
                if (primary == null || url.isPrimary()) {
                    primary = url.getUrl();
                }
            }
        }

        voShopUrl.setPreviewUrl(previewURLTemplate.replace("{primaryShopURL}", primary));

        final String previewURICss = systemService.getPreviewShopURICss();

        voShopUrl.setPreviewCss(voShopUrl.getPreviewUrl() + previewURICss);


        return voShopUrl;
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public VoShopAlias getShopAliases(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            return getShopAliasesInternal(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoShopAlias getShopAliasesInternal(long shopId) throws Exception {
        final List<ShopAliasDTO> shopAliasDTO = dtoShopAliasService.getAllByShopId(shopId);
        final VoShopAlias voShopAlias = new VoShopAlias();
        voShopAlias.setAliases(voAssemblySupport.assembleVos(VoShopAliasDetail.class, ShopAliasDTO.class, shopAliasDTO));
        voShopAlias.setShopId(shopId);

        return voShopAlias;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopAlias update(VoShopAlias vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {
            final List<ShopAliasDTO> originalShopAliasDTOs = dtoShopAliasService.getAllByShopId(vo.getShopId());
            final Set<Long> updated = new HashSet<>();
            for (VoShopAliasDetail aliasDetail : vo.getAliases()) {
                ShopAliasDTO shopAliasDTO =
                        voAssemblySupport.assembleDto(ShopAliasDTO.class, VoShopAliasDetail.class, new ShopAliasDTOImpl(), aliasDetail);
                shopAliasDTO.setShopId(vo.getShopId());
                if (aliasDetail.getAliasId() == 0) {  //new one insert
                    dtoShopAliasService.create(shopAliasDTO);
                } else { //update
                    dtoShopAliasService.update(shopAliasDTO);
                    updated.add(shopAliasDTO.getStoreAliasId());
                }
            }
            for (ShopAliasDTO dto : originalShopAliasDTOs) {
                if (!updated.contains(dto.getId())) {
                    dtoShopAliasService.remove(dto.getId());
                }
            }
            return getShopAliases(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopSupportedCurrencies getShopCurrencies(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            return getShopCurrenciesInternal(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoShopSupportedCurrencies getShopCurrenciesInternal(long shopId) throws Exception {
        VoShopSupportedCurrencies ssc = new VoShopSupportedCurrencies();
        ssc.setShopId(shopId);
        ssc.setAll(new ArrayList<>());
        ssc.setSupported(new ArrayList<>());

        final List<String> all = currencyService.getSupportedCurrencies();
        final String supportedStr = dtoShopService.getSupportedCurrencies(shopId);
        final List<String> supported = supportedStr == null ? Collections.emptyList() : Arrays.asList(supportedStr.split(","));
        final Map<String, String> names = currencyService.getCurrencyName();

        for (final String one : all) {
            if (names.containsKey(one)) {
                ssc.getAll().add(MutablePair.of(one, names.get(one)));
            } else {
                ssc.getAll().add(MutablePair.of(one, one));
            }
        }

        for (final String one : supported) {
            if (names.containsKey(one)) {
                ssc.getSupported().add(MutablePair.of(one, names.get(one)));
            } else {
                ssc.getSupported().add(MutablePair.of(one, one));
            }
        }

        return ssc;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopSupportedCurrencies update(VoShopSupportedCurrencies vo) throws Exception {
        if (vo != null && federationFacade.isShopAccessibleByCurrentManager(vo.getShopId())) {

            final List<String> all = currencyService.getSupportedCurrencies();
            final List<String> supported = new ArrayList<>();
            if (vo.getSupported() != null) {

                for (final MutablePair<String, String> currency : vo.getSupported()) {

                    if (all.contains(currency.getFirst())) {
                        supported.add(currency.getFirst());
                    }

                }

            }

            dtoShopService.updateSupportedCurrencies(
                    vo.getShopId(),
                    StringUtils.join(supported.toArray(), ",")
            );
            return getShopCurrencies(vo.getShopId());
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoShopLanguages getShopLanguages(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            return getShopLanguagesInternal(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoShopLanguages getShopLanguagesInternal(long shopId) throws Exception {
        final VoShopLanguages voShopLanguages = new VoShopLanguages();
        String lng = dtoShopService.getSupportedLanguages(shopId);
        voShopLanguages.setSupported(lng == null ? Collections.emptyList() : Arrays.asList(lng.split(",")));
        voShopLanguages.setAll(VoUtils.adaptMapToPairs(languageService.getLanguageName()));
        voShopLanguages.setShopId(shopId);
        return voShopLanguages;
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public VoShopLocations getShopLocations(long shopId) throws Exception {
        if (federationFacade.isShopAccessibleByCurrentManager(shopId)) {
            return getShopLocationsInternal(shopId);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    private VoShopLocations getShopLocationsInternal(long shopId) throws Exception {
        final VoShopLocations shopLocations = new VoShopLocations();

        String billing = dtoShopService.getSupportedBillingCountries(shopId);
        String shipping = dtoShopService.getSupportedShippingCountries(shopId);
        shopLocations.setSupportedBilling(billing == null ? Collections.emptyList() : Arrays.asList(billing.split(",")));
        shopLocations.setSupportedShipping(shipping == null ? Collections.emptyList() : Arrays.asList(shipping.split(",")));

        final List<Country> countries = countryService.findAll();
        final List<VoLocation> all = new ArrayList<>();
        for (final Country country : countries) {

            final VoLocation loc = new VoLocation();
            loc.setCode(country.getCountryCode());
            loc.setName(country.getName());
            final List<MutablePair<String, String>> names = new ArrayList<>();
            if (country.getDisplayName() != null) {
                for (final Map.Entry<String, String> name : country.getDisplayName().getAllValues().entrySet()) {
                    names.add(MutablePair.of(name.getKey(), name.getValue()));
                }
            }
            loc.setDisplayNames(names);

            all.add(loc);
        }

        shopLocations.setAll(all);
        shopLocations.setShopId(shopId);
        return shopLocations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public VoShop updateDisabledFlag(final long shopId, final boolean disabled) throws Exception {
        final ShopDTO shop = dtoShopService.getById(shopId);
        if (shop.getMasterId() == null && federationFacade.isCurrentUserSystemAdmin() ||
                shop.getMasterId() != null && federationFacade.isShopAccessibleByCurrentManager(shop.getMasterId())) {
            final ShopDTO dto = dtoShopService.updateDisabledFlag(shopId, disabled);
            if (dto != null) {
                return getById(shopId);
            }
        }
        throw new AccessDeniedException("Access is denied");
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueShop> getShopAttributes(final long shopId, final boolean includeSecure) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(shopId, includeSecure);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueShop> update(final List<MutablePair<VoAttrValueShop, Boolean>> vo, final boolean includeSecure) throws Exception {

        final long shopId = this.voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, includeSecure);

        return getShopAttributes(shopId, includeSecure);

    }


    void collectKnownCustomerTypesInShop(final String locale, final List<MutablePair<String, String>> types, final Set<String> knownCustomerTypes, final long shopId) throws Exception {

        final VoShopSummary summary = new VoShopSummary();
        fillShopSummaryDetails(summary, shopId, locale);

        for (final MutablePair<String, String> typeAndName : summary.getCustomerTypes()) {
            if (!AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST.equals(typeAndName.getFirst())
                    && !knownCustomerTypes.contains(typeAndName.getFirst())) {
                knownCustomerTypes.add(typeAndName.getFirst());
                types.add(typeAndName);
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MutablePair<String, String>> getAvailableShopsCustomerTypes(final String locale) throws Exception {

        final List<MutablePair<String, String>> types = new ArrayList<>();

        final Set<String> knownCustomerTypes = new HashSet<>();
        for (final Long shopId : federationFacade.getAccessibleShopIdsByCurrentManager()) {

            collectKnownCustomerTypesInShop(locale, types, knownCustomerTypes, shopId);

        }

        return types;

    }

    @Override
    public List<MutablePair<String, String>> getAvailableShopsCustomerTypes(final List<Long> shopIds, final String locale) throws Exception {

        final List<MutablePair<String, String>> types = new ArrayList<>();

        final Set<String> knownCustomerTypes = new HashSet<>();

        for (final Long shopId : shopIds) {

            if (shopId != null && federationFacade.isShopAccessibleByCurrentManager(shopId)) {

                collectKnownCustomerTypesInShop(locale, types, knownCustomerTypes, shopId);

            }

        }

        return types;

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
