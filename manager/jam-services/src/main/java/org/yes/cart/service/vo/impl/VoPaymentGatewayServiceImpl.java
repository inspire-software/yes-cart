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
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPaymentGatewayService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 17:47
 */
public class VoPaymentGatewayServiceImpl implements VoPaymentGatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(VoPaymentGatewayServiceImpl.class);

    private static final String DEFAULT_SHOP_CODE = "DEFAULT";

    private static final Comparator<VoPaymentGatewayInfo> VO_PAYMENT_GATEWAY_INFO_COMPARATOR = new Comparator<VoPaymentGatewayInfo>() {

        /** {@inheritDoc} */
        @Override
        public int compare(VoPaymentGatewayInfo o1, VoPaymentGatewayInfo o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    private static final Comparator<VoPaymentGateway> VO_PAYMENT_GATEWAY_COMPARATOR = new Comparator<VoPaymentGateway>() {

        /** {@inheritDoc} */
        @Override
        public int compare(VoPaymentGateway o1, VoPaymentGateway o2) {
            final int rez = Integer.compare(o1.getRank(), o2.getRank());
            if (rez == 0) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
            return rez;
        }
    };

    private final PaymentModulesManager paymentModulesManager;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    private final FederationFacade federationFacade;

    private final VoAssemblySupport voAssemblySupport;

    public VoPaymentGatewayServiceImpl(final PaymentModulesManager paymentModulesManager,
                                       final CustomerOrderPaymentService customerOrderPaymentService,
                                       final FederationFacade federationFacade,
                                       final VoAssemblySupport voAssemblySupport) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }


    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGatewayInfo> getPaymentGateways(final String lang) throws Exception {

        List<VoPaymentGatewayInfo> rez = new ArrayList<>();
        List<MutablePair<String, String>> active = getAllowedPaymentGateways(lang);
        List<MutablePair<String, String>> available = getAvailablePaymentGateways(lang);
        Set<String> activeLabels = new HashSet<>();
        for (MutablePair<String, String> pair : active) {
            rez.add(
                    new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), true)
            );
            activeLabels.add(pair.getFirst());
        }

        for (MutablePair<String, String> pair : available) {
            if (!activeLabels.contains(pair.getFirst())) {
                rez.add(
                        new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), false)
                );
            }
        }

        sortPgInfo(rez);

        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(final String lang,
                                                                final String shopCode) throws Exception {

        List<VoPaymentGatewayInfo> rez = new ArrayList<>();
        List<MutablePair<String, String>> active = getAllowedPaymentGatewaysForShop(lang, shopCode);
        List<MutablePair<String, String>> available = getAvailablePaymentGatewaysForShop(lang, shopCode);
        Set<String> activeLabels = new HashSet<>();
        for (MutablePair<String, String> pair : active) {
            rez.add(
                    new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), true)
            );
            activeLabels.add(pair.getFirst());
        }

        for (MutablePair<String, String> pair : available) {
            if (!activeLabels.contains(pair.getFirst())) {
                rez.add(
                        new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), false)
                );
            }
        }

        sortPgInfo(rez);

        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public void fillShopSummaryDetails(final VoShopSummary summary, final String shopCode, final String lang) throws Exception {

        if (federationFacade.isManageable(summary.getCode(), ShopDTO.class)) {

            List<VoPaymentGatewayInfo> rez = new ArrayList<>();
            List<MutablePair<String, String>> active = getAllowedPaymentGatewaysForShopInternal(lang, shopCode);
            List<MutablePair<String, String>> available = getAvailablePaymentGatewaysForShopInternal(lang, shopCode);
            Set<String> activeLabels = new HashSet<>();
            for (MutablePair<String, String> pair : active) {
                rez.add(
                        new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), true)
                );
                activeLabels.add(pair.getFirst());
            }

            for (MutablePair<String, String> pair : available) {
                if (!activeLabels.contains(pair.getFirst())) {
                    rez.add(
                            new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), false)
                    );
                }
            }

            sortPgInfo(rez);

            for (final VoPaymentGatewayInfo pg : rez) {

                summary.getPaymentGateways().add(MutablePair.of(pg.getName(), !pg.isActive()));

            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    private void sortPgInfo(final List<VoPaymentGatewayInfo> rez) {
        rez.sort(VO_PAYMENT_GATEWAY_INFO_COMPARATOR);
    }

    private void sortPg(final List<VoPaymentGateway> rez) {
        rez.sort(VO_PAYMENT_GATEWAY_COMPARATOR);
    }

    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(final String lang) throws Exception {

        List<VoPaymentGatewayInfo> rez = new ArrayList<>();
        List<MutablePair<String, String>> active = getAllowedPaymentGateways(lang);
        for (MutablePair<String, String> pair : active) {
            rez.add(
                    new VoPaymentGatewayInfo(pair.getFirst(), pair.getSecond(), true)
            );
        }

        sortPgInfo(rez);

        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public List<MutablePair<String, String>> getAllowedPaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, DEFAULT_SHOP_CODE);
        return fillPaymentDescriptors(descriptors, lang);
    }

    /** {@inheritDoc} */
    @Override
    public List<MutablePair<String, String>> getAllowedPaymentGatewaysForShop(final String lang,
                                                                              final String shopCode) throws Exception {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            return getAllowedPaymentGatewaysForShopInternal(lang, shopCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    private List<MutablePair<String, String>> getAllowedPaymentGatewaysForShopInternal(final String lang,
                                                                                       final String shopCode) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, shopCode);
        return fillPaymentDescriptors(descriptors, lang);
    }

    /** {@inheritDoc} */
    @Override
    public List<MutablePair<String, String>> getAvailablePaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, DEFAULT_SHOP_CODE);
        final List<MutablePair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
        rez.removeAll(getAllowedPaymentGateways(lang));
        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public List<MutablePair<String, String>> getAvailablePaymentGatewaysForShop(final String lang,
                                                                                final String shopCode) throws Exception {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            return getAvailablePaymentGatewaysForShopInternal(lang, shopCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    private List<MutablePair<String, String>> getAvailablePaymentGatewaysForShopInternal(final String lang,
                                                                                         final String shopCode) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, shopCode);
        final List<MutablePair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
        rez.removeAll(getAllowedPaymentGatewaysForShopInternal(lang, shopCode));
        return rez;
    }


    private List<MutablePair<String, String>> fillPaymentDescriptors(final List<PaymentGatewayDescriptor> descriptors,
                                                              final String lang) {
        final List<MutablePair<String, String>> rez = new ArrayList<>(descriptors.size());
        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel(), DEFAULT_SHOP_CODE);
            final MutablePair<String, String> pairCandidate = new MutablePair<>(
                    descr.getLabel(),
                    paymentGateway.getName(lang)
            );
            rez.add(pairCandidate);
        }
        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGateway> getPaymentGatewaysWithParameters(final String lang,
                                                                   final boolean includeSecure) throws Exception {

        if (federationFacade.isCurrentUserSystemAdmin()) {

            final List<VoPaymentGatewayInfo> info = this.getPaymentGateways(lang);
            final List<VoPaymentGateway> vos = new ArrayList<>(info.size());
            for (final VoPaymentGatewayInfo pgInfo : info) {

                vos.add(getPaymentGatewayWithParameters(pgInfo, DEFAULT_SHOP_CODE, includeSecure));

            }

            sortPg(vos);

            return vos;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(final String lang,
                                                                          final String shopCode,
                                                                          final boolean includeSecure) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(shopCode)) {

            final List<VoPaymentGatewayInfo> info = this.getPaymentGatewaysForShop(lang, shopCode);
            final List<VoPaymentGateway> vos = new ArrayList<>(info.size());
            for (final VoPaymentGatewayInfo pgInfo : info) {

                vos.add(getPaymentGatewayWithParameters(pgInfo, shopCode, includeSecure));

            }

            sortPg(vos);

            return vos;
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }


    private VoPaymentGateway getPaymentGatewayWithParameters(final VoPaymentGatewayInfo pgInfo,
                                                             final String shopCode,
                                                             final boolean includeSecure) {
        final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(pgInfo.getLabel(), shopCode);

        final VoPaymentGatewayFeature feature = voAssemblySupport.assembleVo(
                VoPaymentGatewayFeature.class, PaymentGatewayFeature.class,
                new VoPaymentGatewayFeature(), paymentGateway.getPaymentGatewayFeatures()
        );

        final List<VoPaymentGatewayParameter> parameters = voAssemblySupport.assembleVos(
                VoPaymentGatewayParameter.class, PaymentGatewayParameter.class,
                new ArrayList<>(getPaymentGatewayParameters(paymentGateway, shopCode, includeSecure))
        );

        int rank = 100; // default priority in modules

        for (final VoPaymentGatewayParameter parameter : parameters) {
            if ("priority".equals(parameter.getLabel())) {
                rank = NumberUtils.toInt(parameter.getValue(), rank);
                break;
            }
        }

        return new VoPaymentGateway(
                pgInfo.getLabel(), pgInfo.getName(), pgInfo.isActive(), rank, shopCode,
                feature, parameters
        );
    }

    private Collection<PaymentGatewayParameter> getPaymentGatewayParameters(final PaymentGateway paymentGateway,
                                                                            final String shopCode,
                                                                            final boolean includeSecure) {

        if (DEFAULT_SHOP_CODE.equals(shopCode)) {

            final List<PaymentGatewayParameter> defParams = new ArrayList<>(paymentGateway.getPaymentGatewayParameters());
            defParams.removeIf(param -> !includeSecure && param.isSecure());
            return defParams;

        } else {

            final List<PaymentGatewayParameter> shopOnly = new ArrayList<>(paymentGateway.getPaymentGatewayParameters());
            final Iterator<PaymentGatewayParameter> shopOnlyIt = shopOnly.iterator();
            final String prefix = labelToShopLabel(shopCode, "");
            while (shopOnlyIt.hasNext()) {
                final PaymentGatewayParameter param = shopOnlyIt.next();
                if ((!includeSecure && param.isSecure()) || !param.getLabel().startsWith(prefix)) {
                    shopOnlyIt.remove();
                }
            }
            return shopOnly;
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGatewayParameter> updateParameters(final String pgLabel,
                                                            final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo,
                                                            final boolean includeSecure) throws Exception {
        return updateParameters(DEFAULT_SHOP_CODE, pgLabel, vo, includeSecure);
    }

    /** {@inheritDoc} */
    @Override
    public List<VoPaymentGatewayParameter> updateParameters(final String shopCode,
                                                            final String pgLabel,
                                                            final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo,
                                                            final boolean includeSecure) throws Exception {

        final boolean systemSettings = DEFAULT_SHOP_CODE.equals(shopCode);

        if ((systemSettings && !federationFacade.isCurrentUserSystemAdmin()) ||
                (!systemSettings && !federationFacade.isShopAccessibleByCurrentManager(shopCode))) {
            throw new AccessDeniedException("Access is denied");
        }

        PaymentGateway pg = paymentModulesManager.getPaymentGateway(pgLabel, shopCode);
        final VoAssemblySupport.VoAssembler<VoPaymentGatewayParameter, PaymentGatewayParameter> asm =
                voAssemblySupport.with(VoPaymentGatewayParameter.class, PaymentGatewayParameter.class);

        Map<Long, PaymentGatewayParameter> existing = mapAvById((List) getPaymentGatewayParameters(pg, shopCode, includeSecure));

        for (final MutablePair<VoPaymentGatewayParameter, Boolean> item : vo) {
            if (item.getFirst().getPaymentGatewayParameterId() > 0L && !pg.getLabel().equals(item.getFirst().getPgLabel())) {
                throw new AccessDeniedException("Access is denied");
            }

            if (Boolean.valueOf(item.getSecond())) {
                // delete mode
                final PaymentGatewayParameter param = existing.get(item.getFirst().getPaymentGatewayParameterId());
                if (param != null) {
                    if ((includeSecure || !param.isSecure())) {
                        LOG.info("Removing PG({}/{}) for {}, value was {}",
                                shopCode, param.getPgLabel(), param.getLabel(), param.getValue());
                        param.setValue("");
                        pg.updateParameter(param);
                    } else {
                        throw new AccessDeniedException("Access is denied");
                    }
                } else {
                    LOG.warn("Update skipped for inexistent ID {}", item.getFirst().getPaymentGatewayParameterId());
                }
            } else if (item.getFirst().getPaymentGatewayParameterId() > 0L) {
                // update mode
                final PaymentGatewayParameter param = existing.get(item.getFirst().getPaymentGatewayParameterId());
                if (param != null) {
                    if ((includeSecure || !param.isSecure())) {
                        LOG.info("Updating PG({}/{}) for {}, value was {}, now {}",
                                shopCode, param.getPgLabel(), param.getLabel(), param.getValue(), item.getFirst().getValue());
                        param.setValue(item.getFirst().getValue());
                        pg.updateParameter(param);
                    } else {
                        throw new AccessDeniedException("Access is denied");
                    }
                } else {
                    LOG.warn("Update skipped for inexistent ID {}", item.getFirst().getPaymentGatewayParameterId());
                }
            } else {
                // insert mode

                if (systemSettings) {

                    final Collection<PaymentGatewayParameter> all = existing.values();
                    final String name = item.getFirst().getName();
                    final String label = item.getFirst().getLabel();
                    final String value = item.getFirst().getValue();
                    final String businesstype = item.getFirst().getBusinesstype();
                    final boolean secure = item.getFirst().isSecure();

                    if (includeSecure || !secure) {
                        final boolean duplicate = createPaymentGatewayParameter(pg, all, name, label, name, value, StringUtils.isBlank(businesstype) ? null : businesstype, secure);
                        if (duplicate) {
                            LOG.warn("Update skipped because create mode detected duplicate label {}", label);
                        } else {
                            LOG.info("Creating PG({}/{}) for {}, value {}",
                                    shopCode, pg.getLabel(), label, value);
                        }
                    } else {
                        throw new AccessDeniedException("Access is denied");
                    }

                } else {

                    LOG.warn("Update skipped because create mode is not allowed for shops");

                }

            }

        }

        if (pg != null) {
            return voAssemblySupport.assembleVos(
                    VoPaymentGatewayParameter.class, PaymentGatewayParameter.class,
                    new ArrayList<>(getPaymentGatewayParameters(pg, shopCode, includeSecure))
            );
        }

        return Collections.emptyList();

    }

    private boolean createPaymentGatewayParameter(final PaymentGateway pg,
                                                  final Collection<PaymentGatewayParameter> all,
                                                  final String name,
                                                  final String label,
                                                  final String description,
                                                  final String value,
                                                  final String businesstype,
                                                  final boolean secure) {

        for (PaymentGatewayParameter param : all) {
            if (param.getLabel().equals(label)) {
                return true;
            }
        }

        final PaymentGatewayParameter parameter = new PaymentGatewayParameterEntity();
        parameter.setPgLabel(pg.getLabel());
        parameter.setName(name);
        parameter.setLabel(label);
        parameter.setDescription(description);
        parameter.setValue(value);
        parameter.setGuid(UUID.randomUUID().toString());
        parameter.setBusinesstype(businesstype);
        parameter.setSecure(secure);
        pg.addParameter(parameter);

        return false;
    }


    private Map<Long, PaymentGatewayParameter> mapAvById(final List<PaymentGatewayParameter> entityAttributes) {
        Map<Long, PaymentGatewayParameter> map = new HashMap<>();
        for (final PaymentGatewayParameter dto : entityAttributes) {
            map.put(dto.getPaymentGatewayParameterId(), dto);
        }
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public void updateDisabledFlag(final String pgLabel, final boolean disabled) throws Exception {
        updateDisabledFlag(DEFAULT_SHOP_CODE, pgLabel, disabled);
    }

    /** {@inheritDoc} */
    @Override
    public void updateDisabledFlag(final String shopCode, final String pgLabel, final boolean disabled) throws Exception {

        final boolean systemSettings = DEFAULT_SHOP_CODE.equals(shopCode);

        if ((systemSettings && !federationFacade.isCurrentUserSystemAdmin()) ||
                (!systemSettings && !federationFacade.isShopAccessibleByCurrentManager(shopCode))) {
            throw new AccessDeniedException("Access is denied");
        }

        if (systemSettings) {

            if (disabled) {

                paymentModulesManager.disallowPaymentGateway(pgLabel);

            } else {

                paymentModulesManager.allowPaymentGateway(pgLabel);

            }

        } else {

            if (disabled) {

                paymentModulesManager.disallowPaymentGatewayForShop(pgLabel, shopCode);

            } else {

                paymentModulesManager.allowPaymentGatewayForShop(pgLabel, shopCode);
                final PaymentGateway pgDefault = paymentModulesManager.getPaymentGateway(pgLabel, DEFAULT_SHOP_CODE);
                final PaymentGateway pgShop = paymentModulesManager.getPaymentGateway(pgLabel, shopCode);
                getPaymentGatewayParameters(pgShop, shopCode, true);

                final Collection<PaymentGatewayParameter> defParams = getPaymentGatewayParameters(pgDefault, DEFAULT_SHOP_CODE, true);
                final Collection<PaymentGatewayParameter> shopParams = getPaymentGatewayParameters(pgShop, shopCode, true);

                for (final PaymentGatewayParameter defParam : defParams) {

                    final String shopLabel = labelToShopLabel(shopCode, defParam.getLabel());
                    final boolean duplicate = createPaymentGatewayParameter(
                            pgShop,
                            shopParams,
                            defParam.getName(),
                            shopLabel,
                            defParam.getDescription(),
                            defParam.getValue(),
                            defParam.getBusinesstype(),
                            defParam.isSecure()
                    );

                    if (duplicate) {

                        LOG.debug("Copy default skipped for label {}/{} because detected duplicate", shopCode, shopLabel);

                    } else {

                        LOG.info("Copy default label {}/{} with value {}", shopCode, shopLabel, defParam.getValue());

                    }

                }


            }

        }

        LOG.warn("PG{} on {} is {}", pgLabel, shopCode, disabled ? "DISABLED" : "ENABLED");

    }

    private String labelToShopLabel(String shopCode, String label) {
        return "#" + shopCode + "_"  + label;
    }

}
