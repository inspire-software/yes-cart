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
    public List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(final String lang,
                                                                final String shopCode) throws Exception {

        List<VoPaymentGatewayInfo> rez = new ArrayList<VoPaymentGatewayInfo> ();
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
    public void fillShopSummaryDetails(final VoShopSummary summary, final String shopCode, final String lang) throws Exception {

        if (federationFacade.isManageable(summary.getCode(), ShopDTO.class)) {

            List<VoPaymentGatewayInfo> rez = new ArrayList<VoPaymentGatewayInfo> ();
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
        Collections.sort(
                rez,
                new Comparator<VoPaymentGatewayInfo>() {

                    /** {@inheritDoc} */
                    public int compare(VoPaymentGatewayInfo o1, VoPaymentGatewayInfo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
        );
    }

    /** {@inheritDoc} */
    public List<VoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(final String lang) throws Exception {

        List<VoPaymentGatewayInfo> rez = new ArrayList<VoPaymentGatewayInfo> ();
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
    public List<MutablePair<String, String>> getAllowedPaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, DEFAULT_SHOP_CODE);
        return fillPaymentDescriptors(descriptors, lang);
    }

    /** {@inheritDoc} */
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
    public List<MutablePair<String, String>> getAvailablePaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, DEFAULT_SHOP_CODE);
        final List<MutablePair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
        rez.removeAll(getAllowedPaymentGateways(lang));
        return rez;
    }

    /** {@inheritDoc} */
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
            final MutablePair<String, String> pairCandidate = new MutablePair<String, String>(
                    descr.getLabel(),
                    paymentGateway.getName(lang)
            );
            rez.add(pairCandidate);
        }
        return rez;
    }

    /** {@inheritDoc} */
    public List<VoPaymentGateway> getPaymentGatewaysWithParameters(final String lang) throws Exception {

        if (federationFacade.isCurrentUserSystemAdmin()) {

            final List<VoPaymentGatewayInfo> info = this.getPaymentGateways(lang);
            final List<VoPaymentGateway> vos = new ArrayList<>(info.size());
            for (final VoPaymentGatewayInfo pgInfo : info) {

                vos.add(getPaymentGatewayWithParameters(pgInfo, DEFAULT_SHOP_CODE));

            }

            return vos;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /** {@inheritDoc} */
    public List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(final String lang, final String shopCode) throws Exception {

        if (federationFacade.isShopAccessibleByCurrentManager(shopCode)) {

            final List<VoPaymentGatewayInfo> info = this.getPaymentGatewaysForShop(lang, shopCode);
            final List<VoPaymentGateway> vos = new ArrayList<>(info.size());
            for (final VoPaymentGatewayInfo pgInfo : info) {

                vos.add(getPaymentGatewayWithParameters(pgInfo, shopCode));

            }

            return vos;
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }


    private VoPaymentGateway getPaymentGatewayWithParameters(final VoPaymentGatewayInfo pgInfo, final String shopCode) {
        final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(pgInfo.getLabel(), shopCode);

        final VoPaymentGatewayFeature feature = voAssemblySupport.assembleVo(
                VoPaymentGatewayFeature.class, PaymentGatewayFeature.class,
                new VoPaymentGatewayFeature(), paymentGateway.getPaymentGatewayFeatures()
        );

        final List<VoPaymentGatewayParameter> parameters = voAssemblySupport.assembleVos(
                VoPaymentGatewayParameter.class, PaymentGatewayParameter.class,
                new ArrayList<PaymentGatewayParameter>(getPaymentGatewayParameters(paymentGateway, shopCode))
        );

        return new VoPaymentGateway(
                pgInfo.getLabel(), pgInfo.getName(), pgInfo.isActive(), DEFAULT_SHOP_CODE,
                feature, parameters
        );
    }

    private Collection<PaymentGatewayParameter> getPaymentGatewayParameters(final PaymentGateway paymentGateway, final String shopCode) {

        if (DEFAULT_SHOP_CODE.equals(shopCode)) {

            return new ArrayList<PaymentGatewayParameter>(paymentGateway.getPaymentGatewayParameters());

        } else {

            final List<PaymentGatewayParameter> shopOnly = new ArrayList<PaymentGatewayParameter>(paymentGateway.getPaymentGatewayParameters());
            final Iterator<PaymentGatewayParameter> shopOnlyIt = shopOnly.iterator();
            final String prefix = labelToShopLabel(shopCode, "");
            while (shopOnlyIt.hasNext()) {
                final PaymentGatewayParameter param = shopOnlyIt.next();
                if (!param.getLabel().startsWith(prefix)) {
                    shopOnlyIt.remove();
                }
            }
            return shopOnly;
        }

    }

    /** {@inheritDoc} */
    public List<VoPaymentGatewayParameter> update(final String pgLabel, final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception {
        return update(DEFAULT_SHOP_CODE, pgLabel, vo);
    }

    /** {@inheritDoc} */
    public List<VoPaymentGatewayParameter> update(final String shopCode, final String pgLabel, final List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception {

        final boolean systemSettings = DEFAULT_SHOP_CODE.equals(shopCode);

        if ((systemSettings && !federationFacade.isCurrentUserSystemAdmin()) ||
                (!systemSettings && !federationFacade.isShopAccessibleByCurrentManager(shopCode))) {
            throw new AccessDeniedException("Access is denied");
        }

        PaymentGateway pg = paymentModulesManager.getPaymentGateway(pgLabel, shopCode);
        final VoAssemblySupport.VoAssembler<VoPaymentGatewayParameter, PaymentGatewayParameter> asm =
                voAssemblySupport.with(VoPaymentGatewayParameter.class, PaymentGatewayParameter.class);

        Map<Long, PaymentGatewayParameter> existing = mapAvById((List) getPaymentGatewayParameters(pg, shopCode));

        for (final MutablePair<VoPaymentGatewayParameter, Boolean> item : vo) {
            if (item.getFirst().getPaymentGatewayParameterId() > 0L && !pg.getLabel().equals(item.getFirst().getPgLabel())) {
                throw new AccessDeniedException("Access is denied");
            }

            if (Boolean.valueOf(item.getSecond())) {
                // delete mode
                final PaymentGatewayParameter param = existing.get(item.getFirst().getPaymentGatewayParameterId());
                if (param != null) {
                    LOG.info("Removing PG({}/{}) for {}, value was {}",
                            new Object[]{shopCode, param.getPgLabel(), param.getLabel(), param.getValue()});
                    param.setValue("");
                    pg.updateParameter(param);
                } else {
                    LOG.warn("Update skipped for inexistent ID {}", item.getFirst().getPaymentGatewayParameterId());
                }
            } else if (item.getFirst().getPaymentGatewayParameterId() > 0L) {
                // update mode
                final PaymentGatewayParameter param = existing.get(item.getFirst().getPaymentGatewayParameterId());
                if (param != null) {
                    LOG.info("Updating PG({}/{}) for {}, value was {}, now {}",
                            new Object[]{shopCode, param.getPgLabel(), param.getLabel(), param.getValue(), item.getFirst().getValue()});
                    param.setValue(item.getFirst().getValue());
                    pg.updateParameter(param);
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

                    final boolean duplicate = createPaymentGatewayParameter(pg, all, name, label, name, value);
                    if (duplicate) {
                        LOG.warn("Update skipped because create mode detected duplicate label {}", label);
                    } else {
                        LOG.info("Creating PG({}/{}) for {}, value {}",
                                new Object[]{shopCode, pg.getLabel(), label, value});
                    }

                } else {

                    LOG.warn("Update skipped because create mode is not allowed for shops");

                }

            }

        }

        if (pg != null) {
            return voAssemblySupport.assembleVos(
                    VoPaymentGatewayParameter.class, PaymentGatewayParameter.class,
                    new ArrayList<PaymentGatewayParameter>(getPaymentGatewayParameters(pg, shopCode))
            );
        }

        return Collections.emptyList();

    }

    private boolean createPaymentGatewayParameter(final PaymentGateway pg,
                                                  final Collection<PaymentGatewayParameter> all,
                                                  final String name,
                                                  final String label,
                                                  final String description,
                                                  final String value) {

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
        pg.addParameter(parameter);

        return false;
    }


    private Map<Long, PaymentGatewayParameter> mapAvById(final List<PaymentGatewayParameter> entityAttributes) {
        Map<Long, PaymentGatewayParameter> map = new HashMap<Long, PaymentGatewayParameter>();
        for (final PaymentGatewayParameter dto : entityAttributes) {
            map.put(dto.getPaymentGatewayParameterId(), dto);
        }
        return map;
    }

    /** {@inheritDoc} */
    public void updateDisabledFlag(final String pgLabel, final boolean disabled) throws Exception {
        updateDisabledFlag(DEFAULT_SHOP_CODE, pgLabel, disabled);
    }

    /** {@inheritDoc} */
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
                getPaymentGatewayParameters(pgShop, shopCode);

                final Collection<PaymentGatewayParameter> defParams = getPaymentGatewayParameters(pgDefault, DEFAULT_SHOP_CODE);
                final Collection<PaymentGatewayParameter> shopParams = getPaymentGatewayParameters(pgShop, shopCode);

                for (final PaymentGatewayParameter defParam : defParams) {

                    final String shopLabel = labelToShopLabel(shopCode, defParam.getLabel());
                    final boolean duplicate = createPaymentGatewayParameter(pgShop, shopParams, defParam.getName(), shopLabel, defParam.getDescription(), defParam.getValue());

                    if (duplicate) {

                        LOG.debug("Copy default skipped for label {}/{} because detected duplicate", shopCode, shopLabel);

                    } else {

                        LOG.info("Copy default label {}/{} with value {}", new Object[] { shopCode, shopLabel, defParam.getValue() });

                    }

                }


            }

        }

        LOG.warn("PG{} on {} is {}", new Object[] { pgLabel, shopCode, disabled ? "DISABLED" : "ENABLED" });

    }

    private String labelToShopLabel(String shopCode, String label) {
        return "#" + shopCode + "_"  + label;
    }

}
