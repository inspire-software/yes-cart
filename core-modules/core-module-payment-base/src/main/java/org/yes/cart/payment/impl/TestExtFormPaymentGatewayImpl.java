/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.payment.impl;


import org.apache.commons.lang.SerializationUtils;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.util.HttpParamsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestExtFormPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayExternalForm {

    public static String ORDER_GUID_PARAM_KEY = "ext-order-guid";
    public static String AUTH_RESPONSE_CODE_PARAM_KEY = "ext-auth-response-code";
    public static String REFUND_RESPONSE_CODE_PARAM_KEY = "ext-refund-response-code";

    private static final PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, false,
            null,
            false, false
    );


    private static final Map<String, PaymentGatewayParameter> gatewayConfig = new HashMap<String, PaymentGatewayParameter>();


    /**
     * Getter for unit testing.
     *
     * @return gateway additional parameters (e.g. failure scenario simulation)
     */
    public static Map<String, PaymentGatewayParameter> getGatewayConfig() {
        return gatewayConfig;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "testExtFormPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        return "https://some.payment.gateway.domain.com/bender-pay.cgi";
    }

    /**
     * {@inheritDoc}
     */
    public String getSubmitButton(final String locale) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return HttpParamsUtils.getSingleValue(privateCallBackParameters.get(ORDER_GUID_PARAM_KEY));
    }

    /**
     * {@inheritDoc}
     */
    public CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult) {

        String responseCode = callbackResult.get(AUTH_RESPONSE_CODE_PARAM_KEY);
        if (responseCode == null) {
            responseCode = callbackResult.get(REFUND_RESPONSE_CODE_PARAM_KEY);
        }
        if ("1".equals(responseCode)) {
            return CallbackResult.OK;
        } else if ("2".equals(responseCode)) {
            return CallbackResult.UNSETTLED;
        } else if ("3".equals(responseCode)) {
            return CallbackResult.PROCESSING;
        } else if ("4".equals(responseCode)) {
            return CallbackResult.MANUAL_REQUIRED;
        } else {
            return CallbackResult.FAILED;
        }

    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final String operation, final Map privateCallBackParameters) {
        final Payment payment = new PaymentImpl();

        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());

        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);
        final CallbackResult res = getExternalCallbackResult(params);

        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());
        payment.setTransactionOperationResultCode(params.get(AUTH_RESPONSE_CODE_PARAM_KEY));
        payment.setTransactionOperationResultMessage(
                HttpParamsUtils.stringify("Params: ", params)
        );
        payment.setCardNumber("4111111111111111");
        payment.setShopperIpAddress(params.get(PaymentMiscParam.CLIENT_IP));

        return payment;
    }


    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {
        return (Payment) SerializationUtils.clone(paymentIn);
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());

        final String responseCode = gatewayConfig.containsKey(REFUND_RESPONSE_CODE_PARAM_KEY) ?
                gatewayConfig.get(REFUND_RESPONSE_CODE_PARAM_KEY).getValue() : Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED;

        final CallbackResult res = getExternalCallbackResult(new HashMap<String, String>() {{
            put(REFUND_RESPONSE_CODE_PARAM_KEY, responseCode);
        }});

        payment.setTransactionGatewayLabel(getLabel());
        payment.setTransactionOperationResultCode(responseCode);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

}
