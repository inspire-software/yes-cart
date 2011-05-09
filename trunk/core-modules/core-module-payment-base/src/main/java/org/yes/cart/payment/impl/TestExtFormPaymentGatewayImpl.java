package org.yes.cart.payment.impl;


import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import java.util.Map;
import java.util.UUID;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestExtFormPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayExternalForm {

    public static String ORDER_GUID_PARAM_KEY = "do-a-barrel-roll";
    public static String RESPONCE_CODE_PARAM_KEY = "do-a-barrel-roll-response-code";

    /** {@inheritDoc} */
    public String getLabel() {
        return "testExtFormPaymentGateway";
    }

    /** {@inheritDoc} */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return new PaymentGatewayFeatureImpl(
                    false, false, false, true,
                    false, false, false, true,
                    true,
                    null
            );
    }

    /** {@inheritDoc} */
    public String getPostActionUrl() {
        return "https://some.payment.gateway.domain.com/bender-pay.cgi";
    }

    /** {@inheritDoc} */
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return (String) privateCallBackParameters.get(ORDER_GUID_PARAM_KEY);
    }


    /**
     * Process public call back request from payment gateway.
     *
     * @param privateCallBackParameters get/post parameters
     * @return true in case in payment was ok, false in case if payment failed
     */
    public Payment createPaymentPrototype(final Map privateCallBackParameters) {
        final Payment payment = new PaymentImpl();

        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());



        String responseCode = (String)privateCallBackParameters.get(RESPONCE_CODE_PARAM_KEY);
        if("1".equalsIgnoreCase(responseCode)) {
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        } else {
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        }
        payment.setTransactionOperationResultCode(responseCode);
        payment.setTransactionOperationResultMessage(
                "Everything is ok, Boss"
        );
        payment.setCardNumber("4111111111111111");

        return payment;
    }




    /** {@inheritDoc} */
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /** {@inheritDoc} */
    public Payment authorize(final Payment payment) {
        return payment;
    }

    /** {@inheritDoc} */
    public Payment reverseAuthorization(final Payment payment) {
        return payment;
    }

    /** {@inheritDoc} */
    public Payment capture(final Payment payment) {
        return payment;
    }

    /** {@inheritDoc} */
    public Payment voidCapture(final Payment payment) {
        return payment;
    }

    /** {@inheritDoc} */
    public Payment refund(final Payment payment) {
        return payment;
    }

}
