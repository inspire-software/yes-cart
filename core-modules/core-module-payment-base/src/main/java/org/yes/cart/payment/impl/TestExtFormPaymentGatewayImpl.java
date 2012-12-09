package org.yes.cart.payment.impl;


import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestExtFormPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayExternalForm {

    public static String ORDER_GUID_PARAM_KEY = "do-a-barrel-roll";
    public static String RESPONSE_CODE_PARAM_KEY = "do-a-barrel-roll-response-code";

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
        return new PaymentGatewayFeatureImpl(
                false, false, false, true,
                false, false, false, true,
                true,  false,
                null,
                false, false
        );
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
    public String getSubmitButton() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return (String) privateCallBackParameters.get(ORDER_GUID_PARAM_KEY);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> setExpressCheckoutMethod(final BigDecimal amount, final String currencyCode) throws IOException {
        return Collections.EMPTY_MAP;
    }


     /**
     * {@inheritDoc}
     */
    public Map<String, String> doDoExpressCheckoutPayment(final String token, final String payerId,
                                                          final BigDecimal amount, final String currencyCode) throws IOException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getExpressCheckoutDetails(final String token) throws IOException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccess(final Map<String, String> nvpCallResult) {
        return true;
    }

    /** {@inheritDoc} */
    public void handleNotification(final HttpServletRequest request, final HttpServletResponse response) {
        //nothing to do
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


        String responseCode = (String) privateCallBackParameters.get(RESPONSE_CODE_PARAM_KEY);
        if ("1".equalsIgnoreCase(responseCode)) {
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


    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment payment) {
        return payment;
    }

}
