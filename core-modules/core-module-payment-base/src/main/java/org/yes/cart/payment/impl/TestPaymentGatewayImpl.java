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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;

import java.util.*;

/**
 * This payment gateway used for testing purposes, and present in test environment only.
 * Not support persisted configuration.
 * 
 * <p/>
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayInternalForm {

    public static final String SUPPORTED_OPERATIONS = "SUPPORTED_OPERATIONS_";

    public static final String AUTH_FAIL = "AUTH_FAIL";
    public static final String AUTH_FAIL_NO = "AUTH_FAIL_SEQNO_"; //prefix to fail payment no
    public static final String REVERSE_AUTH_FAIL = "REVERSE_AUTH_FAIL";
    public static final String REVERSE_AUTH_FAIL_NO = "REVERSE_AUTH_FAIL_SEQNO_";
    public static final String CAPTURE_FAIL = "CAPTURE_FAIL";
    public static final String CAPTURE_FAIL_NO = "CAPTURE_FAIL_SEQNO_";
    public static final String AUTH_CAPTURE_FAIL = "AUTH_CAPTURE_FAIL";
    public static final String AUTH_CAPTURE_FAIL_NO = "AUTH_CAPTURE_FAIL_SEQNO_";
    public static final String VOID_CAPTURE_FAIL = "VOID_CAPTURE_FAIL";
    public static final String VOID_CAPTURE_FAIL_NO = "VOID_CAPTURE_FAIL_SEQNO_";
    public static final String REFUND_FAIL = "REFUND_FAIL";
    public static final String REFUND_FAIL_NO = "REFUND_FAIL_SEQNO_";
    public static final String PROCESSING = "PROCESSING";
    public static final String PROCESSING_NO = "PROCESSING_SEQNO_";
    public static final String UNSETTLED = "UNSETTLED";
    public static final String UNSETTLED_NO = "UNSETTLED_SEQNO_";


    private static final Set<String> ADDITIONAL_CONFIG = new HashSet<String>(Arrays.asList(

            AUTH_FAIL, REVERSE_AUTH_FAIL, CAPTURE_FAIL, AUTH_CAPTURE_FAIL, VOID_CAPTURE_FAIL, REFUND_FAIL,
            PROCESSING, UNSETTLED,

            SUPPORTED_OPERATIONS + "SupportAuthorizePerShipment", SUPPORTED_OPERATIONS + "SupportAuthorize",
            SUPPORTED_OPERATIONS + "SupportCapture", SUPPORTED_OPERATIONS + "SupportAuthorizeCapture",
            SUPPORTED_OPERATIONS + "SupportVoid", SUPPORTED_OPERATIONS + "SupportReverseAuthorization",
            SUPPORTED_OPERATIONS + "SupportRefund", SUPPORTED_OPERATIONS + "SupportCaptureMore",
            SUPPORTED_OPERATIONS + "SupportCaptureLess"

    ));

    private static int authNum;

    private final PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, true, true, true,
            true, true, true, false,
            true, false,
            null,
            true, true
    ) {

        @Override
        public boolean isSupportAuthorizePerShipment() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizePerShipment")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizePerShipment"));
            }
            return super.isSupportAuthorizePerShipment();
        }

        @Override
        public boolean isSupportAuthorize() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorize")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorize"));
            }
            return super.isSupportAuthorize();
        }

        @Override
        public boolean isSupportCapture() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportCapture")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportCapture"));
            }
            return super.isSupportCapture();
        }

        @Override
        public boolean isSupportAuthorizeCapture() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizeCapture")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizeCapture"));
            }
            return super.isSupportAuthorizeCapture();
        }

        @Override
        public boolean isSupportVoid() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizeCapture")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportAuthorizeCapture"));
            }
            return isParameterActivated(SUPPORTED_OPERATIONS + "SupportVoid") || super.isSupportVoid();
        }

        @Override
        public boolean isSupportReverseAuthorization() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportReverseAuthorization")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportReverseAuthorization"));
            }
            return super.isSupportReverseAuthorization();
        }

        @Override
        public boolean isSupportRefund() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportRefund")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportRefund"));
            }
            return super.isSupportRefund();
        }

        @Override
        public boolean isSupportCaptureMore() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportCaptureMore")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportCaptureMore"));
            }
            return super.isSupportCaptureMore();
        }

        @Override
        public boolean isSupportCaptureLess() {
            if (isParameterActivated(SUPPORTED_OPERATIONS + "SupportCaptureLess")) {
                return Boolean.valueOf(getParameterActivated(SUPPORTED_OPERATIONS + "SupportCaptureLess"));
            }
            return super.isSupportCaptureLess();
        }

    };


    private static final Map<String, PaymentGatewayParameter> gatewayConfig = new HashMap<String, PaymentGatewayParameter>();


    /**
     * Getter for unit testing.
     *
     * @return gateway additional parameters (e.g. failure scenario simulation)
     */
    public static Map<String, PaymentGatewayParameter> getGatewayConfig() {
        return gatewayConfig;
    }


    private boolean isParameterActivated(final String parameter) {
        // Need to go over all DB and static since we want to use this PG for manual testing too
        // and storefront and Admin could be in different VMs
        for (final PaymentGatewayParameter param : getPaymentGatewayParameters()) {
            if (parameter.equals(param.getLabel())) {
                return StringUtils.isNotBlank(param.getValue()) && !"DEACTIVATED".equals(param.getValue());
            }
        }

        return false;
    }

    private String getParameterActivated(final String parameter) {
        // Need to go over all DB and static since we want to use this PG for manual testing too
        // and storefront and Admin could be in different VMs
        for (final PaymentGatewayParameter param : getPaymentGatewayParameters()) {
            if (parameter.equals(param.getLabel())) {
                return param.getValue();
            }
        }

        return "";
    }


    /**
     * Get number of auth operation
     * @return number of auth operation
     */
    public static int getAuthNum() {
        return authNum;
    }

    /**
     * Set number of auth operation
     * @param authNum number of auth operation
     */
    public static void setAuthNum(final int authNum) {
        TestPaymentGatewayImpl.authNum = authNum;
    }



    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "testPaymentGateway";
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
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        List<PaymentGatewayParameter> all = new ArrayList<PaymentGatewayParameter>(super.getPaymentGatewayParameters());

        final Map<String, Integer> existing = new HashMap<String, Integer>();
        int counter = 0;
        for (final PaymentGatewayParameter pgp : all) {
            existing.put(pgp.getLabel(), counter++);
        }

        final Set<String> extra = new HashSet<String>(ADDITIONAL_CONFIG);
        for (final Map.Entry<String, PaymentGatewayParameter> entry : gatewayConfig.entrySet()) {
            if (extra.contains(entry.getKey())) {
                extra.remove(entry.getKey());
            }
            all.add(entry.getValue());
        }
        for (final String name : extra) {
            final Integer index = existing.get(name);
            if (index != null) {
                continue;
            }
            final PaymentGatewayParameterEntity entity = new PaymentGatewayParameterEntity();
            entity.setLabel(name);
            entity.setName(name);
            entity.setPgLabel(getLabel());
            entity.setDescription("Add 'true' or 'false' value to activate this flag or any values for FAIL flags, set to 'DEACTIVATED' to deactivate");
            all.add(entity);
        }
        return all;

    }

    /**
     * {@inheritDoc}
     */
    public void deleteParameter(final String label) {
        if (gatewayConfig.containsKey(label)) {
            gatewayConfig.remove(label);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        if (StringUtils.isNotBlank(paymentGatewayParameter.getValue())) {
            gatewayConfig.put(paymentGatewayParameter.getLabel(), paymentGatewayParameter);
        } else {
            deleteParameter(paymentGatewayParameter.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {

        if (ADDITIONAL_CONFIG.contains(paymentGatewayParameter.getLabel())
                || paymentGatewayParameter.getLabel().contains("_SEQNO_")) {
            // extra runtime settings
            if (StringUtils.isBlank(paymentGatewayParameter.getValue())) {
                // delete from runtime
                deleteParameter(paymentGatewayParameter.getLabel());
                // delete from db
                super.deleteParameter(paymentGatewayParameter.getLabel());
            } else if (paymentGatewayParameter.getPaymentGatewayParameterId() > 0L) {
                // update db with new value (no need for runtime update)
                super.updateParameter(paymentGatewayParameter);
            } else {
                // add new value to db (no need for runtime update)
                super.addParameter(paymentGatewayParameter);
            }
        } else {
            // normal flow
            super.updateParameter(paymentGatewayParameter);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        if (isParameterActivated(AUTH_FAIL) || isParameterActivated(AUTH_FAIL_NO + getAuthNum()) || isParameterActivated(AUTH_FAIL_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Authorize failed for " + getAuthNum() + "@" + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);
        } else if (isParameterActivated(PROCESSING) || isParameterActivated(PROCESSING_NO + getAuthNum()) || isParameterActivated(PROCESSING_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
            payment.setTransactionOperationResultCode("EXTERNAL_OK");
            payment.setTransactionOperationResultMessage("Authorize processing for " + getAuthNum() + "@" + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_PROCESSING);
            payment.setPaymentProcessorBatchSettlement(false);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
            payment.setTransactionOperationResultCode("EXTERNAL_OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            payment.setPaymentProcessorBatchSettlement(false);
        }
        authNum ++;

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH_CAPTURE);
        if (isParameterActivated(AUTH_CAPTURE_FAIL) || isParameterActivated(AUTH_CAPTURE_FAIL_NO + payment.getPaymentAmount().toPlainString())) {

            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Authorize-capture failed for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);

        } else if (isParameterActivated(PROCESSING) || isParameterActivated(PROCESSING_NO + payment.getPaymentAmount().toPlainString())) {

            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
            payment.setTransactionOperationResultCode("EXTERNAL_OK");
            payment.setTransactionOperationResultMessage("Authorize-capture processing for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_PROCESSING);
            payment.setPaymentProcessorBatchSettlement(false);

        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());


            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");

            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

            payment.setPaymentProcessorBatchSettlement(!isParameterActivated(UNSETTLED) && !isParameterActivated(UNSETTLED_NO + payment.getPaymentAmount().toPlainString()));
        }
        return payment;

    }


    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        if (isParameterActivated(REVERSE_AUTH_FAIL) || isParameterActivated(REVERSE_AUTH_FAIL_NO + payment.getPaymentAmount().toPlainString())) {

            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Reverse authorize for "  + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);

        } else {

            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("REVERSE_AUTH_OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            payment.setPaymentProcessorBatchSettlement(false);

        }

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        if (isParameterActivated(CAPTURE_FAIL) || isParameterActivated(CAPTURE_FAIL_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Capture for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);
        } else if (isParameterActivated(PROCESSING) || isParameterActivated(PROCESSING_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
            payment.setTransactionOperationResultCode("EXTERNAL_OK");
            payment.setTransactionOperationResultMessage("Capture processing for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_PROCESSING);
            payment.setPaymentProcessorBatchSettlement(false);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            payment.setPaymentProcessorBatchSettlement(!isParameterActivated(UNSETTLED) && !isParameterActivated(UNSETTLED_NO + payment.getPaymentAmount().toPlainString()));
        }
        return payment;

    }


    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        if (isParameterActivated(VOID_CAPTURE_FAIL) || isParameterActivated(VOID_CAPTURE_FAIL_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Void Capture for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            payment.setPaymentProcessorBatchSettlement(false);
        }
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        if (isParameterActivated(REFUND_FAIL) || isParameterActivated(REFUND_FAIL_NO + payment.getPaymentAmount().toPlainString())) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Refund for " + payment.getPaymentAmount().toPlainString());
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setPaymentProcessorBatchSettlement(false);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            payment.setPaymentProcessorBatchSettlement(false);
        }
        return payment;
    }


}
