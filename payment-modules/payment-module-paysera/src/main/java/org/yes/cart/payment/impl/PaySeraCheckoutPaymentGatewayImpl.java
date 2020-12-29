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

import org.apache.commons.collections.MapUtils;
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
import org.yes.cart.payment.utils.PaySeraUtils;
import org.yes.cart.payment.utils.PaySeraRequest;
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
public class PaySeraCheckoutPaymentGatewayImpl extends AbstractPaySeraPaymentGatewayImpl
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
        
        final Map<String, String> originData = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);

		final String dataAsBase64 = originData.get("data");
        final String ss1AsBase64 = originData.get("ss1");

		final String dataQuery = PaySeraUtils.decodeBase64UrlSafeToString(dataAsBase64);
        final Map<String, String> parsedParams = PaySeraUtils.parseQueryString(dataQuery);

        final boolean valid = isValid(dataAsBase64, ss1AsBase64);

        if (valid || forceProcessing) {
            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }
            final String orderNumber = parsedParams.get("orderid");

            /*
                Payment status:
                 0 - Payment has not been executed
                 1 - Payment successful
                 2 - Payment order accepted, but not yet executed
                 3 - Additional payment information
             */
            final String paymentStatus = parsedParams.get("status");
            final boolean refund = false; // TODO: does PaySera support refund notifications?
            BigDecimal callbackAmount = null;
            try {
                callbackAmount = new BigDecimal(parsedParams.get("amount")).movePointLeft(2); // Amount in cents
            } catch (Exception exp) {
                LOG.error("Callback for {} did not have a valid amount {}", orderNumber, parsedParams.get("amount"));
            }

            if ("3".equals(paymentStatus)) {

                return new BasicCallbackInfoImpl(
                        orderNumber,
                        CallbackOperation.INFO,
                        callbackAmount, privateCallBackParameters, valid
                );

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

    boolean isValid(final String dataAsBase64, final String ss1AsBase64) {

        final String sign = PaySeraUtils.calculateMD5(dataAsBase64 + getParameterValue(PSC_SIGN_PASSWORD));

        return sign.equals(ss1AsBase64);
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

        final Map<String, String> originData = callbackResult;

        final String dataAsBase64 = originData.get("data");
        final String ss1AsBase64 = originData.get("ss1");

        final String dataQuery = PaySeraUtils.decodeBase64UrlSafeToString(dataAsBase64);
        final Map<String, String> parsedParams = PaySeraUtils.parseQueryString(dataQuery);

        final boolean valid = isValid(dataAsBase64, ss1AsBase64);

        final String statusRes = parsedParams.get("status");

        if (valid || forceProcessing) {

            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }

            /* As per documentation:
            Payment status:
                0 - payment has not been executed
                      TODO: find out how this works, assume it is a cancel for now
                1 - payment successful
                        -> CallbackAware.CallbackResult.OK
                2 - payment order accepted, but not yet executed (this status does not guarantee execution of the payment)
                        -> CallbackAware.CallbackResult.UNSETTLED
                3 - additional payment information
                        -> This will be ignored as this will result in callback operation INFO
             */
            final boolean success = statusRes != null &&
                    ("1".equalsIgnoreCase(statusRes)
                            || "2".equalsIgnoreCase(statusRes));

            if (success) {

                if ("1".equalsIgnoreCase(statusRes)) {
                    LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.OK);
                    return CallbackAware.CallbackResult.OK;
                }
                LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.UNSETTLED);
                return CallbackAware.CallbackResult.UNSETTLED;
            }

        } else {
            LOG.debug("Signature is not valid");
        }
        LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.FAILED);
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
    	
    	// Construct a PaySera request
    	PaySeraRequest request = new PaySeraRequest();
    	request.setProjectId(getParameterValue(PSC_PROJECTID));
    	request.setVersion(getParameterValue(PSC_API_VERSION));
    	final String envMode = getParameterValue(PSC_ENVIRONMENT);
    	request.setTest(!"live".equalsIgnoreCase(envMode));
    	request.setCallbackUrl(getParameterValue(PSC_CALLBACKURL));
    	request.setAcceptUrl(getParameterValue(PSC_ACCEPTURL));
    	request.setCancelUrl(getParameterValue(PSC_CANCELURL));
    	request.setCurrency(currencyCode);
    	request.setAmount(amount != null ? amount.movePointRight(2).intValueExact() : null);
    	request.setOrderId(orderReference);
    	request.setLanguage(paymentLocaleTranslator.translateLocale(this, locale));
    	if (payment.getBillingAddress() != null) {
    		request.setPayerFirstName(payment.getBillingAddress().getFirstname());
    		request.setPayerLastName(payment.getBillingAddress().getLastname());
    		request.setEmail(payment.getBillingEmail());
    	}
    	if (payment.getShippingAddress() != null) {
    		request.setPayerStreet(StringUtils.join(Arrays.asList(
                    payment.getShippingAddress().getAddrline1(),
                    payment.getShippingAddress().getAddrline2()), ' '));
    		request.setPayerCity(payment.getShippingAddress().getCity());
    		request.setPayerCountryCode(payment.getShippingAddress().getCountryCode());
    		request.setPayerState(payment.getShippingAddress().getStateCode());
    		request.setPayerZip(payment.getShippingAddress().getPostcode());
    	}
    	final StringBuilder paytext = new StringBuilder();
    	paytext.append("\n");
    	for (final PaymentLine item : payment.getOrderItems()) {
    		paytext.append("* " + item.getSkuName() + " x" + item.getQuantity());
    		paytext.append("\n");
    	}

    	String template = getParameterValue("PSC_MESSAGE_TEMPLATE_" + locale);
    	if (StringUtils.isBlank(template)) {
    	    template = "[items]";
        }

        final String payseraMsg = template.replace("[items]", paytext.toString());

    	request.setPayText(payseraMsg.length() < 255 ? payseraMsg : paytext.substring(0, 255));
    	
