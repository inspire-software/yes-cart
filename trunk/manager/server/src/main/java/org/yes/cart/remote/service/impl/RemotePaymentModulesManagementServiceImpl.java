/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.remote.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.impl.DtoPaymentGatewayInfoImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.remote.service.RemotePaymentModulesManagementService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.*;

/**
 *
 * Remote service to manage payment gateways and his parameters.
 * Delete and add parameters operation are prohibited for security reason. This two operations are very rare
 * and can not be performed without tech personal support.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/22/12
 * Time: 8:59 PM
 */
public class RemotePaymentModulesManagementServiceImpl implements RemotePaymentModulesManagementService {

    private static final String DEFAULT_SHOP_CODE = "DEFAULT";

    private final PaymentModulesManager paymentModulesManager;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    private final FederationFacade federationFacade;

    /**
     * Create remote payment gateway manager service.
     * @param paymentModulesManager service to use.
     * @param federationFacade federation facade
     */
    public RemotePaymentModulesManagementServiceImpl(final PaymentModulesManager paymentModulesManager,
                                                     final CustomerOrderPaymentService customerOrderPaymentService,
                                                     final FederationFacade federationFacade) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.federationFacade = federationFacade;
    }

    /** {@inheritDoc} */
    public List<DtoPaymentGatewayInfo> getPaymentGateways(final String lang) {

        List<DtoPaymentGatewayInfo> rez = new ArrayList<DtoPaymentGatewayInfo> ();
        List<Pair<String, String>> active = getAllowedPaymentGateways(lang);
        List<Pair<String, String>> available = getAvailablePaymentGateways(lang);
        for (Pair<String, String> pair : active) {
            rez.add(
                    new DtoPaymentGatewayInfoImpl(pair.getFirst(), pair.getSecond(), true)
            );
        }

        for (Pair<String, String> pair : available) {
            rez.add(
                    new DtoPaymentGatewayInfoImpl(pair.getFirst(), pair.getSecond(), false)
            );
        }

        Collections.sort(
                rez,
                new Comparator<DtoPaymentGatewayInfo>() {

                    /** {@inheritDoc} */
                    public int compare(DtoPaymentGatewayInfo o1, DtoPaymentGatewayInfo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
        );

        return rez;
    }

    /** {@inheritDoc} */
    public List<DtoPaymentGatewayInfo> getPaymentGatewaysForShop(final String lang,
                                                                 final String shopCode) {

        List<DtoPaymentGatewayInfo> rez = new ArrayList<DtoPaymentGatewayInfo> ();
        List<Pair<String, String>> active = getAllowedPaymentGatewaysForShop(lang, shopCode);
        List<Pair<String, String>> available = getAvailablePaymentGatewaysForShop(lang, shopCode);
        for (Pair<String, String> pair : active) {
            rez.add(
                    new DtoPaymentGatewayInfoImpl(pair.getFirst(), pair.getSecond(), true)
            );
        }

        for (Pair<String, String> pair : available) {
            rez.add(
                    new DtoPaymentGatewayInfoImpl(pair.getFirst(), pair.getSecond(), false)
            );
        }

        Collections.sort(
                rez,
                new Comparator<DtoPaymentGatewayInfo>() {

                    /** {@inheritDoc} */
                    public int compare(DtoPaymentGatewayInfo o1, DtoPaymentGatewayInfo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
        );

        return rez;
    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAllowedPaymentGateways(final String lang) {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, DEFAULT_SHOP_CODE);
        return fillPaymentDescriptors(descriptors, lang);
    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAllowedPaymentGatewaysForShop(final String lang,
                                                                       final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, shopCode);
            return fillPaymentDescriptors(descriptors, lang);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAvailablePaymentGateways(final String lang) {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, DEFAULT_SHOP_CODE);
        final List<Pair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
        rez.removeAll(getAllowedPaymentGateways(lang));
        return rez;
    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAvailablePaymentGatewaysForShop(final String lang,
                                                                         final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true, shopCode);
            final List<Pair<String, String>> rez = fillPaymentDescriptors(descriptors, lang);
            rez.removeAll(getAllowedPaymentGatewaysForShop(lang, shopCode));
            return rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    private List<Pair<String, String>> fillPaymentDescriptors(final List<PaymentGatewayDescriptor> descriptors,
                                                              final String lang) {
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(descriptors.size());
        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel(), DEFAULT_SHOP_CODE);
            final Pair<String, String> pairCandidate = new Pair<String, String>(
                    descr.getLabel(),
                    paymentGateway.getName(lang)
            );
            rez.add(pairCandidate);
        }
        return rez;
    }

    /** {@inheritDoc}*/
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters(final String gatewayLabel,
                                                                           final String lang) {
        return paymentModulesManager.getPaymentGateway(gatewayLabel, DEFAULT_SHOP_CODE).getPaymentGatewayParameters();
    }

    /** {@inheritDoc}*/
    public Collection<PaymentGatewayParameter> getPaymentGatewayParametersForShop(final String gatewayLabel,
                                                                                  final String lang,
                                                                                  final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            final List<PaymentGatewayParameter> shopOnly = new ArrayList<PaymentGatewayParameter>(paymentModulesManager.getPaymentGateway(gatewayLabel, shopCode).getPaymentGatewayParameters());
            final Iterator<PaymentGatewayParameter> shopOnlyIt = shopOnly.iterator();
            while (shopOnlyIt.hasNext()) {
                final PaymentGatewayParameter param = shopOnlyIt.next();
                if (!param.getLabel().startsWith("#" + shopCode + "_")) {
                    shopOnlyIt.remove();
                }
            }
            return shopOnly;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    final Map<String, PaymentGatewayFeature> paymentGatewayFeatureCache = new HashMap<String, PaymentGatewayFeature>();

    /** {@inheritDoc}*/
    public PaymentGatewayFeature getPaymentGatewayFeature(final String gatewayLabel) {
        PaymentGatewayFeature rez = paymentGatewayFeatureCache.get(gatewayLabel);
        if (rez == null) {
            rez =  paymentModulesManager.getPaymentGateway(gatewayLabel, DEFAULT_SHOP_CODE).getPaymentGatewayFeatures();
            paymentGatewayFeatureCache.put(gatewayLabel, rez) ;
        }
        return  rez;
    }

    /** {@inheritDoc}*/
    public PaymentGatewayFeature getPaymentGatewayFeatureForShop(final String gatewayLabel,
                                                                 final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            PaymentGatewayFeature rez = paymentGatewayFeatureCache.get(gatewayLabel);
            if (rez == null) {
                rez =  paymentModulesManager.getPaymentGateway(gatewayLabel, DEFAULT_SHOP_CODE).getPaymentGatewayFeatures();
                paymentGatewayFeatureCache.put(gatewayLabel, rez) ;
            }
            return  rez;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc}*/
    public boolean updateConfigurationParameter(final String gatewayLabel,
                                                final String parameterLabel,
                                                final String parameterValue) {
        final Collection<PaymentGatewayParameter> params = paymentModulesManager.getPaymentGateway(gatewayLabel, DEFAULT_SHOP_CODE).getPaymentGatewayParameters();
        for (PaymentGatewayParameter param : params ) {
            if (param.getLabel().equals(parameterLabel)) {
                param.setValue(parameterValue);
                paymentModulesManager.getPaymentGateway(gatewayLabel, DEFAULT_SHOP_CODE).updateParameter(param);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc}*/
    public boolean updateConfigurationParameterForShop(final String gatewayLabel,
                                                       final String parameterLabel,
                                                       final String parameterValue,
                                                       final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class) && parameterLabel.startsWith("#" + shopCode + "_")) {
            final Collection<PaymentGatewayParameter> params = paymentModulesManager.getPaymentGateway(gatewayLabel, shopCode).getPaymentGatewayParameters();
            for (PaymentGatewayParameter param : params ) {
                if (param.getLabel().equals(parameterLabel)) {
                    param.setValue(parameterValue);
                    paymentModulesManager.getPaymentGateway(gatewayLabel, shopCode).updateParameter(param);
                    return true;
                }
            }
            return false;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc}*/
    public void allowPaymentGateway(final String label) {
        paymentModulesManager.allowPaymentGateway(label);
    }

    /** {@inheritDoc}*/
    public void allowPaymentGatewayForShop(final String label,
                                           final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {

            final List<Pair<String, String>> listLabelAndName = getAllowedPaymentGatewaysForShop(label, shopCode);
            boolean enabled = false;
            for (final Pair<String, String> labelAndName : listLabelAndName) {
                if (labelAndName.getFirst().equals(label)) {
                    enabled = true;
                }
            }
            if (!enabled) {
                // When we enable PG need to copy over SHOP specific settings
                paymentModulesManager.allowPaymentGatewayForShop(label, shopCode);
                final Collection<PaymentGatewayParameter> defParams = getPaymentGatewayParameters(label, "en");
                final Collection<PaymentGatewayParameter> shopParams = getPaymentGatewayParametersForShop(label, "en", shopCode);
                final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(label, shopCode);
                for (final PaymentGatewayParameter defParam : defParams) {
                    boolean exists = false;
                    for (final PaymentGatewayParameter shopParam : shopParams) {
                        if (shopParam.getLabel().equals("#" + shopCode + "_"  + defParam.getLabel())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        final PaymentGatewayParameter parameter = new PaymentGatewayParameterEntity();
                        parameter.setPgLabel(defParam.getPgLabel());
                        parameter.setName(defParam.getName());
                        parameter.setLabel("#" + shopCode + "_" + defParam.getLabel());
                        parameter.setValue(defParam.getValue());
                        parameter.setDescription(defParam.getDescription());
                        parameter.setGuid(UUID.randomUUID().toString());
                        gateway.addParameter(parameter);
                    }
                }
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc}*/
    public void disallowPaymentGateway(final String label) {
        paymentModulesManager.disallowPaymentGateway(label);
    }

    /** {@inheritDoc}*/
    public void disallowPaymentGatewayForShop(final String label,
                                              final String shopCode) {
        if (federationFacade.isManageable(shopCode, ShopDTO.class)) {
            paymentModulesManager.disallowPaymentGatewayForShop(label, shopCode);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    /**
     * Find all payments by given parameters.
     * All parameters are optional, but at least one must be present. Please verify this fact on UI.
     *
     * @param orderNumber            given order number. optional
     * @param fromDate from date
     * @param tillDate till date
     * @param lastCardDigits last 4 digits of plastic card
     * @param cardHolderName card holder name
     * @param paymentGateway payment gateway
     * @return list of payments which satisfy search criteria
     */
    public List<CustomerOrderPayment> findPayments(final String orderNumber,
                                                   final Date fromDate,
                                                   final Date tillDate,
                                                   final String lastCardDigits,
                                                   final String cardHolderName,
                                                   final String paymentGateway) {

        final List<CustomerOrderPayment> payments = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                orderNumber,
                fromDate,
                tillDate,
                lastCardDigits,
                cardHolderName,
                paymentGateway));

        federationFacade.applyFederationFilter(payments, CustomerOrderPayment.class);
        return payments;

    }


}
