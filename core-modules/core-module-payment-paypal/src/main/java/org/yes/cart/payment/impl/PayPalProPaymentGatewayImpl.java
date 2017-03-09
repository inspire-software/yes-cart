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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.HttpParamsUtils;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.log.Markers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 24/11/2015
 * Time: 08:29
 */
public class PayPalProPaymentGatewayImpl extends AbstractPayPalNVPPaymentGatewayImpl implements PaymentGatewayInternalForm {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalProPaymentGatewayImpl.class);

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, true, true, false,
            true, true, true,
            false, true, true,
            null,
            false, true
    );


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "payPalProPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public synchronized PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        final NvpBuilder npvs = createAuthRequest(payment, "Authorization");
        return runTransaction("DoDirectPayment", npvs.toMap(), payment, AUTH);
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }


    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        final NvpBuilder npvs = new NvpBuilder();
        npvs.addRaw("AUTHORIZATIONID", payment.getTransactionReferenceId());
        return runTransaction("DoVoid", npvs.toMap(), payment, REVERSE_AUTH);
    }

    /**
     * Void capture performed as refund
     * <p/>
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {

        final Payment reverse = reverseAuthorization(paymentIn);
        if (Payment.PAYMENT_STATUS_OK.equals(reverse.getPaymentProcessorResult())) {
            reverse.setTransactionOperation(VOID_CAPTURE);
            return reverse;
        }
        final Payment refund = super.refund(paymentIn);
        refund.setTransactionOperation(VOID_CAPTURE);
        return refund;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment reverse = reverseAuthorization(paymentIn);
        if (Payment.PAYMENT_STATUS_OK.equals(reverse.getPaymentProcessorResult())) {
            reverse.setTransactionOperation(VOID_CAPTURE);
            return reverse;
        }
        return super.refund(paymentIn);
    }


    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        final NvpBuilder npvs = new NvpBuilder();
        npvs.addRaw("AUTHORIZATIONID", payment.getTransactionReferenceId());
        npvs.addRaw("AMT", payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
        npvs.addRaw("COMPLETETYPE", "NotComplete");
        npvs.addRaw("CURRENCYCODE", payment.getOrderCurrency());
        npvs.addEncoded("INVNUM", payment.getOrderShipment());
        return runTransaction("DoCapture", npvs.toMap(), payment, CAPTURE);

    }

    private static final int ITEMSKU = 50;
    private static final int ITEMNAME = 50;


    private NvpBuilder createAuthRequest(final Payment payment, final String paymentAction) {
        final NvpBuilder npvs = new NvpBuilder();
        npvs.addRaw("PAYMENTACTION", paymentAction);
        npvs.addRaw("INVNUM", payment.getOrderShipment());
        npvs.addRaw("CREDITCARDTYPE", payment.getCardType());
        npvs.addRaw("ACCT", payment.getCardNumber());
        npvs.addRaw("EXPDATE",
                payment.getCardExpireMonth()
                        + payment.getCardExpireYear());
        npvs.addRaw("CVV2", payment.getCardCvv2Code());

        npvs.addRaw("AMT", payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
        npvs.addRaw("CURRENCYCODE", payment.getOrderCurrency());

        int i = 0;

        BigDecimal itemsNetTotal = Total.ZERO;
        BigDecimal ship = Total.ZERO;

        for (final PaymentLine item : payment.getOrderItems()) {

            if (item.isShipment()) {

                ship = item.getUnitPrice();

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

                npvs.addEncoded("L_NUMBER" + i,
                        item.getSkuCode().length() > ITEMSKU ? item.getSkuCode().substring(0, ITEMSKU - 1) + "~" : item.getSkuCode());
                npvs.addEncoded("L_NAME" + i,
                        skuName.length() > ITEMNAME ? skuName.substring(0, ITEMNAME - 1) + "~" : skuName);

                npvs.addRaw("L_QTY" + i, qty.stripTrailingZeros().toPlainString());

                final BigDecimal itemNetAmount = item.getUnitPrice().multiply(item.getQuantity()).subtract(item.getTaxAmount()).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP);
                final BigDecimal itemNetPricePerAdjustedQty = itemNetAmount.divide(qty, Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP);
                // Need to do this to overcome rounding
                final BigDecimal restoredNetAmount = itemNetPricePerAdjustedQty.multiply(qty).setScale(Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP);

                itemsNetTotal = itemsNetTotal.add(restoredNetAmount);
//                final BigDecimal taxUnit = MoneyUtils.isFirstBiggerThanSecond(item.getTaxAmount(), Total.ZERO) ? item.getTaxAmount().divide(qty, Total.ZERO.scale(), BigDecimal.ROUND_HALF_UP) : Total.ZERO;

                npvs.addRaw("L_AMT" + i, itemNetPricePerAdjustedQty.toPlainString());

//                npvs.addRaw("L_TAXAMT" + i, taxUnit.toPlainString());

                i++;
            }
        }

        final BigDecimal itemsAndShipping = itemsNetTotal.add(ship);
        final BigDecimal paymentNet = payment.getPaymentAmount().subtract(payment.getTaxAmount());
        if (MoneyUtils.isFirstBiggerThanSecond(itemsAndShipping, paymentNet)) {

            npvs.addRaw("SHIPDISCAMT", paymentNet.subtract(itemsAndShipping).toPlainString());

        }

        npvs.addRaw("ITEMAMT", itemsNetTotal.toPlainString());
        npvs.addRaw("SHIPPINGAMT", ship.toPlainString());
        npvs.addRaw("TAXAMT", payment.getTaxAmount().toPlainString());

        if (payment.getBillingAddress() != null) {
            npvs.addEncoded("EMAIL", payment.getBillingEmail());
            npvs.addEncoded("FIRSTNAME", payment.getBillingAddress().getFirstname());
            npvs.addEncoded("LASTNAME", payment.getBillingAddress().getLastname());
            npvs.addEncoded("STREET", payment.getBillingAddress().getAddrline1());
            if (StringUtils.isNotBlank(payment.getBillingAddress().getAddrline2())) {
                npvs.addEncoded("STREET2", payment.getBillingAddress().getAddrline2());
            }
            npvs.addEncoded("CITY", payment.getBillingAddress().getCity());
            npvs.addEncoded("STATE", payment.getBillingAddress().getStateCode());
            npvs.addEncoded("ZIP", payment.getBillingAddress().getStateCode());
            npvs.addEncoded("COUNTRYCODE", payment.getBillingAddress().getCountryCode());
        }

        if (payment.getShippingAddress() != null) {
            npvs.addEncoded("SHIPTONAME", payment.getShippingAddress().getFirstname() + " " + payment.getShippingAddress().getLastname());
            npvs.addEncoded("SHIPTOSTREET", payment.getShippingAddress().getAddrline1());
            if (StringUtils.isNotBlank(payment.getShippingAddress().getAddrline2())) {
                npvs.addEncoded("SHIPTOSTREET2", payment.getShippingAddress().getAddrline2());
            }
            npvs.addEncoded("SHIPTOCITY", payment.getShippingAddress().getCity());
            npvs.addEncoded("SHIPTOSTATE", payment.getShippingAddress().getStateCode());
            npvs.addEncoded("SHIPTOZIP", payment.getShippingAddress().getStateCode());
            npvs.addEncoded("SHIPTOCOUNTRY", payment.getShippingAddress().getCountryCode());
        }

        return npvs;
    }

    private Payment runTransaction(final String method, final Map<String, String> npvs, final Payment payment, final String txOperation) {

        payment.setTransactionOperation(txOperation);

        try {

            final Map<String, String> result = performHttpCall(method, npvs);

            if (isAckSuccess(result)) {
                payment.setTransactionRequestToken(result.get("TOKEN"));
                payment.setTransactionReferenceId(result.get("TRANSACTIONID"));
                payment.setTransactionAuthorizationCode(result.get("PAYERID"));
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                payment.setPaymentProcessorBatchSettlement(CAPTURE.equals(txOperation) || AUTH_CAPTURE.equals(txOperation));
            } else {
                payment.setTransactionReferenceId(UUID.randomUUID().toString());
                payment.setTransactionAuthorizationCode("");
                payment.setTransactionOperationResultCode(result.get("L_ERRORCODE0"));

                final StringBuilder msg = new StringBuilder();
                if (result.get("L_SHORTMESSAGE0") != null) {
                    msg.append(result.get("L_SHORTMESSAGE0"));
                }
                if (result.get("L_LONGMESSAGE0") != null) {
                    if (msg.length() > 0) {
                        msg.append(": ");
                    }
                    msg.append(result.get("L_LONGMESSAGE0"));
                }

                payment.setTransactionOperationResultMessage(msg.toString());
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                payment.setPaymentProcessorBatchSettlement(false);
            }
        } catch (Exception exp) {

            LOG.error(Markers.alert(), "PayPal Pro transaction [" + payment.getOrderNumber() + "] failed, cause: " + exp.getMessage(), exp);
            LOG.error("PayPal Pro transaction failed, payment: " + payment, exp);

            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode("");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setTransactionOperationResultMessage(exp.getMessage());
            payment.setPaymentProcessorBatchSettlement(false);

        }
        return payment;
    }


    /**
     * {@inheritDoc}
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {
        return getHtmlForm(cardHolderName, locale);
    }


    /**
     * Default implementation.
     *
     * @param cardHolderName card holder name.
     * @param locale locale
     *
     * @return html form.
     */
    protected String getHtmlForm(final String cardHolderName, final String locale) {
        String htmlForm = getParameterValue("htmlForm_" + locale);
        if (htmlForm == null) {
            htmlForm = getParameterValue("htmlForm");
        }
        if (htmlForm != null) {
            return htmlForm.replace("@CARDHOLDERNAME@", cardHolderName);
        }
        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final String operation, final Map parametersMap) {

        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(parametersMap);
        final Payment payment = new PaymentImpl();

        payment.setCardHolderName(params.get("ccHolderName"));
        payment.setCardNumber(params.get("ccNumber"));
        payment.setCardExpireMonth(params.get("ccExpireMonth"));
        payment.setCardExpireYear(params.get("ccExpireYear"));
        payment.setCardCvv2Code(params.get("ccSecCode"));
        payment.setCardType(params.get("ccType"));
        payment.setShopperIpAddress(params.get(PaymentMiscParam.CLIENT_IP));

        if (LOG.isDebugEnabled()) {
            LOG.debug(HttpParamsUtils.stringify("Payment prototype from map", parametersMap));
        }


        return payment;
    }

}
