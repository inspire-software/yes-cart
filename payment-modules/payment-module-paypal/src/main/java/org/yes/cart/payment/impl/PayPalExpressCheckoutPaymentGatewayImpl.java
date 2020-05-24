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
import org.springframework.util.Assert;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayPayPalExpressCheckout;
import org.yes.cart.payment.dto.*;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.HttpParamsUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.log.Markers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 19/11/2015
 * Time: 18:13
 */
public class PayPalExpressCheckoutPaymentGatewayImpl extends AbstractPayPalNVPPaymentGatewayImpl
        implements PaymentGatewayPayPalExpressCheckout, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalExpressCheckoutPaymentGatewayImpl.class);

    protected static final String PP_SUBMIT_BTN = "PP_SUBMIT_BTN";


    protected static final String PP_EC_PAYMENTREQUEST_0_AMT = "PAYMENTREQUEST_0_AMT";
    protected static final String PP_EC_PAYMENTREQUEST_0_TAXAMT = "PAYMENTREQUEST_0_TAXAMT";
    protected static final String PP_EC_PAYMENTREQUEST_0_PAYMENTACTION = "PAYMENTREQUEST_0_PAYMENTACTION";
    protected static final String PP_EC_PAYMENTREQUEST_0_CURRENCYCODE = "PAYMENTREQUEST_0_CURRENCYCODE";
    protected static final String PP_EC_PAYMENTREQUEST_0_INVNUM = "PAYMENTREQUEST_0_INVNUM";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_NAME = "L_PAYMENTREQUEST_0_NAME";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_AMT = "L_PAYMENTREQUEST_0_AMT";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_NUMBER = "L_PAYMENTREQUEST_0_NUMBER";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_QTY = "L_PAYMENTREQUEST_0_QTY";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_TAXAMT = "L_PAYMENTREQUEST_0_TAXAMT";
    protected static final String PP_EC_L_PAYMENTREQUEST_0_DESC = "L_PAYMENTREQUEST_0_DESC";
    protected static final String PP_EC_L_SHIPPINGOPTIONISDEFAULT0 = "L_SHIPPINGOPTIONISDEFAULT0";
    protected static final String PP_EC_L_SHIPPINGOPTIONNAME0 = "L_SHIPPINGOPTIONNAME0";
    protected static final String PP_EC_L_SHIPPINGOPTIONAMOUNT0 = "L_SHIPPINGOPTIONAMOUNT0";
    protected static final String PP_EC_RETURNURL = "RETURNURL";
    protected static final String PP_EC_CANCELURL = "CANCELURL";
    protected static final String PP_EC_CALLBACK = "CALLBACK";
    protected static final String PP_EC_NOSHIPPING = "NOSHIPPING";



    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, true,
            true, true, true, true,
            null,
            false , false
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }


    /**
     * Get the POST url for form
     */
    @Override
    public String getPostActionUrl() {
        // This is the PayPalExpressCheckoutFilter mapping, which performs setExpressCheckoutMethod
        // and then redirects to PayPal to login and authorise the payment
        return "paymentpaypalexpress?start=1";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {

        String submit = getParameterValue(PP_SUBMIT_BTN + "_" + locale);
        if (submit == null) {
            submit = getParameterValue(PP_SUBMIT_BTN);
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
        return super.refund(paymentIn, forceProcessing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters, final boolean forceProcessing) {
        return new BasicCallbackInfoImpl(
                HttpParamsUtils.getSingleValue(privateCallBackParameters.get(ORDER_GUID)),
                CallbackOperation.PAYMENT,
                null, privateCallBackParameters, true
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param payment payment
     * @param token   the token obtained via   SetExpressCheckout method
     *
     * @return map of parsed key - values with detail information
     */
    @Override
    public Map<String, String> doExpressCheckoutPayment(final Payment payment,
                                                        final String token) {

        Assert.notNull(token, "The pay pal tonek must  be not null");
        Assert.notNull(payment, "Payment must be provided");
        Assert.notNull(payment.getPaymentAmount(), "Amount must be provided");
        Assert.isTrue(MoneyUtils.isPositive(payment.getPaymentAmount()), "Amount must be positive");
        Assert.notNull(payment.getOrderCurrency(), "Currency code must be provided");

        try {

            final Map<String, String> details = getExpressCheckoutDetails(token);
            if (details.containsKey(PP_EC_PAYERID)) {

                final NvpBuilder npvs = new NvpBuilder();

                npvs
                        .addRaw(PP_EC_TOKEN, token)
                        .addRaw(PP_EC_PAYERID, details.get(PP_EC_PAYERID));

                appendOrderDetails(payment, npvs);

                return performHttpCall("DoExpressCheckoutPayment", npvs.toMap());

            }

        } catch (Exception exp) {

            LOG.error(Markers.alert(), "PayPal Express transaction [" + payment.getOrderNumber() + "] failed, cause: " + exp.getMessage(), exp);
            LOG.error("PayPal Express transaction failed, payment: " + payment, exp);

        }

        return Collections.EMPTY_MAP;

    }

    private static final int ITEMSKU = 50;
    private static final int ITEMNAME = 50;

    private void appendOrderDetails(final Payment payment, final NvpBuilder npvs) {

        npvs.addRaw(PP_EC_PAYMENTREQUEST_0_PAYMENTACTION, "Sale");
        npvs.addEncoded(PP_EC_PAYMENTREQUEST_0_INVNUM, payment.getOrderNumber());
        npvs.addRaw(PP_EC_NOSHIPPING, "1");

        int i = 0;

        BigDecimal itemsNetTotal = Total.ZERO;
        BigDecimal ship = Total.ZERO;

        for (final PaymentLine item : payment.getOrderItems()) {

            if (item.isShipment()) {

                npvs.addRaw(PP_EC_L_SHIPPINGOPTIONISDEFAULT0, "true");

                ship = item.getUnitPrice();
                String shipName = item.getSkuName();

                npvs.addEncoded(PP_EC_L_SHIPPINGOPTIONNAME0,
                        shipName.length() > ITEMNAME ? shipName.substring(0, ITEMNAME - 1) + "~" : shipName);

                npvs.addRaw(PP_EC_L_SHIPPINGOPTIONAMOUNT0, ship.toPlainString());

            } else {

                final BigDecimal intQty = item.getQuantity().setScale(0, RoundingMode.CEILING);

                final String skuName;
                final BigDecimal qty;
                if (MoneyUtils.isFirstEqualToSecond(intQty, item.getQuantity())) {
                    // integer qty
                    skuName = item.getSkuName();
                    qty = intQty.stripTrailingZeros();
                } else {
                    // fractional qty
                    skuName = item.getQuantity().toPlainString().concat("x ").concat(item.getSkuName());
                    qty = BigDecimal.ONE;
                }

                npvs.addEncoded(PP_EC_L_PAYMENTREQUEST_0_NUMBER + i,
                        item.getSkuCode().length() > ITEMSKU ? item.getSkuCode().substring(0, ITEMSKU - 1) + "~" : item.getSkuCode());
                npvs.addEncoded(PP_EC_L_PAYMENTREQUEST_0_NAME + i,
                        skuName.length() > ITEMNAME ? skuName.substring(0, ITEMNAME - 1) + "~" : skuName);

                npvs.addRaw(PP_EC_L_PAYMENTREQUEST_0_QTY + i, qty.stripTrailingZeros().toPlainString());

                final BigDecimal itemNetAmount = item.getUnitPrice().multiply(item.getQuantity()).subtract(item.getTaxAmount()).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP);
                final BigDecimal itemNetPricePerAdjustedQty = itemNetAmount.divide(qty, Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP);
                // Need to do this to overcome rounding
                final BigDecimal restoredNetAmount = itemNetPricePerAdjustedQty.multiply(qty).setScale(Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP);

                itemsNetTotal = itemsNetTotal.add(restoredNetAmount);
//                final BigDecimal taxUnit = MoneyUtils.isPositive(item.getTaxAmount()) ? item.getTaxAmount().divide(qty, Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP) : Total.ZERO;

                npvs.addRaw(PP_EC_L_PAYMENTREQUEST_0_AMT + i, itemNetPricePerAdjustedQty.toPlainString());

//                npvs.addRaw(PP_EC_L_PAYMENTREQUEST_0_TAXAMT + i, taxUnit.toPlainString());

                i++;
            }
        }

        final BigDecimal itemsAndShipping = itemsNetTotal.add(ship);
        final BigDecimal paymentNet = payment.getPaymentAmount().subtract(payment.getTaxAmount());
        if (MoneyUtils.isFirstBiggerThanSecond(itemsAndShipping, paymentNet)) {

            npvs.addRaw("PAYMENTREQUEST_0_SHIPDISCAMT", paymentNet.subtract(itemsAndShipping).toPlainString());

        }

        npvs.addRaw("PAYMENTREQUEST_0_ITEMAMT", itemsNetTotal.toPlainString());
        npvs.addRaw("PAYMENTREQUEST_0_SHIPPINGAMT", ship.toPlainString());

        npvs.addRaw(PP_EC_PAYMENTREQUEST_0_AMT, payment.getPaymentAmount().toPlainString());

        // PP recommend to set MAXAMT to slightly higher value, so increase by 10%
        npvs.addRaw("MAXAMT",
                payment.getPaymentAmount().multiply(new BigDecimal("1.1")).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP).toPlainString());

        npvs.addRaw(PP_EC_PAYMENTREQUEST_0_TAXAMT, payment.getTaxAmount().toPlainString());

        final PaymentAddress addr = payment.getShippingAddress() != null ? payment.getShippingAddress() : payment.getBillingAddress();

        if (addr != null) {
            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTONAME", addr.getFirstname() + " " + addr.getLastname());
            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOSTREET", addr.getAddrline1());
            if (StringUtils.isNotBlank(addr.getAddrline2())) {
                npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOSTREET2", addr.getAddrline2());
            }

            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOCITY", addr.getCity());
            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOSTATE", addr.getStateCode());
            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOZIP", addr.getPostcode());
            npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOCOUNTRYCODE", addr.getCountryCode());

            if (StringUtils.isNotBlank(addr.getPhone1())) {
                npvs.addEncoded("PAYMENTREQUEST_0_SHIPTOPHONENUM", addr.getPhone1());
            }

        }

        npvs.addRaw(PP_EC_PAYMENTREQUEST_0_CURRENCYCODE, payment.getOrderCurrency());

    }


    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token the token obtained via   SetExpressCheckout method
     * @return map of parsed key - values with detail information
     * @throws java.io.IOException in case of errors
     */
    protected Map<String, String> getExpressCheckoutDetails(final String token) throws IOException {

        Assert.notNull(token, "The pay pal token must  be not null");

        final NvpBuilder npvs = new NvpBuilder();

        npvs.addRaw(PP_EC_TOKEN, token);

        return performHttpCall("GetExpressCheckoutDetails", npvs.toMap());

    }


    /**
     * Support for pp express checkout. In case if gateway not support this operation , return will be empty hashmap.
     *
     * All info about SetExpressCheckout see here:
     * https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_nvp_r_SetExpressCheckout
     *
     * @param payment       payment
     *
     * @return continue URL
     */
    @Override
    public String setExpressCheckoutMethod(final Payment payment, final String verify) {

        Assert.notNull(payment, "Payment must be provided");
        Assert.notNull(payment.getPaymentAmount(), "Amount must be provided");
        Assert.isTrue(MoneyUtils.isPositive(payment.getPaymentAmount()), "Amount must be positive");
        Assert.notNull(payment.getOrderCurrency(), "Currency code must be provided");


        final NvpBuilder npvs = new NvpBuilder();

        final String confirmPaymentStep = getParameterValue(PP_EC_CALLBACK) +
                "?orderGuid=" + payment.getOrderNumber() +
                "&verify=" + verify; // Need verification to validate response

//        npvs.addEncoded(PP_EC_RETURNURL, getParameterValue(PP_EC_RETURNURL));
        npvs.addEncoded(PP_EC_RETURNURL, confirmPaymentStep); // This is return URL to confirm payment
        npvs.addEncoded(PP_EC_CANCELURL, getParameterValue(PP_EC_CANCELURL));
//        npvs.addEncoded(PP_EC_CALLBACK,
//                getParameterValue(PP_EC_CALLBACK) +
//                        "?orderGuid=" + payment.getOrderNumber() +
//                        "&verify=" + verify); // Need verification to validate response

        npvs.addRaw("CALLBACKTIMEOUT", "3"); // PayPal recommended value

        appendOrderDetails(payment, npvs);

        try {

            final Map<String, String> result = performHttpCall("SetExpressCheckout", npvs.toMap());

            final String redirectUrl;

            if (CallbackAware.CallbackResult.OK == getExternalCallbackResult(result, false)) {
                /*not encoded answer will be like this
                TOKEN=EC%2d8DX631540T256421Y&TIMESTAMP=2011%2d12%2d21T20%3a12%3a37Z&CORRELATIONID=2d2aa98bcd550&ACK=Success&VERSION=2%2e3&BUILD=2271164
                 Redirect url  to paypal for perform login and payment */
                redirectUrl = getParameterValue(PP_EC_PAYPAL_URL)
                        + "?orderGuid="  + payment.getOrderNumber()
                        + "&token="      + result.get("TOKEN")
                        + "&cmd=_express-checkout";

            } else {
                redirectUrl = "paymentresult" //mounted page
                        + "?orderNum="   + payment.getOrderNumber()
                        + "&hint=failed"
                        + "&errMsg="     + result.get("L_ERRORCODE0")
                        + '|'   + result.get("L_SHORTMESSAGE0")
                        + '|'   + result.get("L_LONGMESSAGE0")
                        + '|'   + result.get("L_SEVERITYCODE0") ;

            }

            return redirectUrl;

        } catch (IOException e) {

            LOG.error("Payment failed: " + e.getMessage(), e);

        }

        return "paymentresult" //mounted page
                + "?orderNum="   + payment.getOrderNumber()
                + "&hint=failed";
    }


    /**
     * Check the result for success attributes.
     *
     * @param callbackResult call result
     *
     * @return true in case of success
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult, final boolean forceProcessing) {
        if (isAckSuccess(callbackResult)) {
            return CallbackAware.CallbackResult.OK;
        }
        return  CallbackAware.CallbackResult.FAILED;
    }

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHiddenFieldValue(ORDER_GUID, orderReference));  // this will be passed via payment gateway to restore it latter

        LOG.debug("PayPalExpress form request: {}", stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation, final Map parametersMap, final boolean forceProcessing) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(parametersMap);
        payment.setTransactionRequestToken(params.get("TOKEN"));
        payment.setTransactionReferenceId(params.get("PAYMENTINFO_0_TRANSACTIONID"));
        payment.setTransactionAuthorizationCode(params.get("PAYERID"));

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(parametersMap);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(params, forceProcessing);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());

        payment.setShopperIpAddress(params.get(PaymentMiscParam.CLIENT_IP));
        return payment;

    }

}
