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

import com.liqpay.LiqPay;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.util.HttpParamsUtils;
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * LiqPay payment gateway implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 12:53 PM
 */
public class LiqPayPaymentGatewayImpl extends AbstractLiqPayPaymentGatewayImpl
        implements PaymentGatewayExternalForm {

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, true,
            true, true, true,
            null,
            false, false
    );

    // merchant id
    static final String LP_MERCHANT_ID = "LP_MERCHANT_ID";

    // key
    static final String LP_MERCHANT_KEY = "LP_MERCHANT_KEY";

    // result_url  shopper will be redirected to
    static final String LP_RESULT_URL = "LP_RESULT_URL";

    // server_url back url for server - server communications
    static final String LP_SERVER_URL = "LP_SERVER_URL";

    // form post acton url
    static final String LP_POST_URL = "LP_POST_URL";

    // payment way
    static final String LP_PAYWAY_URL = "LP_PAYWAY_URL";


    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        final String url = getParameterValue(LP_POST_URL);
        if (url.endsWith("/")) {
            return url + "pay";
        }
        return url + "/pay";
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

        if (privateCallBackParameters != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = params.get("amount");
            final String currency = params.get("currency");
            final String description = params.get("description");
            final String order_id = params.get("order_id");
            final String type = params.get("type");
            final String sender_phone = params.get("sender_phone");
            final String status = params.get("status");
            final String transaction_id = params.get("transaction_id");
            final String signature = params.get("signature");

            final String validSignature = api.str_to_sign(privateKey +
                    amount  +
                    currency +
                    publicKey +
                    order_id +
                    type +
                    description  +
                    status +
                    transaction_id +
                    sender_phone);

            if (signature.equals(validSignature)) {
                return order_id;
            }

        }

        return null;

    }

    /**
     * {@inheritDoc}
     */
    public CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult) {

        String statusRes = null;

        if (callbackResult != null) {

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = callbackResult.get("amount");
            final String currency = callbackResult.get("currency");
            final String description = callbackResult.get("description");
            final String order_id = callbackResult.get("order_id");
            final String type = callbackResult.get("type");
            final String sender_phone = callbackResult.get("sender_phone");
            final String status = callbackResult.get("status");
            final String transaction_id = callbackResult.get("transaction_id");
            final String signature = callbackResult.get("signature");

            final String validSignature = api.str_to_sign(privateKey +
                    amount  +
                    currency +
                    publicKey +
                    order_id +
                    type +
                    description  +
                    status +
                    transaction_id +
                    sender_phone);

            if (signature.equals(validSignature)) {
                statusRes = status;
            }

        }


        final boolean success = statusRes != null &&
                ("success".equalsIgnoreCase(statusRes)
                  || "wait_secure".equalsIgnoreCase(statusRes)
                  || "sandbox".equalsIgnoreCase(statusRes));


        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug(HttpParamsUtils.stringify("LiqPay callback", callbackResult));
        }

        if (success) {
            if ("wait_secure".equalsIgnoreCase(statusRes)) {
                return CallbackResult.UNSETTLED;
            }
            return CallbackResult.OK;
        }
        return CallbackResult.FAILED;

    }


    /**
     * {@inheritDoc}
     */
    public void handleNotification(HttpServletRequest request, HttpServletResponse response) {

    }


    private LiqPay getLiqPayAPI() {
        return new LiqPay(getParameterValue(LP_MERCHANT_ID), getParameterValue(LP_MERCHANT_KEY), getParameterValue(LP_POST_URL));
    }

    /**
     * {@inheritDoc}
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        final LiqPay api = getLiqPayAPI();

        final HashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        params.put("server_url", getParameterValue(LP_SERVER_URL));
        params.put("description", getDescription(payment));
        params.put("pay_way", getParameterValue(LP_PAYWAY_URL));
        params.put("result_url", getParameterValue(LP_RESULT_URL));

        params.put("type", "buy");
        params.put("order_id", orderGuid);

        params.put("currency", currencyCode);
        params.put("formDataOnly", "formDataOnly"); // YC specific

        return api.cnb_form(params);

    }

    /**
     * Get order description.
     *
     * @param payment payment
     * @return order description.
     */
    private String getDescription(final Payment payment) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (PaymentLine line : payment.getOrderItems()) {
            if (line.isShipment()) {
                stringBuilder.append(line.getSkuName().replace("\"","")).append(", ");
            } else {
                stringBuilder.append(line.getSkuCode().replace("\"",""));
                stringBuilder.append(" x ");
                stringBuilder.append(line.getQuantity());
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(payment.getBillingEmail());
        stringBuilder.append(", ");
        stringBuilder.append(payment.getOrderNumber());
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Shipment not included. Will be added at capture operation.
     */
    public Payment authorize(final Payment paymentIn) {
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
    public Payment reverseAuthorization(final Payment paymentIn) {
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
    public Payment capture(final Payment paymentIn) {
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
    public Payment voidCapture(final Payment paymentIn) {
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
    public Payment refund(final Payment payment) {

        final LiqPay api = getLiqPayAPI();

        final HashMap params = new HashMap();
        params.put("order_id", payment.getTransactionAuthorizationCode()); // this is populated in prototype when we capture

        boolean success = false;
        try {
            final HashMap res = api.api("payment/refund", params);
            success = "ok".equals(res.get("result"));
        } catch (Exception exp) {
            final Logger log = ShopCodeContext.getLog(this);
            log.error("LiqPayPaymentGatewayImpl#refund failed for {}", payment.getOrderNumber());
            log.error("LiqPayPaymentGatewayImpl#refund failed cause:", exp);
        }
        payment.setTransactionOperation(REFUND);
        payment.setPaymentProcessorResult(success ? Payment.PAYMENT_STATUS_OK : Payment.PAYMENT_STATUS_FAILED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final String operation, final Map map) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> singleParamMap = HttpParamsUtils.createSingleValueMap(map);

        payment.setPaymentAmount(new BigDecimal(singleParamMap.get("amount")));
        payment.setOrderCurrency(singleParamMap.get("currency"));
        payment.setTransactionReferenceId(singleParamMap.get("transaction_id"));
        payment.setTransactionAuthorizationCode(singleParamMap.get("order_id")); // this is order guid - we need it for refunds

        final CallbackResult res = getExternalCallbackResult(singleParamMap);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());

        payment.setShopperIpAddress(singleParamMap.get(PaymentMiscParam.CLIENT_IP));

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "liqPayPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

}
