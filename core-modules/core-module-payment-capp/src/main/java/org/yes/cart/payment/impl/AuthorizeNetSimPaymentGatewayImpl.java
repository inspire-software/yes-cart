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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AuthorizeNetSimPaymentGatewayImpl extends AbstractAuthorizeNetPaymentGatewayImpl implements PaymentGatewayExternalForm {


    private static final String ORDER_GUID = "orderGuid";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, true,
            true, false,
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
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get(ORDER_GUID));
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
    public boolean isSuccess(final Map<String, String> nvpCallResult) {
        return true; //nothing to do
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
        stringBuilder.append(getHiddenFiled("x_login", apiLoginId));
        stringBuilder.append(getHiddenFiled("x_fp_sequence", x_fp_sequence));
        stringBuilder.append(getHiddenFiled("x_fp_timestamp", x_fp_timestamp));
        stringBuilder.append(getHiddenFiled("x_fp_hash", x_fp_hash));
        stringBuilder.append(getHiddenFiled("x_version", "3.1"));
        stringBuilder.append(getHiddenFiled("x_method", "CC"));
        stringBuilder.append(getHiddenFiled("x_type", "AUTH_CAPTURE"));
        stringBuilder.append(getHiddenFiled("x_amount", amountString));
        stringBuilder.append(getHiddenFiled("x_show_form", "payment_form"));
        stringBuilder.append(getHiddenFiled("x_test_request", getParameterValue(AN_TEST_REQUEST)));


        //not mandatory parameters
        stringBuilder.append(getHiddenFiled("x_invoice_num", StringUtils.substring(orderGuid.replace("-", ""), 20))); // limit to 20 chast lenght
        stringBuilder.append(getHiddenFiled("x_description", StringUtils.defaultString(getParameterValue(AN_DESCRIPTION))));


        stringBuilder.append(getHiddenFiled("x_relay_response", "TRUE"));


        stringBuilder.append(getHiddenFiled(ORDER_GUID, orderGuid));  // this will be bypassed via payment gateway to restore it latter


        return stringBuilder.toString();
    }


    /**
     * Process public call back request from payment gateway.
     *
     * @param privateCallBackParameters get/post parameters
     * @return true in case in payment was ok, false in case if payment failed
     */
    public Payment createPaymentPrototype(final Map privateCallBackParameters) {
        final Payment payment = new PaymentImpl();

        payment.setTransactionReferenceId(AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_trans_id")));
        payment.setTransactionAuthorizationCode(AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_auth_code")));


        String responseCode = AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_response_code"));
        if ("1".equalsIgnoreCase(responseCode)) {
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        } else {
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
        }
        payment.setTransactionOperationResultCode(responseCode);
        payment.setTransactionOperationResultMessage(
                AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_response_reason_code"))
                        + " "
                        + AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_response_reason_text"))
        );
        payment.setCardNumber(AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get("x_account_number")));
        payment.setShopperIpAddress(getSingleValue(privateCallBackParameters.get(PaymentMiscParam.CLIENT_IP)));

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
