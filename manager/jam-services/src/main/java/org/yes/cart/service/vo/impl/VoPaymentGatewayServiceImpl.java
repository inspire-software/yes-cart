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
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
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

    /** {@inheritDoc}*/
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

    /** {@inheritDoc}*/
    public List<MutablePair<String, String>> getAllowedPaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, DEFAULT_SHOP_CODE);
        return fillPaymentDescriptors(descriptors, lang);
    }

    /** {@inheritDoc}*/
    public List<MutablePair<String, String>> getAllowedPaymentGatewaysForShop(final String lang,
                                                                       final String shopCode) throws Exception {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, shopCode);
            return fillPaymentDescriptors(descriptors, lang);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc}*/
    public List<MutablePair<String, String>> getAvailablePaymentGateways(final String lang) throws Exception {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, DEFAULT_SHOP_CODE);
        final List<MutablePair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
        rez.removeAll(getAllowedPaymentGateways(lang));
        return rez;
    }

    /** {@inheritDoc}*/
    public List<MutablePair<String, String>> getAvailablePaymentGatewaysForShop(final String lang,
                                                                         final String shopCode) throws Exception {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, shopCode);
            final List<MutablePair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
            rez.removeAll(getAllowedPaymentGatewaysForShop(lang, shopCode));
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }

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


}
