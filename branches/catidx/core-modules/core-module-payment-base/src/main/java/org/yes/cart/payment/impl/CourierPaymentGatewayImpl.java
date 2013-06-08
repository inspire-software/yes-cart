package org.yes.cart.payment.impl;

import org.apache.commons.lang.SerializationUtils;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;

import java.util.UUID;

/**
 * Courier offline payment gateway.
 *
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CourierPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayInternalForm {


    private PaymentGatewayFeature paymentGatewayFeature;




    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "courierPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        if (paymentGatewayFeature == null) {
            paymentGatewayFeature = new PaymentGatewayFeatureImpl(
                    true, true, true, true,
                    true, true, true, false,
                    false, false,
                    null,
                    true, true
            );
        }

        return paymentGatewayFeature;
    }


    /**
     * {@inheritDoc}
     */
    public Payment authorize(Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(Payment paymentIn) {
       final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        return payment;
    }
}
