package org.yes.cart.payment.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.SerializationUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.util.Assert;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/14/11
 * Time: 2:33 PM
 */
public class PayPalExpressCheckoutPaymentGatewayImpl extends AbstractPayPalPaymentGatewayImpl implements PaymentGatewayExternalForm {

    private static final String EQ = "=";
    private static final String AND = "&";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, true,
            true,
            null
    );

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }


    /**
     * Get the POST url for form
     * may be vary for sandbox and real payment gateway
     * possible values are:
     */
    public String getPostActionUrl() {
        //must be special mounted page in UI
        return "paymentpaypalexpress";
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {

        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);

        payment.setTransactionOperation(AUTH_CAPTURE);

        try {
            final Map<String, String> paymentResult = doDoExpressCheckoutPayment(
                    payment.getTransactionRequestToken(),
                    payment.getTransactionReferenceId(),
                    payment.getPaymentAmount(), payment.getOrderCurrency());
            payment.setPaymentProcessorResult( isSuccess(paymentResult)
                    ? Payment.PAYMENT_STATUS_OK
                    : Payment.PAYMENT_STATUS_FAILED);

        } catch (IOException e) {
            payment.setPaymentProcessorResult( Payment.PAYMENT_STATUS_FAILED);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorize(final Payment payment) {
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_PROCESSING); // because no actually ok and not failed
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
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(final Map privateCallBackParameters) {
        return AbstractCappPaymentGatewayImpl.getSingleValue(privateCallBackParameters.get(ORDER_GUID));
    }


    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token   the token obtained via   SetExpressCheckout method
     * @param payerId the token obtained via   GetExpressCheckoutDetails method
     * @param amount  the amount
     * @return map of parsed key - values with detail information
     * @throws java.io.IOException in case of errors
     */
    public Map<String, String> doDoExpressCheckoutPayment(final String token,
                                                          final String payerId,
                                                          final BigDecimal amount,
                                                          final String currencyCode) throws IOException {

        Assert.notNull(token, "The pay pal tonek must  be not null");
        Assert.notNull(payerId, "Payer must be provided");
        Assert.notNull(amount, "Amount must be provided");
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0, "Amount must be positive");
        Assert.notNull(currencyCode, "Currency code must be provided");


        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(PP_EC_TOKEN);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(token));
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_PAYERID);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(payerId));
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_AMT);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode("" + amount));
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_CURRENCYCODE);
        stringBuilder.append(EQ);
        stringBuilder.append(currencyCode);
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_PAYMENTACTION);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode("Sale"));

        return performHttpCall("DoExpressCheckoutPayment", stringBuilder.toString());


    }


    /**
     * Get the express checkout details via GetExpressCheckoutDetails method of
     * pay pal payment gateway
     *
     * @param token the token obtained via   SetExpressCheckout method
     * @return map of parsed key - values with detail information
     * @throws java.io.IOException in case of errors
     */
    public Map<String, String> getExpressCheckoutDetails(final String token) throws IOException {

        Assert.notNull(token, "The pay pal tonek must  be not null");

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(PP_EC_TOKEN);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(token));

        return performHttpCall("GetExpressCheckoutDetails", stringBuilder.toString());

    }


    /**
     * Support for pp express checkout. In case if gateway not support this operation , return will be empty hashmap.
     *
     * All info about SetExpressCheckout see here:
     * https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_nvp_r_SetExpressCheckout
     *
     * @param amount       amount
     * @param currencyCode currecny
     * @return map with auth token
     * @throws java.io.IOException in case of errors
     */
    public Map<String, String> setExpressCheckoutMethod(final BigDecimal amount, final String currencyCode) throws IOException {

        Assert.notNull(amount, "Amount must be provided");
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0, "Amount must be positive");
        Assert.notNull(currencyCode, "Currency code must be provided");


        final StringBuilder stringBuilder = new StringBuilder();


        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_AMT);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode("" + amount));
        stringBuilder.append(AND);


        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_PAYMENTACTION);
        stringBuilder.append(EQ);
        stringBuilder.append("Sale");
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_RETURNURL);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(getParameterValue(PP_EC_RETURNURL)));
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_CANCELURL);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(getParameterValue(PP_EC_CANCELURL)));
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_NOSHIPPING);
        stringBuilder.append(EQ);
        stringBuilder.append("1");
        stringBuilder.append(AND);


        stringBuilder.append(PP_EC_PAYMENTREQUEST_0_CURRENCYCODE);
        stringBuilder.append(EQ);
        stringBuilder.append(currencyCode);


        return performHttpCall("SetExpressCheckout", stringBuilder.toString());
    }


    private Map<String, String> performHttpCall(final String method, final String nvpStr) throws IOException {

        final StringBuilder stringBuilder = new StringBuilder();


        stringBuilder.append(PP_EC_METHOD);
        stringBuilder.append(EQ);
        stringBuilder.append(method);
        stringBuilder.append(AND);

        stringBuilder.append(PP_EC_VERSION);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode("2.3"));
        stringBuilder.append(AND);

        stringBuilder.append("PWD");
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(getParameterValue(PP_API_USER_PASSWORD)));
        stringBuilder.append(AND);

        stringBuilder.append("USER");
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(getParameterValue(PP_API_USER_NAME)));
        stringBuilder.append(AND);


        stringBuilder.append(PP_SIGNATURE);
        stringBuilder.append(EQ);
        stringBuilder.append(URLEncoder.encode(getParameterValue(PP_SIGNATURE)));
        stringBuilder.append(AND);

        stringBuilder.append(nvpStr);

        return deformatNVP(
                performPayPalApiCall(stringBuilder.toString())
        );

    }

    private String performPayPalApiCall(final String callParams) throws IOException {

        System.out.println(">>>> " + callParams);

        final StringBuilder respBuilder = new StringBuilder();

        final HttpPost httpPost = new HttpPost(getParameterValue(PP_EC_API_URL));
        httpPost.setEntity(new StringEntity(callParams));

        final DefaultHttpClient client = new DefaultHttpClient();

        final HttpResponse response = client.execute(httpPost);
        final BufferedReader rd = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));

        String _line;
        while (((_line = rd.readLine()) != null)) {
            respBuilder.append(_line);
        }
        System.out.println("<<<< " + respBuilder.toString());
        return respBuilder.toString();
    }

    /**
     * Check the result for success attributes.
     *
     * @param nvpCallResult call result
     * @return true in case of success
     */
    public boolean isSuccess(final Map<String, String> nvpCallResult) {
        return nvpCallResult.get("ACK") != null && nvpCallResult.get("ACK").equalsIgnoreCase("Success");
    }


    /**
     * ******************************************************************************
     * deformatNVP: Function to break the NVP string into a HashMap
     * pPayLoad is the NVP string.
     * returns a HashMap object containing all the name value pairs of the string.
     * *******************************************************************************
     *
     * @param pPayload given string
     * @return map
     */
    public Map<String, String> deformatNVP(final String pPayload) {
        Map<String, String> nvp = new HashMap<String, String>();
        StringTokenizer stTok = new StringTokenizer(pPayload, AND);
        while (stTok.hasMoreTokens()) {
            StringTokenizer stInternalTokenizer = new StringTokenizer(stTok.nextToken(), EQ);
            if (stInternalTokenizer.countTokens() == 2) {
                String key = URLDecoder.decode(stInternalTokenizer.nextToken());
                String value = URLDecoder.decode(stInternalTokenizer.nextToken());
                nvp.put(key.toUpperCase(), value);
            }
        }
        return nvp;
    }

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHiddenFiled(ORDER_GUID, orderGuid));  // this will be bypassed via payment gateway to restore it latter
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map parametersMap) {
        final Payment payment = new PaymentImpl();
        payment.setTransactionRequestToken((String) parametersMap.get("TOKEN"));
        payment.setTransactionReferenceId((String) parametersMap.get("PAYERID"));
        payment.setTransactionAuthorizationCode((String) parametersMap.get("CORRELATIONID"));
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "payPalExpressPaymentGateway";
    }

}
