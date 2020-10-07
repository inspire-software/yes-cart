/*
 * Copyright 2009 Inspire-Software.com
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.service.payment.PaymentLocaleTranslator;
import org.yes.cart.service.payment.impl.PaymentLocaleTranslatorImpl;
import org.yes.cart.utils.HttpParamsUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * User: inspiresoftware
 * Date: 07/10/2020
 * Time: 11:22
 */
public class PaySeraCheckoutPaymentGatewayImpl  extends AbstractPaySeraPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(PaySeraCheckoutPaymentGatewayImpl.class);

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, true,
            null,
            false, false
    );

    // form post acton url
    static final String PSC_POSTURL = "PSC_POSTURL";

    // Configuration parameters
    static final String PSC_ENVIRONMENT = "PSC_ENVIRONMENT";
    static final String PSC_PROJECTID = "PSC_PROJECTID";
    static final String PSC_SIGN_PASSWORD = "PSC_SIGN_PASSWORD";
    static final String PSC_API_VERSION = "PSC_API_VERSION";
    static final String PSC_BUYBUTTON = "PSC_BUYBUTTON";
    static final String PSC_CALLBACKURL = "PSC_CALLBACKURL";
    static final String PSC_ACCEPTURL = "PSC_ACCEPTURL";
    static final String PSC_CANCELURL = "PSC_CANCELURL";

    private final PaymentLocaleTranslator paymentLocaleTranslator = new PaymentLocaleTranslatorImpl();


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        return getParameterValue(PSC_POSTURL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {

        String submit = getParameterValue(PSC_BUYBUTTON + "_" + locale);
        if (submit == null) {
            submit = getParameterValue(PSC_BUYBUTTON);
        }
        if (StringUtils.isNotBlank(submit)) {
            return submit;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters, final boolean forceProcessing) {

        final Map<String, String> data = Collections.emptyMap(); // TODO: decode privateCallBackParameters data parameter

        final boolean valid = false; // TODO: ss1 validation of the parameters privateCallBackParameters

        if (valid || forceProcessing) {
            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }
            final String orderNumber = data.get("orderid");
            final String paymentStatus = data.get("status");
            final boolean refund = false; // TODO: does PaySera support refund notifications? If so then calculate here, if not then remove all code around refunds
            BigDecimal callbackAmount = null;
            try {
                callbackAmount = new BigDecimal(data.get("amount")).movePointRight(2); // Amount in cents
            } catch (Exception exp) {
                LOG.error("Callback for {} did not have a valid amount {}", orderNumber, data.get("amount"));
            }

            return new BasicCallbackInfoImpl(
                    orderNumber,
                    refund ? CallbackOperation.REFUND : CallbackOperation.PAYMENT,
                    callbackAmount, privateCallBackParameters, valid
            );
        } else {
            LOG.debug("Signature is not valid");
        }
        return new BasicCallbackInfoImpl(
                null,
                CallbackOperation.INVALID,
                null, privateCallBackParameters, valid
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

        final Map<String, String[]> request = HttpParamsUtils.createArrayValueMap(callbackResult);

        final Map<String, String> data = null; // TODO: decode privateCallBackParameters data parameter

        final boolean valid = false; // TODO: ss1 validation of the parameters privateCallBackParameters

        final String paymentStatus = data.get("status");

        if (valid || forceProcessing) {

            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }

            // TODO: write logic to detect payment status correctly
            /* As per documentation:
            Payment status:
                0 - payment has not been executed
                      TODO: find out how this works
                1 - payment successful
                      TODO: potentially return CallbackAware.CallbackResult.OK
                2 - payment order accepted, but not yet executed (this status does not guarantee execution of the payment)
                      TODO: potentially return CallbackAware.CallbackResult.UNSETTLED
                3 - additional payment information
                      TODO: find out how this works
             */

            final boolean settled = false; // TODO: populate right value depending on the status

            if (settled) {
                LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.OK);
                return CallbackAware.CallbackResult.OK;
            }
            LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.UNSETTLED);
            return CallbackAware.CallbackResult.UNSETTLED;
        } else {
            LOG.debug("Signature is not valid");
        }
        LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.FAILED);
        return CallbackAware.CallbackResult.FAILED;

    }


    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

        // TODO: preprocess hook, maybe usefull for paymentstatus=3 (additional payment information) messages


        if (PaymentGateway.REFUND_NOTIFY.equals(processorOperation) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            // TODO: remap parameters or pre-process payment if necessary, see PayPalButton impl
        }


    }

    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if ((PaymentGateway.REFUND.equals(processorOperation) || PaymentGateway.VOID_CAPTURE.equals(processorOperation)) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            // TODO: remap parameters or post-process payment if necessary, see PayPalButton impl

            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

        }

    }

    private void setParameterIfNotNull(final Map<String, String> params, final String key, final String valueKey) {
        final String value = getParameterValue(valueKey);
        if (StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorizeCapture(final Payment payment, final boolean forceProcessing) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorize(final Payment paymentIn, final boolean forceProcessing) {
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
    public Payment reverseAuthorization(final Payment paymentIn, final boolean forceProcessing) {
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
    public Payment capture(final Payment paymentIn, final boolean forceProcessing) {
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
    public Payment voidCapture(final Payment paymentIn, final boolean forceProcessing) {
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
    public Payment refund(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
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
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount, final String currencyCode, final String orderReference, final Payment payment) {

        final StringBuilder form = new StringBuilder();

        form.append(getHiddenFieldParam("projectid", PSC_PROJECTID));
        form.append(getHiddenFieldParam("version", PSC_API_VERSION));

        /*
         * The parameter, which allows to test the connection. The payment is not executed, but the result is returned
         * immediately, as if the payment has been made. To test, it is necessary to activate the mode for a particular
         * project by logging in and selecting: "Manage projects" -> "Payment gateway" (for a specific project) ->
         * "Allow test payments" (check).
         */
        final String value = getParameterValue(PSC_ENVIRONMENT);
        if (!"live".equalsIgnoreCase(value)) {
            form.append(getHiddenFieldValue("test", "1"));
        }

        form.append(getHiddenFieldParam("callbackurl", PSC_CALLBACKURL));
        form.append(getHiddenFieldParam("accepturl", PSC_ACCEPTURL));
        form.append(getHiddenFieldParam("cancelurl", PSC_CANCELURL));

        form.append(getHiddenFieldValue("currency", currencyCode));
        form.append(getHiddenFieldValue("orderid", orderReference));
        form.append(getHiddenFieldValue("lang", paymentLocaleTranslator.translateLocale(this, locale)));

        if (payment.getBillingAddress() != null) {
            form.append(getHiddenFieldValue("p_firstname", payment.getBillingAddress().getFirstname()));
            form.append(getHiddenFieldValue("p_lastname", payment.getBillingAddress().getLastname()));
            form.append(getHiddenFieldValue("p_email", payment.getBillingEmail()));
        }
        if (payment.getShippingAddress() != null) {
            form.append(getHiddenFieldValue("p_street", StringUtils.join(Arrays.asList(
                    payment.getShippingAddress().getAddrline1(),
                    payment.getShippingAddress().getAddrline2()), ' ')));
            form.append(getHiddenFieldValue("p_city", payment.getShippingAddress().getCity()));
            form.append(getHiddenFieldValue("p_countrycode", payment.getShippingAddress().getCountryCode()));
            form.append(getHiddenFieldValue("p_state", payment.getShippingAddress().getStateCode()));
            form.append(getHiddenFieldValue("p_zip", payment.getShippingAddress().getPostcode()));
        }

        final StringBuilder paytext = new StringBuilder();
        for (final PaymentLine item : payment.getOrderItems()) {
            // TODO: assemble paytext from items
        }
        form.append(getHiddenFieldValue("paytext", paytext.length() < 255 ? paytext.toString() : paytext.substring(0, 255)));

        LOG.debug("PaySeraCheckout form request: {}", form);

        // TODO: verify that form renders all details correctly

        return form.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation,
                                          final Map map,
                                          final boolean forceProcessing) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(map);

        // TODO: update payment object with payment callback data, see PayPalButton impl

        return payment;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }



}
