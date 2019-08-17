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

import com.paypal.ipn.IPNMessage;
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
import org.yes.cart.service.payment.PaymentLocaleTranslator;
import org.yes.cart.service.payment.impl.PaymentLocaleTranslatorImpl;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.HttpParamsUtils;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 19/11/2015
 * Time: 18:13
 */
public class PayPalButtonPaymentGatewayImpl extends AbstractPayPalPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalButtonPaymentGatewayImpl.class);

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, true,
            null,
            false, false
    );

    // form post acton url
    static final String PPB_POST_URL = "PPB_POST_URL";

    // Configuration parameters
    static final String PPB_ENVIRONMENT = "PPB_ENVIRONMENT";
    static final String PPB_BUSINESS = "PPB_BUSINESS";
    static final String PPB_USER = "PPB_USER";
    static final String PPB_PASSWORD = "PPB_PASSWORD";
    static final String PPB_SIGNATURE = "PPB_SIGNATURE";
    static final String PPB_BUYBUTTON = "PPB_BUYBUTTON";
    static final String PPB_NOTIFYURL = "PPB_NOTIFYURL";
    static final String PPB_RETURNURL = "PPB_RETURNURL";
    static final String PPB_CANCELURL = "PPB_CANCELURL";

    private final PaymentLocaleTranslator paymentLocaleTranslator = new PaymentLocaleTranslatorImpl();


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        return getParameterValue(PPB_POST_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {

        String submit = getParameterValue(PPB_BUYBUTTON + "_" + locale);
        if (submit == null) {
            submit = getParameterValue(PPB_BUYBUTTON);
        }
        if (StringUtils.isNotBlank(submit)) {
            return submit;
        }
        return null;
    }

    protected IPNMessage createIPNMessage(final Map<String, String[]> requestParams) {

        final Map<String, String> configurationMap = new HashMap<>();
        setParameterIfNotNull(configurationMap, "acct1.UserName", PPB_USER);
        setParameterIfNotNull(configurationMap, "acct1.Password", PPB_PASSWORD);
        setParameterIfNotNull(configurationMap, "acct1.Signature", PPB_SIGNATURE);
        setParameterIfNotNull(configurationMap, "mode", PPB_ENVIRONMENT);

        return new IPNMessage(requestParams, configurationMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters, final boolean forceProcessing) {

        final IPNMessage ipn = createIPNMessage(privateCallBackParameters);

        final boolean valid = ipn.validate();

        if (valid || forceProcessing) {
            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }
            final String invoice = ipn.getIpnValue("invoice");
            final String paymentStatus = ipn.getIpnValue("payment_status");
            final boolean refund = "Refunded".equalsIgnoreCase(paymentStatus);
            BigDecimal callbackAmount = null;
            try {
                callbackAmount = new BigDecimal(ipn.getIpnValue("mc_gross"));
                if (refund && callbackAmount.compareTo(BigDecimal.ZERO) < 0) {
                    callbackAmount = callbackAmount.negate(); // if amount is negative turn it to positive
                }
            } catch (Exception exp) {
                LOG.error("Callback for {} did not have a valid amount {}", invoice, ipn.getIpnValue("mc_gross"));
            }

            if (StringUtils.isBlank(invoice)) {
                return new BasicCallbackInfoImpl(
                        ipn.getIpnValue("custom"),
                        refund ? CallbackOperation.REFUND : CallbackOperation.PAYMENT,
                        callbackAmount, privateCallBackParameters, valid
                );
            }
            return new BasicCallbackInfoImpl(
                    invoice,
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

        final IPNMessage ipn = createIPNMessage(request);

        final boolean valid = ipn.validate();
        final String paymentStatus = ipn.getIpnValue("payment_status");

        if (valid || forceProcessing) {

            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }

            final boolean settled = "Completed".equalsIgnoreCase(paymentStatus);

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

        if (PaymentGateway.REFUND_NOTIFY.equals(processorOperation) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            if (params.get("txn_id") != null) {
                payment.setTransactionReferenceId(params.get("txn_id"));
            }
            if (params.get("ipn_track_id") != null) {
                payment.setTransactionAuthorizationCode(params.get("ipn_track_id"));
            }

            if (params.get("payment_status") != null || params.get("payer_id") != null) {
                payment.setTransactionOperationResultMessage(
                        params.get("payment_status")
                                + " "
                                + params.get("payer_id")
                );
            }

        }


    }

    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if ((PaymentGateway.REFUND.equals(processorOperation) || PaymentGateway.VOID_CAPTURE.equals(processorOperation)) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            if (params.get("txn_id") != null) {
                payment.setTransactionReferenceId(params.get("txn_id"));
            }
            if (params.get("ipn_track_id") != null) {
                payment.setTransactionAuthorizationCode(params.get("ipn_track_id"));
            }

            if (params.get("payment_status") != null || params.get("payer_id") != null) {
                payment.setTransactionOperationResultMessage(
                        params.get("payment_status")
                                + " "
                                + params.get("payer_id")
                );
            }
            if (payment.getTransactionOperationResultMessage() != null) {
                payment.setTransactionOperationResultMessage(
                        payment.getTransactionOperationResultMessage() + ". Amount: " + callback.getAmount().toPlainString());
            } else {
                payment.setTransactionOperationResultMessage("Amount: " + callback.getAmount().toPlainString());
            }

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

        form.append(getHiddenFieldValue("button", "buynow"));
        form.append(getHiddenFieldValue("cmd", "_cart"));
        form.append(getHiddenFieldValue("upload", "1"));
        form.append(getHiddenFieldValue("paymentaction", "sale"));

        form.append(getHiddenFieldParam("business", PPB_BUSINESS, PPB_USER));
        form.append(getHiddenFieldParam("env", PPB_ENVIRONMENT));
        form.append(getHiddenFieldParam("notify_url", PPB_NOTIFYURL));
        form.append(getHiddenFieldParam("return", PPB_RETURNURL));
        form.append(getHiddenFieldParam("cancel_return", PPB_CANCELURL));

        form.append(getHiddenFieldValue("currency_code", currencyCode));
        form.append(getHiddenFieldValue("invoice", orderReference));
        form.append(getHiddenFieldValue("custom", orderReference));

        form.append(getHiddenFieldValue("lc", paymentLocaleTranslator.translateLocale(this, locale)));
        form.append(getHiddenFieldValue("charset", "UTF-8"));

        if (payment.getBillingAddress() != null) {
            form.append(getHiddenFieldValue("first_name", payment.getBillingAddress().getFirstname()));
            form.append(getHiddenFieldValue("last_name", payment.getBillingAddress().getLastname()));
            form.append(getHiddenFieldValue("email", payment.getBillingEmail()));
        }
        if (payment.getShippingAddress() != null) {
            form.append(getHiddenFieldValue("address1", payment.getShippingAddress().getAddrline1()));
            form.append(getHiddenFieldValue("address2", payment.getShippingAddress().getAddrline2()));
            form.append(getHiddenFieldValue("city", payment.getShippingAddress().getCity()));
            form.append(getHiddenFieldValue("country", payment.getShippingAddress().getCountryCode()));
            form.append(getHiddenFieldValue("state", payment.getShippingAddress().getStateCode()));
            form.append(getHiddenFieldValue("zip", payment.getShippingAddress().getPostcode()));
            form.append(getHiddenFieldValue("address_override", "1"));
        }

        BigDecimal totalItems = Total.ZERO;

        int i = 1;
        for (final PaymentLine item : payment.getOrderItems()) {
            form.append(getHiddenFieldValue("item_name_" + i, item.getSkuName()));
            form.append(getHiddenFieldValue("item_number_" + i, item.getSkuCode()));
            // PayPal can only handle whole values, so do ceil
            final BigDecimal ppQty = item.getQuantity().setScale(0, BigDecimal.ROUND_CEILING);
            form.append(getHiddenFieldValue("quantity_" + i, ppQty.toPlainString()));

            final BigDecimal taxUnit = MoneyUtils.isPositive(item.getTaxAmount()) ?
                            item.getTaxAmount().divide(item.getQuantity(), Total.ZERO.scale(), RoundingMode.HALF_UP)
                                : Total.ZERO;
            final BigDecimal itemAmount = item.getUnitPrice().subtract(taxUnit);
            form.append(getHiddenFieldValue("amount_" + i, itemAmount.toPlainString()));
//            form.append(getHiddenFieldValue("tax_" + i, taxUnit.setScale(Total.ZERO.scale(), RoundingMode.HALF_UP).toPlainString()));
            if (ppQty.compareTo(item.getQuantity()) != 0) {
                // If we have decimals in qty need to save it as item option
                form.append(getHiddenFieldValue("on0_" + i, "x"));
                form.append(getHiddenFieldValue("on1_" + i, item.getQuantity().toPlainString()));
            }
            i++;
            totalItems = totalItems.add(itemAmount.multiply(item.getQuantity()));
        }

        final BigDecimal payNet = payment.getPaymentAmount().subtract(payment.getTaxAmount());
        if (payNet.compareTo(totalItems) < 0) {
            form.append(getHiddenFieldValue("discount_amount_cart",
                    totalItems.subtract(payNet).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP).toPlainString()));
        }
        form.append(getHiddenFieldValue("tax_cart", payment.getTaxAmount().toPlainString()));

        LOG.debug("PayPalButton form request: {}", form);

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

        final String amount = params.get("mc_gross");
        if (amount != null) {
            payment.setPaymentAmount(new BigDecimal(amount));
        }
        payment.setOrderCurrency(params.get("mc_currency"));
        payment.setTransactionReferenceId(params.get("txn_id"));
        payment.setTransactionAuthorizationCode(params.get("ipn_track_id"));
        payment.setCardNumber(null);
        payment.setCardType(null);
        final StringBuilder name = new StringBuilder();
        if (params.get("first_name") != null) {
            name.append(params.get("first_name"));
        }
        if (params.get("last_name") != null) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(params.get("last_name"));
        }
        payment.setCardHolderName(name.length() > 0 ? name.toString() : null);

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(map);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(params, forceProcessing);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());

        final StringBuilder msg = new StringBuilder();
        msg.append(params.get("payment_status"));
        if (StringUtils.isNotBlank(params.get("pending_reason"))) {
            msg.append(" ").append(params.get("pending_reason"));
        }
        if (StringUtils.isNotBlank(params.get("payer_id"))) {
            msg.append(" ").append(params.get("payer_id"));
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
