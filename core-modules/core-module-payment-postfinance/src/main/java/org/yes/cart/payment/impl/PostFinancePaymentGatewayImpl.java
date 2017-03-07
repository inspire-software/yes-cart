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
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.service.payment.PaymentLocaleTranslator;
import org.yes.cart.service.payment.impl.PaymentLocaleTranslatorImpl;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.HttpParamsUtils;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 07/10/2015
 * Time: 14:47
 */
public class PostFinancePaymentGatewayImpl extends AbstractPostFinancePaymentGatewayImpl
        implements PaymentGatewayExternalForm {

    private static final Logger LOG = LoggerFactory.getLogger(PostFinancePaymentGatewayImpl.class);

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true,
            null,
            false, false
    );


    // form post acton url
    static final String PF_POST_URL = "PF_POST_URL";
    // result_url  shopper will be redirected to
    static final String PF_RESULT_URL_HOME = "PF_RESULT_URL_HOME";
    static final String PF_RESULT_URL_CATALOG = "PF_RESULT_URL_CATALOG";
    static final String PF_RESULT_URL_ACCEPT = "PF_RESULT_URL_ACCEPT";
    static final String PF_RESULT_URL_DECLINE = "PF_RESULT_URL_DECLINE";
    static final String PF_RESULT_URL_EXCEPTION = "PF_RESULT_URL_EXCEPTION";
    static final String PF_RESULT_URL_CANCEL = "PF_RESULT_URL_CANCEL";

    // Affiliation name in PostFinance system
    static final String PF_PSPID = "PF_PSPID";

    // SHA-1 signature hash
    static final String PF_SHA_IN = "PF_SHA_IN";
    static final String PF_SHA_OUT = "PF_SHA_OUT";

    // Styling
    static final String PF_STYLE_TITLE = "PF_STYLE_TITLE";
    static final String PF_STYLE_BGCOLOR = "PF_STYLE_BGCOLOR";
    static final String PF_STYLE_TXTCOLOR = "PF_STYLE_TXTCOLOR";
    static final String PF_STYLE_TBLBGCOLOR = "PF_STYLE_TBLBGCOLOR";
    static final String PF_STYLE_TBLTXTCOLOR = "PF_STYLE_TBLTXTCOLOR";
    static final String PF_STYLE_BUTTONBGCOLOR = "PF_STYLE_BUTTONBGCOLOR";
    static final String PF_STYLE_BUTTONTXTCOLOR = "PF_STYLE_BUTTONTXTCOLOR";
    static final String PF_STYLE_FONTTYPE = "PF_STYLE_FONTTYPE";
    static final String PF_STYLE_LOGO = "PF_STYLE_LOGO";
    static final String PF_STYLE_TP = "PF_STYLE_TP";

    // Payment method configuration
    static final String PF_PM = "PF_PM";
    static final String PF_BRAND = "PF_BRAND";
    static final String PF_WIN3DS = "PF_WIN3DS";
    static final String PF_PMLIST = "PF_PMLIST";
    static final String PF_EXCLPMLIST = "PF_EXCLPMLIST";
    static final String PF_PMLISTTYPE = "PF_PMLISTTYPE";

    static final String PF_ITEMISED = "PF_ITEMISED";

    private final PaymentLocaleTranslator paymentLocaleTranslator = new PaymentLocaleTranslatorImpl();

    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        final String url = getParameterValue(PF_POST_URL);
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public String getSubmitButton(final String locale) {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(final Map privateCallBackParameters) {

        if (privateCallBackParameters != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);
            final Map<String, String> sorted = new TreeMap<String, String>();
            final String signature = copyHttpParamsAndRemoveSignature(params, sorted);
            final String verify = sha1sign(sorted, getParameterValue(PF_SHA_OUT));
            if (verify.equals(signature)) {
                LOG.debug("Signature is valid");
                return sorted.get("ORDERID");
            } else {
                LOG.warn("Signature is not valid");
            }

        }

        return null;

    }

    protected String copyHttpParamsAndRemoveSignature(final Map<String, String> params, final Map<String, String> sorted) {
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            // need to recode keys to upper
            sorted.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        return sorted.remove("SHASIGN");
    }


    /**
     * {@inheritDoc}
     */
    public CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult) {

        String statusRes = null;

        if (callbackResult != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callbackResult);
            final Map<String, String> sorted = new TreeMap<String, String>();
            final String signature = copyHttpParamsAndRemoveSignature(params, sorted);
            final String verify = sha1sign(sorted, getParameterValue(PF_SHA_OUT));
            if (verify.equals(signature)) {
                LOG.debug("Signature is valid");
                statusRes = sorted.get("STATUS");
            } else {
                LOG.warn("Signature is not valid");
            }

        }

        final boolean success = statusRes != null &&
                ("5".equalsIgnoreCase(statusRes)
                        || "9".equalsIgnoreCase(statusRes)
                        || "51".equalsIgnoreCase(statusRes)
                        || "91".equalsIgnoreCase(statusRes));


        if (LOG.isDebugEnabled()) {
            LOG.debug(HttpParamsUtils.stringify("PostFinance callback", callbackResult));
        }

        if (success) {
            if ("51".equalsIgnoreCase(statusRes) || "91".equalsIgnoreCase(statusRes)) {
                return CallbackResult.UNSETTLED;
            }
            return CallbackResult.OK;
        }
        return CallbackResult.FAILED;

    }



    /**
     * {@inheritDoc}
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {


        // Parameters must be in apha order for hash
        final Map<String, String> params = new TreeMap<String, String>();

        // 1. general parameters

        // Your affiliation name in our system
        params.put("PSPID", getParameterValue(PF_PSPID));
        // Your unique order number (merchant reference). The system checks that a payment has not been requested twice
        // for the same order. The ORDERID has to be assigned dynamically.
        params.put("ORDERID", orderReference);
        // Amount to be paid MULTIPLIED BY 100 since the format of the amount must not contain any decimals or other separators.
        // The amount must be assigned dynamically.
        params.put("AMOUNT", amount.multiply(MoneyUtils.HUNDRED).setScale(0, RoundingMode.HALF_UP).toPlainString());
        // ISO alpha order currency code, for example: EUR, USD, GBP, CHF, ...
        params.put("CURRENCY", currencyCode);
        // Language of the customer, for example: en_US, nl_NL, fr_FR, ...
        params.put("LANGUAGE", paymentLocaleTranslator.translateLocale(this, locale));

        // 2. optional customer details, highly recommended for fraud prevention

        // Customer name. It will be pre-initialised (but still editable) in the cardholder name field of the credit card details.
        setValueIfNotNull(params, "CN", cardHolderName);
        // Customer’s e-mail address
        setValueIfNotNull(params, "EMAIL", payment.getBillingEmail());

        final PaymentAddress address = payment.getBillingAddress();
        if (address != null) {
            // Customer’s street name and number
            setValueIfNotNull(params, "OWNERADDRESS", getAddressLines(address));
            // Customer’s postcode
            setValueIfNotNull(params, "OWNERZIP", address.getPostcode());
            // Customer’s town/city name
            setValueIfNotNull(params, "OWNERTOWN", address.getCity());
            // Customer’s country
            setValueIfNotNull(params, "OWNERCTY", address.getCountryCode());
            // Customer’s telephone number
            setValueIfNotNull(params, "OWNERTELNO", address.getPhone1());
        }

        // Order description
        final boolean itemised = Boolean.valueOf(getParameterValue(PF_ITEMISED));
        params.put("COM", getDescription(payment, itemised));
        if (itemised) {
            populateItems(payment, params);
        }


        // 3. layout information: see Look & Feel of the Payment Page

        // Title and header of the page
        setParameterIfNotNull(params, "TITLE", PF_STYLE_TITLE);
        // Background colour
        setParameterIfNotNull(params, "BGCOLOR", PF_STYLE_BGCOLOR);
        // Text colour
        setParameterIfNotNull(params, "TXTCOLOR", PF_STYLE_TXTCOLOR);
        // Table background colour
        setParameterIfNotNull(params, "TBLBGCOLOR", PF_STYLE_TBLBGCOLOR);
        // Table text colour
        setParameterIfNotNull(params, "TBLTXTCOLOR", PF_STYLE_TBLTXTCOLOR);
        // Button background colour
        setParameterIfNotNull(params, "BUTTONBGCOLOR", PF_STYLE_BUTTONBGCOLOR);
        // Button text colour
        setParameterIfNotNull(params, "BUTTONTXTCOLOR", PF_STYLE_BUTTONTXTCOLOR);
        // Font family
        setParameterIfNotNull(params, "FONTTYPE", PF_STYLE_FONTTYPE);
        // URL/filename of the logo you want to display at the top of the payment page, next to the title.
        // The URL must be absolute (i.e. contain the full path), it cannot be relative.
        setParameterIfNotNull(params, "LOGO", PF_STYLE_LOGO);

        // 4. dynamic template page: see Look & Feel of the Payment Page
        setParameterIfNotNull(params, "TP", PF_STYLE_TP);

        // 5. payment methods/page specifics: see Payment method and payment page specifics
        // Payment method e.g. CreditCard, iDEAL or blank
        setParameterIfNotNull(params, "PM", PF_PM);
        // Credit card brand e.g. VISA or blank
        setParameterIfNotNull(params, "BRAND", PF_BRAND);
        // 3-D secure: MAINW or POPUP
        setParameterIfNotNull(params, "WIN3DS", PF_WIN3DS);
        // List of selected payment methods and/or credit card brands. Separated by a “;” (semicolon).  e.g. VISA;iDEAL
        setParameterIfNotNull(params, "PMLIST", PF_PMLIST);
        // List of payment methods and/or credit card brands that should NOT be shown. Separated by a “;” (semicolon).
        setParameterIfNotNull(params, "EXCLPMLIST", PF_EXCLPMLIST);
        // The possible values are 0, 1 and 2:
        // 0: Horizontally grouped logos with the group name on the left (default value)
        // 1: Horizontally grouped logos with no group names
        // 2: Vertical list of logos with specific payment method or brand name
        setParameterIfNotNull(params, "PMLISTTYPE", PF_PMLISTTYPE);

        // 6. link to your website: see Default reaction
        setParameterIfNotNull(params, "HOMEURL", PF_RESULT_URL_HOME);
        setParameterIfNotNull(params, "CATALOGURL", PF_RESULT_URL_CATALOG);

        // 7. post payment parameters: see Redirection depending on the payment result
        // <input type="hidden" name="COMPLUS" value="">
        // <input type="hidden" name="PARAMPLUS" value="">

        // 8. post payment parameters: see Direct feedback requests (Post-payment)
        // <input type="hidden" name="PARAMVAR" value="">

        // 9.  post payment redirection: see Redirection depending on the payment result
        setParameterIfNotNull(params, "ACCEPTURL", PF_RESULT_URL_ACCEPT);
        setParameterIfNotNull(params, "DECLINEURL", PF_RESULT_URL_DECLINE);
        setParameterIfNotNull(params, "EXCEPTIONURL", PF_RESULT_URL_EXCEPTION);
        setParameterIfNotNull(params, "CANCELURL", PF_RESULT_URL_CANCEL);

        // 10. optional operation field: see Operation
        // Operation code for the transaction. Possible values for new orders:
        // RES: request for authorisation
        // SAL: request for sale (payment)
        // NOTE: for now we shall support only SAL (AUTH_CAPTURE) because PostFinance does not have CAPTURE API and merchants
        //       need to login to their system to CAPTURE payments, which is a bit inconvenient and does nothave integration
        //       with YC.
        params.put("OPERATION", getExternalFormOperation());

        // 11. optional extra login detail field: see User field
        setValueIfNotNull(params, "USERID", payment.getBillingEmail());

        // 12. Alias details: see Alias Management documentation
        //        <input type="hidden" name="ALIAS" value="">
        //        <input type="hidden" name="ALIASUSAGE" value="">
        //        <input type="hidden" name="ALIASOPERATION" value="">

        // 13. check before the payment: see SHA-IN signature
        params.put("SHASIGN", sha1sign(params, getParameterValue(PF_SHA_IN)));

        final StringBuilder form = new StringBuilder();
        for (final Map.Entry<String, String> param : params.entrySet()) {
            form.append(getHiddenField(param.getKey(), param.getValue()));
        }

        form.append(getHiddenField("SUBMIT2", ""));

        return form.toString();

    }

    private static final int ITEMID = 15;
    private static final int ITEMNAME = 40;

    private void populateItems(final Payment payment, final Map<String, String> params) {

        BigDecimal totalItemsGross = Total.ZERO;

        for (final PaymentLine item : payment.getOrderItems()) {
            totalItemsGross = totalItemsGross.add(item.getQuantity().multiply(item.getUnitPrice()));
        }

        final int it = payment.getOrderItems().size();

        BigDecimal orderDiscountRemainder = Total.ZERO;
        BigDecimal orderDiscountPercent = Total.ZERO;
        final BigDecimal payGross = payment.getPaymentAmount();
        if (payGross.compareTo(totalItemsGross) < 0) {
            orderDiscountRemainder = totalItemsGross.subtract(payGross);
            orderDiscountPercent = orderDiscountRemainder.divide(totalItemsGross, 10, RoundingMode.HALF_UP);
        }


        int i = 1;
        boolean hasOrderDiscount = MoneyUtils.isFirstBiggerThanSecond(orderDiscountRemainder, Total.ZERO);
        for (final PaymentLine item : payment.getOrderItems()) {

            final BigDecimal itemGrossAmount = item.getUnitPrice().multiply(item.getQuantity()).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP);
            params.put("ITEMID" + i, item.getSkuCode().length() > ITEMID ? item.getSkuCode().substring(0, ITEMID - 1) + "~" : item.getSkuCode());
            params.put("ITEMNAME" + i, item.getSkuName().length() > ITEMNAME ? item.getSkuName().substring(0, ITEMNAME - 1) + "~" : item.getSkuName());
            params.put("ITEMQUANT" + i, item.getQuantity().toPlainString());
            if (hasOrderDiscount
                    && MoneyUtils.isFirstBiggerThanSecond(orderDiscountRemainder, Total.ZERO)
                    && MoneyUtils.isFirstBiggerThanSecond(itemGrossAmount, Total.ZERO)) {
                BigDecimal discount;
                if (i == it) {
                    // last item
                    discount = orderDiscountRemainder;
                } else {
                    BigDecimal itemDiscount = itemGrossAmount.multiply(orderDiscountPercent).setScale(Total.ZERO.scale(), RoundingMode.CEILING);
                    if (MoneyUtils.isFirstBiggerThanSecond(orderDiscountRemainder, itemDiscount)) {
                        discount = itemDiscount;
                        orderDiscountRemainder = orderDiscountRemainder.subtract(itemDiscount);
                    } else {
                        discount = orderDiscountRemainder;
                        orderDiscountRemainder = Total.ZERO;
                    }

                }
                final BigDecimal scaleRate = discount.divide(itemGrossAmount.subtract(discount), 10, RoundingMode.CEILING);
                final BigDecimal scaledTax = item.getTaxAmount().multiply(scaleRate).setScale(Total.ZERO.scale(), RoundingMode.FLOOR);
                params.put("ITEMDISCOUNT" + i, discount.toPlainString());
                params.put("ITEMPRICE" + i, itemGrossAmount.subtract(discount).subtract(item.getTaxAmount()).add(scaledTax).toPlainString());
                params.put("ITEMVAT" + i, item.getTaxAmount().subtract(scaledTax).toPlainString());
            } else {
                params.put("ITEMPRICE" + i, itemGrossAmount.subtract(item.getTaxAmount()).toPlainString());
                params.put("ITEMVAT" + i, item.getTaxAmount().toPlainString());
            }
            i++;
        }
    }

    /**
     * Supported operations are:
     * SAL: AUTH_CAPTURE
     * RES: AUTH (Capture is configured in PostFinance and thus only manual marker should be supported in YC)
     *
     * @return operation
     */
    protected String getExternalFormOperation() {
        return "SAL";
    }

    private void setValueIfNotNull(final Map<String, String> params, final String key, final String value) {
        if (StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }

    private void setParameterIfNotNull(final Map<String, String> params, final String key, final String valueKey) {
        final String value = getParameterValue(valueKey);
        if (StringUtils.isNotBlank(value)) {
            params.put(key, value);
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
    private String getDescription(final Payment payment, final boolean itemised) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (PaymentLine line : payment.getOrderItems()) {
            if (line.isShipment()) {
                stringBuilder.append(line.getSkuName().replace("\"", "")).append(", ");
            } else if (!itemised) {
                stringBuilder.append(line.getSkuCode().replace("\"",""));
                stringBuilder.append("x");
                stringBuilder.append(line.getQuantity().stripTrailingZeros().toPlainString());
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(payment.getBillingEmail());
        stringBuilder.append(", ");
        stringBuilder.append(payment.getOrderNumber());
        if (stringBuilder.length() > 100) {
            // Only 100 chars allowed
            return stringBuilder.substring(0, 100).toString();
        }
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
    public Payment createPaymentPrototype(final String operation, final Map map) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> raw = HttpParamsUtils.createSingleValueMap(map);
        final Map<String, String> sorted = new TreeMap<String, String>();
        copyHttpParamsAndRemoveSignature(raw, sorted);
        final String amount = sorted.get("AMOUNT");
        if (amount != null) {
            payment.setPaymentAmount(new BigDecimal(amount));
        }
        payment.setOrderCurrency(sorted.get("CURRENCY"));
        payment.setTransactionReferenceId(sorted.get("PAYID"));
        payment.setTransactionAuthorizationCode(sorted.get("ORDERID")); // this is order guid - we need it for refunds
        payment.setCardNumber(sorted.get("CARDNO"));
        payment.setCardType(sorted.get("BRAND"));
        payment.setCardHolderName(sorted.get("CN"));
        if (StringUtils.isNotBlank(sorted.get("ED"))) {
            payment.setCardExpireMonth(sorted.get("ED").substring(0, 2));
            payment.setCardExpireYear(sorted.get("ED").substring(2, 4));
        }

        final CallbackResult res = getExternalCallbackResult(raw);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());
        final StringBuilder msg = new StringBuilder();
        msg.append(sorted.get("STATUS"));
        if (StringUtils.isNotBlank(sorted.get("ACCEPTANCE"))) {
            msg.append(" ").append(sorted.get("ACCEPTANCE"));
        }
        if (StringUtils.isNotBlank(sorted.get("NCERROR"))) {
            msg.append(" ").append(sorted.get("NCERROR"));
        }
        payment.setTransactionOperationResultMessage(msg.toString());

        payment.setShopperIpAddress(sorted.get("IP"));

        return payment;

    }



    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "postFinancePaymentGateway";
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }


}
