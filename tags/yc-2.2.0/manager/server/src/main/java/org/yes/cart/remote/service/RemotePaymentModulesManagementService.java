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
package org.yes.cart.remote.service;

import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
public interface RemotePaymentModulesManagementService {

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<DtoPaymentGatewayInfo> getPaymentGateways(String lang);

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<DtoPaymentGatewayInfo> getPaymentGatewaysForShop(String lang, String shopCode);

    /**
     * Get allowed payment gateways in all modules which are system enabled.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<DtoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(String lang);

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<Pair<String, String>> getAllowedPaymentGateways(String lang);

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<Pair<String, String>> getAllowedPaymentGatewaysForShop(String lang, String shopCode);

    /**
     * Get available payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<Pair<String, String>> getAvailablePaymentGateways(String lang);

    /**
     * Get available payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<Pair<String, String>> getAvailablePaymentGatewaysForShop(String lang, String shopCode);

    /**
     * Get parameters for given payment gateway.
     *
     * @param gatewayLabel payment gateway label.
     * @param lang ui lang
     *
     * @return PG parameters.
     */
    Collection<PaymentGatewayParameter> getPaymentGatewayParameters(String gatewayLabel, String lang);

    /**
     * Get parameters for given payment gateway.
     *
     * @param gatewayLabel payment gateway label.
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return PG parameters.
     */
    Collection<PaymentGatewayParameter> getPaymentGatewayParametersForShop(String gatewayLabel, String lang, String shopCode);


    /**
     * Get payment gateway features.
     *
     * @param gatewayLabel payment gateway label.
     *
     * @return {@link PaymentGatewayFeature}  supported features of gateway.
     */
    PaymentGatewayFeature getPaymentGatewayFeature(String gatewayLabel);


    /**
     * Get payment gateway features.
     *
     * @param gatewayLabel payment gateway label.
     * @param shopCode shop code
     *
     * @return {@link PaymentGatewayFeature}  supported features of gateway.
     */
    PaymentGatewayFeature getPaymentGatewayFeatureForShop(String gatewayLabel, String shopCode);


    /**
     * Update configuration parameter of payment gateway.
     *
     * @param gatewayLabel payment gateway label.
     * @param parameterLabel parameter label, unique identification.
     * @param parameterValue parameter value.
     *
     * @return  true in case if update was ok
     */
    boolean updateConfigurationParameter(String gatewayLabel, String parameterLabel, String parameterValue);


    /**
     * Update configuration parameter of payment gateway.
     *
     * @param gatewayLabel payment gateway label.
     * @param parameterLabel parameter label, unique identification.
     * @param parameterValue parameter value.
     * @param shopCode shop code
     *
     * @return  true in case if update was ok
     */
    boolean updateConfigurationParameterForShop(String gatewayLabel, String parameterLabel, String parameterValue, String shopCode);

    /**
     * Put available payment gateway into allowed.
     *
     * @param label   payment gateway label
     */
    void allowPaymentGateway(String label);

    /**
     * Put available payment gateway into allowed.
     *
     * @param label   payment gateway label
     * @param shopCode shop code
     */
    void allowPaymentGatewayForShop(String label, String shopCode);

    /**
     * Remove available payment gateway from allowed and put it into allowed.
     *
     * @param label   payment gateway label
     */
    void disallowPaymentGateway(String label);

    /**
     * Remove available payment gateway from allowed and put it into allowed.
     *
     * @param label   payment gateway label
     * @param shopCode shop code
     */
    void disallowPaymentGatewayForShop(String label, String shopCode);


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
     *
     * @return list of payments which satisfy search criteria
     */
    List<CustomerOrderPayment> findPayments(String orderNumber,
                                            Date fromDate,
                                            Date tillDate,
                                            String lastCardDigits,
                                            String cardHolderName,
                                            String paymentGateway);



}
