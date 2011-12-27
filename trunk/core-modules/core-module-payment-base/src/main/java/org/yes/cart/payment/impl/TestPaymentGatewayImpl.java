package org.yes.cart.payment.impl;


import java.util.*;

import org.apache.commons.lang.SerializationUtils;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.exception.PaymentException;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

/**
 * This payment gateway used for testing purposes, and present in test environment only.
 * Not supoprt persisted configuration.
 * 
 * <p/>
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayInternalForm {

    public static final String AUTH_FAIL = "AUTH_FAIL";
    public static final String AUTH_FAIL_NO = "AUTH_FAIL_NO"; //prefix to fail payment no
    public static final String REVERSE_AUTH_FAIL = "REVERSE_AUTH_FAIL";
    public static final String CAPTURE_FAIL = "CAPTURE_FAIL";
    public static final String AUTH_CAPTURE_FAIL = "AUTH_CAPTURE_FAIL";
    public static final String VOID_CAPTURE_FAIL = "VOID_CAPTURE_FAIL";
    public static final String REFUND_FAIL = "REFUND_FAIL";

    public static final String AUTH_EXCEPTION = "AUTH_EXCEPTION";
    public static final String AUTH_EXCEPTION_NO = "AUTH_EXCEPTION_NO"; //prefix to expection on payment no
    public static final String REVERSE_AUTH_EXCEPTION = "REVERSE_AUTH_EXCEPTION";
    public static final String CAPTURE_EXCEPTION = "CAPTURE_EXCEPTION";
    public static final String AUTH_CAPTURE_EXCEPTION = "AUTH_CAPTURE_EXCEPTION";
    public static final String VOID_CAPTURE_EXCEPTION = "VOID_CAPTURE_EXCEPTION";
    public static final String REFUND_EXCEPTION = "REFUND_EXCEPTION";


    private static int authNum;
    private PaymentGatewayFeature paymentGatewayFeature;




    private static final Map<String, PaymentGatewayParameter> gatewayConfig =
            new HashMap<String, PaymentGatewayParameter>();




    public static Map<String, PaymentGatewayParameter> getGatewayConfig() {
        return gatewayConfig;
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
    public synchronized PaymentGatewayFeature getPaymentGatewayFeatures() {

        if (paymentGatewayFeature == null) {
            paymentGatewayFeature = new PaymentGatewayFeatureImpl(
                    true, true, true, true,
                    true, true, true, false,
                    true, false,
                    null
            );
        }

        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        Collection<PaymentGatewayParameter> fromDatabase = super.getPaymentGatewayParameters();
        fromDatabase.addAll(gatewayConfig.values());
        return fromDatabase;
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
        gatewayConfig.put(paymentGatewayParameter.getLabel(), paymentGatewayParameter);
    }

    /**
     * {@inheritDoc}
     */
    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        gatewayConfig.put(paymentGatewayParameter.getLabel(), paymentGatewayParameter);
    }


    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        if (gatewayConfig.get(AUTH_EXCEPTION) != null) {
            throw new PaymentException("Some exception reason. Auth");
        } else if (gatewayConfig.get(AUTH_EXCEPTION_NO + getAuthNum()) != null) {
            throw new PaymentException("Some exception reason. Auth no " + getAuthNum() + " must throw exception");
        } else if (gatewayConfig.get(AUTH_FAIL_NO + getAuthNum()) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Authorize failed for " + getAuthNum() +" number ");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else if (gatewayConfig.get(AUTH_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Authorize");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
            payment.setTransactionOperationResultCode("EXTERNAL_OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
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
        if (gatewayConfig.get(AUTH_CAPTURE_EXCEPTION) != null) {
            throw new PaymentException("Payment exception during authorize capture");
        } else  if (gatewayConfig.get(AUTH_CAPTURE_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Authorize capture");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);

        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());


            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");

            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

        }
        return payment;

    }


    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        if (gatewayConfig.get(REVERSE_AUTH_EXCEPTION) != null) {
            throw new PaymentException("Some exception reason. Revese auth");
        } else  if (gatewayConfig.get(REVERSE_AUTH_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Revese authorize");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("REVERSE_AUTH_OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        }

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        if (gatewayConfig.get(CAPTURE_EXCEPTION) != null) {
            throw new PaymentException("Exception during capture");
        } else if (gatewayConfig.get(CAPTURE_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Capture ");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        }
        return payment;

    }


    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        if (gatewayConfig.get(VOID_CAPTURE_EXCEPTION) != null) {
            throw new PaymentException("Card rejected exception during void capture");
        } else   if (gatewayConfig.get(VOID_CAPTURE_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Void Capture ");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        }
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        if (gatewayConfig.get(REFUND_EXCEPTION) != null) {
            throw new PaymentException("Card rejected exception during refund operation. Everybody are greedy ");
        } else   if (gatewayConfig.get(REFUND_FAIL) != null) {
            payment.setTransactionOperationResultCode("EXTERNAL_ERROR_CODE");
            payment.setTransactionOperationResultMessage("Card rejected exception. Refund ");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        } else {
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionGatewayLabel(getLabel());
            payment.setTransactionOperationResultCode("OK");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        }
        return payment;
    }


}