//        onlyPayment TODO: for now allowing all that is configured in PaySera
//        disallowPayments
//        timeLimit
    	    	
    	String data = request.toBase64String();
    	form.append(getHiddenFieldValue("data", data));
    	
    	String sign = PaySeraUtils.calculateMD5(data + getParameterValue(PSC_SIGN_PASSWORD));
    	form.append(getHiddenFieldValue("sign", sign));

        LOG.debug("PaySeraCheckout form request: {}", form);

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

        final String dataAsBase64 = params.get("data");

        final Map<String, String> parsedParams;
        if (StringUtils.isNotBlank(dataAsBase64)) {
            final String dataQuery = PaySeraUtils.decodeBase64UrlSafeToString(dataAsBase64);
            parsedParams = PaySeraUtils.parseQueryString(dataQuery);
        } else {
            parsedParams = Collections.emptyMap();
        }

        final String amount = parsedParams.get("payamount");
        if (amount != null) {
            payment.setPaymentAmount(new BigDecimal(amount));
        }
        payment.setOrderCurrency(parsedParams.get("paycurrency"));
        payment.setTransactionReferenceId(parsedParams.get("requestid"));
        payment.setTransactionAuthorizationCode(parsedParams.get("requestid"));
        payment.setCardNumber(null);
        payment.setCardType(null);
        final StringBuilder name = new StringBuilder();
        if (parsedParams.get("name") != null) {
            name.append(parsedParams.get("name"));
        }
        if (parsedParams.get("surename") != null) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(parsedParams.get("surename"));
        }
        payment.setCardHolderName(name.length() > 0 ? name.toString() : null);

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(map);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(params, forceProcessing);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());

        final StringBuilder msg = new StringBuilder();
        msg.append(parsedParams.get("status"));
        if (StringUtils.isNotBlank(parsedParams.get("type"))) {
            msg.append(" ").append(parsedParams.get("type"));
        }
        if (StringUtils.isNotBlank(parsedParams.get("payment"))) {
            msg.append(" ").append(parsedParams.get("payment"));
        }
        if (StringUtils.isNotBlank(parsedParams.get("m_pay_restored"))) {
            msg.append(" ").append(parsedParams.get("m_pay_restored"));
        }
        if (StringUtils.isNotBlank(parsedParams.get("account"))) {
            msg.append(" ").append(parsedParams.get("account"));
        }
        payment.setTransactionOperationResultMessage(msg.toString());

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
