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

import net.authorize.sim.Fingerprint;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.*;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.utils.HttpParamsUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AuthorizeNetSimPaymentGatewayImpl extends AbstractAuthorizeNetPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizeNetSimPaymentGatewayImpl.class);

    /**
     * merchant defined MD5 Hash key
     */
    protected static final String AN_MD5_HASH_KEY = "MD5_HASH_KEY";

    /**
     * SIM/DPM relay response URL
     */
    protected static final String AN_RELAY_RESPONSE_URL = "RELAY_RESPONSE_URL";
    /**
     * SIM/DPM order receipt URL
     */
    protected static final String AN_ORDER_RECEIPT_URL = "ORDER_RECEIPT_URL";

    /**
     * SIM post URL
     */
    protected static final String AN_POST_URL = "POST_URL";
    /**
     * SIM Cancel URL
     */
    protected static final String AN_CANCEL_URL = "CANCEL_URL";
    /**
     * SIM Cancel URL
     */
    protected static final String AN_RETURN_POLICY_URL = "RETURN_POLICY_URL";

    /**
     * SIM test request. Used on production env, if need to send test request.
     */
    protected static final String AN_TEST_REQUEST = "TEST_REQUEST";

    // Styling
    protected static final String AN_STYLE_HEADER_HTML = "STYLE_HEADER_HTML";
    protected static final String AN_STYLE_HEADER2_HTML = "STYLE_HEADER2_HTML";
    protected static final String AN_STYLE_FOOTER_HTML = "STYLE_FOOTER_HTML";
    protected static final String AN_STYLE_FOOTER2_HTML = "STYLE_FOOTER2_HTML";
    protected static final String AN_STYLE_BGCOLOR = "STYLE_BGCOLOR";
    protected static final String AN_STYLE_LINKCOLOR = "STYLE_LINKCOLOR";
    protected static final String AN_STYLE_TEXTCOLOR = "STYLE_TEXTCOLOR";
    protected static final String AN_STYLE_LOGOURL = "STYLE_LOGOURL";
    protected static final String AN_STYLE_BGURL = "STYLE_BGURL";
    protected static final String AN_STYLE_FONTFAMILY = "STYLE_FONTFAMILY";
    protected static final String AN_STYLE_FONTSIZE = "STYLE_FONTSIZE";
    protected static final String AN_STYLE_SECTION1COLOR = "STYLE_SECTION1COLOR";
    protected static final String AN_STYLE_SECTION1FONTFAMILY = "STYLE_SECTION1FONTFAMILY";
    protected static final String AN_STYLE_SECTION1FONTSIZE = "STYLE_SECTION1FONTSIZE";


    private static final String ORDER_GUID = "orderGuid";

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, true,
            null ,
            false, false
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }

    /**
     * {@inheritDoc}
     * Values https://test.authorize.net/gateway/transact.dll test env
     * https://secure.authorize.net/gateway/transact.dll production
     */
    @Override
    public String getPostActionUrl() {
        return getParameterValue(AN_POST_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {
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
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if (PaymentGateway.REFUND_NOTIFY.equals(processorOperation) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            if (params.get("x_trans_id") != null) {
                payment.setTransactionReferenceId(params.get("x_trans_id"));
            }
            if (params.get("x_auth_code") != null) {
                payment.setTransactionAuthorizationCode(params.get("x_auth_code"));
            }
            if (params.get("x_response_code") != null) {
                payment.setTransactionOperationResultCode(params.get("x_response_code"));
            }
            if (params.get("x_response_reason_code") != null || params.get("x_response_reason_text") != null) {
                payment.setTransactionOperationResultMessage(
                        params.get("x_response_reason_code")
                                + " "
                                + params.get("x_response_reason_text")
                );
            }
            if (params.get("x_account_number") != null) {
                payment.setCardNumber(params.get("x_account_number"));
            }

        }

    }

    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if ((PaymentGateway.REFUND.equals(processorOperation) || PaymentGateway.VOID_CAPTURE.equals(processorOperation)) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            if (params.get("x_trans_id") != null) {
                payment.setTransactionReferenceId(params.get("x_trans_id"));
            }
            if (params.get("x_auth_code") != null) {
                payment.setTransactionAuthorizationCode(params.get("x_auth_code"));
            }
            if (params.get("x_response_code") != null) {
                payment.setTransactionOperationResultCode(params.get("x_response_code"));
            }
            if (params.get("x_response_reason_code") != null || params.get("x_response_reason_text") != null) {
                payment.setTransactionOperationResultMessage(
                        params.get("x_response_reason_code")
                                + " "
                                + params.get("x_response_reason_text")
                );
            }
            if (payment.getTransactionOperationResultMessage() != null) {
                payment.setTransactionOperationResultMessage(
                        payment.getTransactionOperationResultMessage() + ". Amount: " + callback.getAmount().toPlainString());
            } else {
                payment.setTransactionOperationResultMessage("Amount: " + callback.getAmount().toPlainString());
            }
            if (params.get("x_account_number") != null) {
                payment.setCardNumber(params.get("x_account_number"));
            }

            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

        }

    }

    protected boolean isValid(final Map privateCallBackParameters) {

        final Map<String, String[]> params = new HashMap<>();
        for (final Map.Entry param : (Set<Map.Entry>) privateCallBackParameters.entrySet()) {
            if (param.getValue() instanceof String[]) {
                params.put(String.valueOf(param.getKey()), (String[]) param.getValue());
            } else {
                params.put(String.valueOf(param.getKey()), new String[] { String.valueOf(param.getValue()) });
            }
        }

        net.authorize.sim.Result txResult = net.authorize.sim.Result.createResult(
                getParameterValue(AN_API_LOGIN_ID),
                getParameterValue(AN_MD5_HASH_KEY),
                params);

        return txResult.isAuthorizeNet();

    }

    protected String md5sign(final String txId, final String amount) {

        final StringBuilder sign = new StringBuilder();
        sign.append(getParameterValue(AN_MD5_HASH_KEY));
        sign.append(getParameterValue(AN_API_LOGIN_ID));
        sign.append(txId);
        sign.append(amount);
        sign.append("EUR");

        try {
            final Charset charset = StandardCharsets.UTF_8;
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(digest.digest(sign.toString().getBytes(charset)))).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("MD5 not available", e);
            return "MD5 not available";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters, final boolean forceProcessing) {

        final boolean valid = isValid(privateCallBackParameters);

        if (valid || forceProcessing) {

            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }

            final String xType = HttpParamsUtils.getSingleValue(privateCallBackParameters.get("x_type"));
            CallbackOperation op = CallbackOperation.PAYMENT;
            if ("CREDIT".equalsIgnoreCase(xType) || "VOID".equalsIgnoreCase(xType)) {
                op = CallbackOperation.REFUND;
            } else if ("PRIOR_AUTH_CAPTURE".equalsIgnoreCase(xType)) {
                op = CallbackOperation.INFO;
            }

            BigDecimal callbackAmount = null;
            try {
                callbackAmount = new BigDecimal(HttpParamsUtils.getSingleValue(privateCallBackParameters.get("x_amount")));
            } catch (Exception exp) {
                LOG.error("Callback for {} did not have a valid amount {}",
                        privateCallBackParameters.get("orderGuid"), privateCallBackParameters.get("x_amount"));
            }


            return new BasicCallbackInfoImpl(
                    HttpParamsUtils.getSingleValue(privateCallBackParameters.get(ORDER_GUID)),
                    op,
                    callbackAmount,
                    privateCallBackParameters,
                    valid
            );
        } else {
            LOG.debug("Signature is not valid");
        }
        return new BasicCallbackInfoImpl(
                null,
                CallbackOperation.INVALID,
                null,
                privateCallBackParameters,
                valid
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

        /*
           See http://developer.authorize.net/guides/SIM/wwhelp/wwhimpl/js/html/wwhelp.htm#href=SIM_Trans_response.09.2.html

           1—Approved
           2—Declined
           3—Error
           4—Held for Review
        */
        String responseCode = null;

        final boolean valid = isValid(callbackResult);

        if (valid || forceProcessing) {
            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }
            responseCode = callbackResult.get("x_response_code");
        } else {
            LOG.debug("Signature is not valid");
        }
        if ("1".equals(responseCode)) {
            LOG.debug("Payment result is {}: {}", responseCode, CallbackAware.CallbackResult.OK);
            return CallbackAware.CallbackResult.OK;
        } else if ("4".equals(responseCode)) {
            LOG.debug("Payment result is {}: {}", responseCode, CallbackAware.CallbackResult.PROCESSING);
            return CallbackAware.CallbackResult.PROCESSING;
        } else {
            LOG.debug("Payment result is {}: {}", responseCode, CallbackAware.CallbackResult.FAILED);
            return CallbackAware.CallbackResult.FAILED;
        }
    }


    private static final int ITEMSKU = 30;
    private static final int ITEMNAME = 30;

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {

        final String apiLoginId = getParameterValue(AN_API_LOGIN_ID);
        final String key = getParameterValue(AN_TRANSACTION_KEY);
        final String amountString = "" + amount;

        final Fingerprint fingerprint = getFingerprint(orderReference, apiLoginId, key, amountString, currencyCode);
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
        stringBuilder.append(getHiddenField("x_currency_code", currencyCode));
        stringBuilder.append(getHiddenField("x_show_form", "payment_form"));
        setParameterIfNotNull(stringBuilder, "x_test_request", AN_TEST_REQUEST);

        if (CollectionUtils.isNotEmpty(payment.getOrderItems())) {
            for (final PaymentLine line : payment.getOrderItems()) {
                if (line.isShipment()) {
                    // <INPUT TYPE="HIDDEN" name="x_freight" VALUE="Freight1<|>ground overnight<|>12.95>
                    String name = line.getSkuName().length() > ITEMNAME ? line.getSkuName().substring(0, ITEMNAME - 1) + "~" : line.getSkuName();
                    setValueIfNotNull(stringBuilder, "x_freight",
                            name + "<|>" + name + "<|>" +  line.getUnitPrice().toPlainString());
                } else {
                    // <INPUT TYPE="HIDDEN" name="x_freight" VALUE="Item ID<|>item name<|>item description<|>item quantity<|>item price (unit cost)<|>item taxable
                    String name = line.getSkuName().length() > ITEMNAME ? line.getSkuName().substring(0, ITEMNAME - 1) + "~" : line.getSkuName();
                    String code = line.getSkuCode().length() > ITEMSKU ? line.getSkuCode().substring(0, ITEMSKU - 1) + "~" : line.getSkuCode();
                    setValueIfNotNull(stringBuilder, "x_line_item",
                            code + "<|>" + name + "<|><|>" + line.getQuantity().toPlainString() + "<|>" +  line.getUnitPrice().toPlainString() + "<|>0");
                }
            }
        }

        // TODO: tax exempt and duty
        //
// x_tax seems to be relating to shipping only
//        if (MoneyUtils.isPositive(payment.getTaxAmount())) {
//            // <INPUT TYPE="HIDDEN" name="x_tax" VALUE="Tax1<|>state tax<|>0.0625">
//            setValueIfNotNull(stringBuilder, "x_tax",
//                    "Tax<|>tax amount<|>" +  payment.getTaxAmount().toPlainString());
//        }
        // <INPUT TYPE="HIDDEN" name="x_duty" VALUE="Duty1<|>export<|> 15.00>
        // x_tax_exempt

        final PaymentAddress address = payment.getBillingAddress();
        if (address != null) {
            setValueIfNotNull(stringBuilder, "x_first_name", address.getFirstname());
            setValueIfNotNull(stringBuilder, "x_last_name", address.getLastname());
            setValueIfNotNull(stringBuilder, "x_address", getAddressLines(address));
            setValueIfNotNull(stringBuilder, "x_city", address.getCity());
            setValueIfNotNull(stringBuilder, "x_state", address.getStateCode());
            setValueIfNotNull(stringBuilder, "x_zip", address.getPostcode());
            setValueIfNotNull(stringBuilder, "x_country", address.getCountryCode());
            setValueIfNotNull(stringBuilder, "x_phone", address.getPhone1());
            setValueIfNotNull(stringBuilder, "x_email", payment.getBillingEmail());
        }
        stringBuilder.append(getHiddenField("x_cust_id", payment.getBillingEmail()));

        final PaymentAddress shipTo = payment.getShippingAddress();
        if (shipTo != null) {
            setValueIfNotNull(stringBuilder, "x_ship_to_first_name", shipTo.getFirstname());
            setValueIfNotNull(stringBuilder, "x_ship_to_last_name", shipTo.getLastname());
            setValueIfNotNull(stringBuilder, "x_ship_to_address", getAddressLines(shipTo));
            setValueIfNotNull(stringBuilder, "x_ship_to_city", shipTo.getCity());
            setValueIfNotNull(stringBuilder, "x_ship_to_state", shipTo.getStateCode());
            setValueIfNotNull(stringBuilder, "x_ship_to_zip", shipTo.getPostcode());
            setValueIfNotNull(stringBuilder, "x_ship_to_country", shipTo.getCountryCode());
        }

        //not mandatory parameters
        stringBuilder.append(getHiddenField("x_invoice_num", StringUtils.substring(orderReference.replace("-", ""), 0, 20))); // limit to 20 chars length
        stringBuilder.append(getHiddenField("x_po_num", orderReference));
        stringBuilder.append(getHiddenField("x_description", getDescription(payment)));

        final String relayUrl = getParameterValue(AN_RELAY_RESPONSE_URL);
        if (StringUtils.isNotBlank(relayUrl)) {
            stringBuilder.append(getHiddenField("x_relay_url", relayUrl));
            stringBuilder.append(getHiddenField("x_relay_response", "TRUE"));
        } else {
            setParameterIfNotNull(stringBuilder, "x_receipt_link_url", AN_ORDER_RECEIPT_URL);
        }

        // payment form configuration
        setParameterIfNotNull(stringBuilder, "x_cancel_url", AN_CANCEL_URL);
        setParameterIfNotNull(stringBuilder, "x_return_policy_url", AN_RETURN_POLICY_URL);

        stringBuilder.append(getHiddenField(ORDER_GUID, orderReference));  // this will be bypassed via payment gateway to restore it latter

        // styling
        setParameterIfNotNull(stringBuilder, "x_return_policy_url", AN_RETURN_POLICY_URL);
        setParameterIfNotNull(stringBuilder, "x_header_html_payment_form", AN_STYLE_HEADER_HTML);
        setParameterIfNotNull(stringBuilder, "x_header2_html_payment_form", AN_STYLE_HEADER2_HTML);
        setParameterIfNotNull(stringBuilder, "x_footer_html_payment_form", AN_STYLE_FOOTER_HTML);
        setParameterIfNotNull(stringBuilder, "x_footer2_html_payment_form", AN_STYLE_FOOTER2_HTML);
        setParameterIfNotNull(stringBuilder, "x_color_background", AN_STYLE_BGCOLOR);
        setParameterIfNotNull(stringBuilder, "x_color_link", AN_STYLE_LINKCOLOR);
        setParameterIfNotNull(stringBuilder, "x_color_text", AN_STYLE_TEXTCOLOR);
        setParameterIfNotNull(stringBuilder, "x_logo_url", AN_STYLE_LOGOURL);
        setParameterIfNotNull(stringBuilder, "x_background_url", AN_STYLE_BGURL);
        setParameterIfNotNull(stringBuilder, "x_font_family", AN_STYLE_FONTFAMILY);
        setParameterIfNotNull(stringBuilder, "x_font_size", AN_STYLE_FONTSIZE);
        setParameterIfNotNull(stringBuilder, "x_sectionhead1_color_text", AN_STYLE_SECTION1COLOR);
        setParameterIfNotNull(stringBuilder, "x_sectionhead1_font_family", AN_STYLE_SECTION1FONTFAMILY);
        setParameterIfNotNull(stringBuilder, "x_sectionhead1_font_size", AN_STYLE_SECTION1FONTSIZE);

        return stringBuilder.toString();
    }

    protected Fingerprint getFingerprint(final String orderReference,
                                         final String apiLoginId,
                                         final String txKey,
                                         final String amountString,
                                         final String currency) {
        final Random rnd = new Random(System.currentTimeMillis());
        return Fingerprint.createFingerprint(
                    apiLoginId,
                    txKey,
                    rnd.nextInt(99999999),
                    amountString,
                    currency
            );
    }

    private void setValueIfNotNull(final StringBuilder params, final String key, final String value) {
        if (StringUtils.isNotBlank(value)) {
            params.append(getHiddenField(key, value));
        }
    }

    private void setParameterIfNotNull(final StringBuilder params, final String key, final String valueKey) {
        final String value = getParameterValue(valueKey);
        if (StringUtils.isNotBlank(value)) {
            params.append(getHiddenField(key, value));
        }
    }

    private String getAddressLines(final PaymentAddress address) {
        final StringBuilder address1 = new StringBuilder();
        if (StringUtils.isNotBlank(address.getAddrline1())) {
            address1.append(address.getAddrline1());
        }
        if (StringUtils.isNotBlank(address.getAddrline2())) {
            if (address1.length() > 0) {
                address1.append(", ");
            }
            address1.append(address.getAddrline2());
        }
        return address1.toString();
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
    @Override
    public Payment createPaymentPrototype(final String operation,
                                          final Map privateCallBackParameters,
                                          boolean forceProcessing) {

        final Payment payment = new PaymentImpl();

        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);

        payment.setTransactionReferenceId(params.get("x_trans_id"));
        payment.setTransactionAuthorizationCode(params.get("x_auth_code"));

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(privateCallBackParameters);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(params, forceProcessing);

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
