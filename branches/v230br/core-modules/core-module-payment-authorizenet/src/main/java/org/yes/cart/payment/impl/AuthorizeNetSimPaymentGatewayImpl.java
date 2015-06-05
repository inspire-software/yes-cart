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

import net.authorize.sim.Fingerprint;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.util.HttpParamsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AuthorizeNetSimPaymentGatewayImpl extends AbstractAuthorizeNetPaymentGatewayImpl implements PaymentGatewayExternalForm {


    private static final String ORDER_GUID = "orderGuid";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, false,
            null ,
            false, false
    );


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "authorizeNetSimPaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     * Values https://test.authorize.net/gateway/transact.dll test env
     * https://secure.authorize.net/gateway/transact.dll production
     */
    public String getPostActionUrl() {
        return getParameterValue(AN_POST_URL);
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
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
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
    public Payment refund(final Payment paymentIn) {
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
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return HttpParamsUtils.getSingleValue(privateCallBackParameters.get(ORDER_GUID));
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode) throws IOException {
        return Collections.EMPTY_MAP; //nothing to do
    }


     /**
     * {@inheritDoc}
     */
    public Map<String, String> doDoExpressCheckoutPayment(final String token, final String payerId,
                                                          final BigDecimal amount, final String currencyCode) throws IOException {
        return null;//nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getExpressCheckoutDetails(final String token) throws IOException {
        return null; //nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult) {
        /*
           See http://developer.authorize.net/guides/SIM/wwhelp/wwhimpl/js/html/wwhelp.htm#href=SIM_Trans_response.09.2.html

           1—Approved
           2—Declined
           3—Error
           4—Held for Review
        */
        String responseCode = callbackResult.get("x_response_code");
        if ("1".equals(responseCode)) {
            return CallbackResult.OK;
        } else if ("4".equals(responseCode)) {
            return CallbackResult.PROCESSING;
        } else {
            return CallbackResult.FAILED;
        }
    }

    /** {@inheritDoc} */
    public void handleNotification(final HttpServletRequest request, final HttpServletResponse response) {
        //nothing to do
    }


    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        final String apiLoginId = getParameterValue(AN_API_LOGIN_ID);
        final String amountString = "" + amount;


        final Random rnd = new Random(new Date().getTime());
        final Fingerprint fingerprint = Fingerprint.createFingerprint(
                apiLoginId,
                getParameterValue(AN_TRANSACTION_KEY),
                rnd.nextInt(99999999),
                amountString
        );
        final long x_fp_sequence = fingerprint.getSequence();
        final long x_fp_timestamp = fingerprint.getTimeStamp();
        final String x_fp_hash = fingerprint.getFingerprintHash();


        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHiddenField("x_login", apiLoginId));
        stringBuilder.append(getHiddenField("x_fp_sequence", x_fp_sequence));
        stringBuilder.append(getHiddenField("x_fp_timestamp", x_fp_timestamp));
        stringBuilder.append(getHiddenField("x_fp_hash", x_fp_hash));
        stringBuilder.append(getHiddenField("x_version", "3.1"));
        stringBuilder.append(getHiddenField("x_method", "CC"));
        stringBuilder.append(getHiddenField("x_type", "AUTH_CAPTURE"));
        stringBuilder.append(getHiddenField("x_amount", amountString));
        stringBuilder.append(getHiddenField("x_show_form", "payment_form"));
        stringBuilder.append(getHiddenField("x_test_request", getParameterValue(AN_TEST_REQUEST)));


        //not mandatory parameters
        stringBuilder.append(getHiddenField("x_invoice_num", StringUtils.substring(orderGuid.replace("-", ""), 20))); // limit to 20 chast lenght
        stringBuilder.append(getHiddenField("x_description", StringUtils.defaultString(getParameterValue(AN_DESCRIPTION))));


        stringBuilder.append(getHiddenField("x_relay_response", "TRUE"));


        stringBuilder.append(getHiddenField(ORDER_GUID, orderGuid));  // this will be bypassed via payment gateway to restore it latter


        return stringBuilder.toString();
    }


    /**
     * Process public call back request from payment gateway.
     *
     *
     * @param operation
     * @param privateCallBackParameters get/post parameters
     * @return true in case in payment was ok, false in case if payment failed
     */
    public Payment createPaymentPrototype(final String operation, final Map privateCallBackParameters) {

        final Payment payment = new PaymentImpl();

        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);

        payment.setTransactionReferenceId(params.get("x_trans_id"));
        payment.setTransactionAuthorizationCode(params.get("x_auth_code"));

        final CallbackResult res = getExternalCallbackResult(params);

        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());
        payment.setTransactionOperationResultCode(params.get("x_response_code"));
        payment.setTransactionOperationResultMessage(
                params.get("x_response_reason_code")
                        + " "
                        + params.get("x_response_reason_text")
        );
        payment.setCardNumber(params.get("x_account_number"));
        payment.setShopperIpAddress(params.get(PaymentMiscParam.CLIENT_IP));

        return payment;
    }

    /*

changeCurrencyCmd/USD
payLink/x
paymentGateway/authorizeNetSimPaymentGatewayLabel

      ***x_account_number/XXXX1111
      x_address/address%20line%201
      x_amount/39.45
      ***x_auth_code/TAB8M6
      *
      x_avs_code/Y

      x_card_type/Visa
      x_cavv_response/2
      x_city/Vancouver
      x_company/None
      x_country/Canada
      x_duty/0.00
      x_email/iazarny@yahoo.com
      x_fax/321
      x_first_name/Igor
      x_freight/0.00
      x_last_name/Azarny
      x_MD5_Hash/E6096D43288C104A9C6697A76AA88A03
      x_method/CC
      x_phone/654

      ***x_response_code/1
      ***x_response_reason_code/1
      ***x_response_reason_text/This%20transaction%20has%20been%20approved.

      x_ship_to_address/address%20line%201
      x_ship_to_city/Vancouver
      x_ship_to_company/None
      x_ship_to_country/Canada
      x_ship_to_first_name/Igor
      x_ship_to_last_name/Azarny
      x_ship_to_state/BC
      x_ship_to_zip/D2E123

      x_state/BC
      x_tax/0.00
      x_tax_exempt/FALSE
      x_test_request/false
      ***x_trans_id/2158240454
      x_type/auth_capture
      x_zip/D2E123

    */
}
