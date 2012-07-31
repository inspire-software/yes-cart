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

import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;
import org.yes.cart.domain.dto.impl.DtoPaymentGatewayInfoImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.remote.service.RemotePaymentModulesManagementService;
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


    private final PaymentModulesManager paymentModulesManager;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    /**
     * Create remote payment gateway manager service.
     * @param paymentModulesManager service to use.
     */
    public RemotePaymentModulesManagementServiceImpl(
            final PaymentModulesManager paymentModulesManager,
            final CustomerOrderPaymentService customerOrderPaymentService) {
        this.paymentModulesManager = paymentModulesManager;
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /** {@inheritDoc} */
    public List<DtoPaymentGatewayInfo> getPaymentGateways(final String lang) {
        List<DtoPaymentGatewayInfo> rez = new ArrayList<DtoPaymentGatewayInfo> ();
        List<Pair<String, String>> active = getAllowedPaymentGateways(lang);
        List<Pair<String, String>> awailable = getAvailablePaymentGateways(lang);
        for (Pair<String, String> pair : active) {
            rez.add(
                    new DtoPaymentGatewayInfoImpl(pair.getFirst(), pair.getSecond(), true)
            );
        }

        for (Pair<String, String> pair : awailable) {
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
        
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false);
        return fillPaymentDescriptors(descriptors);

    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAvailablePaymentGateways(final String lang) {
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true);
        final List<Pair<String, String>> rez = fillPaymentDescriptors(descriptors);
        rez.removeAll(getAllowedPaymentGateways(lang));
        return rez;
    }

    private List<Pair<String, String>> fillPaymentDescriptors(List<PaymentGatewayDescriptor> descriptors) {
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(descriptors.size());
        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel());
            final Pair<String, String> pairCandidate = new Pair<String, String>(
                    descr.getLabel(),
                    descr.getName()
            );
            rez.add(pairCandidate);
        }
        return rez;
    }

    /** {@inheritDoc}*/
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters(final String gatewayLabel, final String lang) {
        return paymentModulesManager.getPaymentGateway(gatewayLabel).getPaymentGatewayParameters();
    }

    /** {@inheritDoc}*/
    public boolean updateConfigurationParameter(final String gatewayLabel, final String paramaterLabel, final String parameterValue) {
        final Collection<PaymentGatewayParameter> params = paymentModulesManager.getPaymentGateway(gatewayLabel).getPaymentGatewayParameters();
        for (PaymentGatewayParameter param : params ) {
            if (param.getLabel().equals(paramaterLabel)) {
                param.setValue(parameterValue);
                paymentModulesManager.getPaymentGateway(gatewayLabel).updateParameter(param);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc}*/
    public void allowPaymentGateway(final String label) {
        paymentModulesManager.allowPaymentGateway(label);
    }

    /** {@inheritDoc}*/
    public void disallowPaymentGateway(final String label) {
        paymentModulesManager.disallowPaymentGateway(label);
    }


    /**
     * Find all payments by given parameters.
     * All parameters are optional, but at leasn one must be present. Please verify this fact on UI.
     *
     * @param orderNumber            given order number. optional
     * @param fromDate from date
     * @param tillDate till date
     * @param lastCardDigits last 4 digits of plastic card
     * @param cardHolderName card holder name
     * @param paymentGateway payment gateway
     * @return list of payments which satisfy search criteria
     */
    public List<CustomerOrderPayment> findPayments(
            final String orderNumber,
            final Date fromDate,
            final Date tillDate,
            final String lastCardDigits,
            final String cardHolderName,
            final String paymentGateway
    ) {

        return customerOrderPaymentService.findBy(
                orderNumber,
                fromDate,
                tillDate,
                lastCardDigits,
                cardHolderName,
                paymentGateway);

    }


}
