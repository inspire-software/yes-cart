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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.util.HttpParamsUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestExtFormPaymentGatewayImpl extends AbstractPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(TestExtFormPaymentGatewayImpl.class);

    public static String ORDER_GUID_PARAM_KEY = "ext-order-guid";
    public static String AUTH_RESPONSE_CODE_PARAM_KEY = "ext-auth-response-code";
    public static String AUTH_RESPONSE_AMOUNT_PARAM_KEY = "ext-auth-response-amount";
    public static String REFUND_RESPONSE_CODE_PARAM_KEY = "ext-refund-response-code";
    public static String REFUND_RESPONSE_AMOUNT_PARAM_KEY = "ext-refund-response-amount";

    private static final PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, false,
            null,
            false, false
    );


    private static final Map<String, PaymentGatewayParameter> gatewayConfig = new HashMap<>();


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
    @Override
    public String getLabel() {
        return "testExtFormPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        return "https://some.payment.gateway.domain.com/bender-pay.cgi";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters) {
        CallbackOperation op = CallbackOperation.PAYMENT;
        String responseCode = HttpParamsUtils.getSingleValue(privateCallBackParameters.get(AUTH_RESPONSE_CODE_PARAM_KEY));
        String amountKey = AUTH_RESPONSE_AMOUNT_PARAM_KEY;
        if (responseCode == null) {
            responseCode = HttpParamsUtils.getSingleValue(privateCallBackParameters.get(REFUND_RESPONSE_CODE_PARAM_KEY));
            if (responseCode != null) {
                op = CallbackOperation.REFUND;
                amountKey = REFUND_RESPONSE_AMOUNT_PARAM_KEY;
            }
        }

        BigDecimal callbackAmount = null;
        try {
            callbackAmount = new BigDecimal(HttpParamsUtils.getSingleValue(privateCallBackParameters.get(amountKey)));
        } catch (Exception exp) {
            LOG.warn("Callback for did not have a valid amount {}", privateCallBackParameters.get(amountKey));
        }

        return new BasicCallbackInfoImpl(
                HttpParamsUtils.getSingleValue(privateCallBackParameters.get(ORDER_GUID_PARAM_KEY)),
                op,
                callbackAmount,
                privateCallBackParameters
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult) {

        String responseCode = callbackResult.get(AUTH_RESPONSE_CODE_PARAM_KEY);
        if (responseCode == null) {
            responseCode = callbackResult.get(REFUND_RESPONSE_CODE_PARAM_KEY);
        }
        if ("1".equals(responseCode)) {
            return CallbackAware.CallbackResult.OK;
        } else if ("2".equals(responseCode)) {
            return CallbackAware.CallbackResult.UNSETTLED;
        } else if ("3".equals(responseCode)) {
            return CallbackAware.CallbackResult.PROCESSING;
        } else if ("4".equals(responseCode)) {
            return CallbackAware.CallbackResult.MANUAL_REQUIRED;
        } else {
            return CallbackAware.CallbackResult.FAILED;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation, final Map privateCallBackParameters) {
        final Payment payment = new PaymentImpl();

        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());

        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);
        final CallbackAware.CallbackResult res = getExternalCallbackResult(params);

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
    @Override
    public Payment authorizeCapture(final Payment paymentIn) {
        return (Payment) SerializationUtils.clone(paymentIn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());

        payment.setTransactionGatewayLabel(getLabel());
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if (PaymentGateway.REFUND.equals(processorOperation)) {

            final String responseCode;
            final CallbackAware.CallbackResult res;

            if (callback != null) {
                responseCode = HttpParamsUtils.getSingleValue(callback.getParameters().get(REFUND_RESPONSE_CODE_PARAM_KEY));
                res = getExternalCallbackResult(HttpParamsUtils.createSingleValueMap(callback.getParameters()));
            } else {
                responseCode = gatewayConfig.containsKey(REFUND_RESPONSE_CODE_PARAM_KEY) ?
                        gatewayConfig.get(REFUND_RESPONSE_CODE_PARAM_KEY).getValue() : Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED;

                res = getExternalCallbackResult(new HashMap<String, String>() {{
                    put(REFUND_RESPONSE_CODE_PARAM_KEY, responseCode);
                }});
            }

            payment.setTransactionOperationResultCode(responseCode);
            payment.setPaymentProcessorResult(res.getStatus());

        }
        
    }
}
