/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * LiqPay payment gateway implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 12:53 PM
 */
public class LiqPayPaymentGatewayImpl extends AbstractGswmPaymentGatewayImpl
        implements PaymentGatewayExternalForm {

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, true, true,
            true, true,
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

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = (String) privateCallBackParameters.get("amount");
            final String currency = (String) privateCallBackParameters.get("currency");
            final String description = (String) privateCallBackParameters.get("description");
            final String order_id = (String) privateCallBackParameters.get("order_id");
            final String type = (String) privateCallBackParameters.get("type");
            final String sender_phone = (String) privateCallBackParameters.get("sender_phone");
            final String status = (String) privateCallBackParameters.get("status");
            final String transaction_id = (String) privateCallBackParameters.get("transaction_id");
            final String signature = (String) privateCallBackParameters.get("signature");

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
    public boolean isSuccess(final Map<String, String> liqPayCallResult) {

        String statusRes = null;

        if (liqPayCallResult != null) {

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = liqPayCallResult.get("amount");
            final String currency = liqPayCallResult.get("currency");
            final String description = liqPayCallResult.get("description");
            final String order_id = liqPayCallResult.get("order_id");
            final String type = liqPayCallResult.get("type");
            final String sender_phone = liqPayCallResult.get("sender_phone");
            final String status = liqPayCallResult.get("status");
            final String transaction_id = liqPayCallResult.get("transaction_id");
            final String signature = liqPayCallResult.get("signature");

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


        ShopCodeContext.getLog(this).info("LiqPayPaymentGatewayImpl#isSuccess {}, {}", statusRes, liqPayCallResult);

        return success;

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

        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        params.put("currency", currencyCode);
        params.put("description", getDescription(payment));
        params.put("order_id", payment.getOrderNumber());
        params.put("result_url", getParameterValue(LP_RESULT_URL));
        params.put("server_url", getParameterValue(LP_SERVER_URL));
        params.put("type", "buy");
        params.put("pay_way", getParameterValue(LP_PAYWAY_URL));
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
            stringBuilder.append(line.getSkuCode().replace("\"","&quot;"));
            stringBuilder.append(" x ");
            stringBuilder.append(line.getQuantity());
            stringBuilder.append(", ");
        }
        stringBuilder.append(payment.getBillingEmail());
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
        return (Payment) SerializationUtils.clone(paymentIn);
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
        payment.setTransactionOperation(CAPTURE);
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

        final LiqPay api = getLiqPayAPI();

        final HashMap params = new HashMap();
        params.put("order_id", payment.getOrderNumber());

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
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map map) {
        final Payment payment = new PaymentImpl();
        payment.setShopperIpAddress(getSingleValue(map.get(PaymentMiscParam.CLIENT_IP)));
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
