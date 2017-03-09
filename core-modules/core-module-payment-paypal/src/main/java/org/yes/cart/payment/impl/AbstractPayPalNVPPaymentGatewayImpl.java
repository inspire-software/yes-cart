package org.yes.cart.payment.impl;

import org.apache.commons.lang.SerializationUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.util.log.Markers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * User: denispavlov
 * Date: 24/11/2015
 * Time: 08:29
 */
public abstract class AbstractPayPalNVPPaymentGatewayImpl extends AbstractPayPalPaymentGatewayImpl {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPayPalNVPPaymentGatewayImpl.class);

    protected static final String EQ = "=";
    protected static final String AND = "&";

    protected static final String PP_EC_VERSION = "VERSION";
    protected static final String PP_EC_METHOD = "METHOD";
    protected static final String PP_API_USER_NAME = "API_USER_NAME";
    protected static final String PP_API_USER_PASSWORD = "API_USER_PASSWORD";
    protected static final String PP_SIGNATURE = "SIGNATURE";
    /*
    protected static final String PP_KEY_PASSWORD = "KEY_PASSWORD";
    protected static final String PP_KEY_PATH = "KEY_PATH";
    */

    protected static final String PP_ENVIRONMENT = "ENVIRONMENT";

    /**
     * "https://api-3t.sandbox.paypal.com/nvp";
     * "https://api-3t.paypal.com/nvp";
     */
    protected static final String PP_EC_API_URL = "PP_EC_API_URL";

    /*
    https://www.sandbox.paypal.com/webscr   &cmd=_express-checkout&token=XXXX
    https://www.paypal.com/cgi-bin/webscr   ?cmd=_express-checkout&token=XXXX
    */
    protected static final String PP_EC_PAYPAL_URL = "PP_EC_PAYPAL_URL";

    public static final String ORDER_GUID = "orderGuid";  //this id our order guid
    public static final String PP_EC_TOKEN = "TOKEN";     //this will be return from pay pall ec
    public static final String PP_EC_PAYERID  = "PAYERID";     // the payer id on paypal  side



    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);

        payment.setTransactionOperation(REFUND);

        final NvpBuilder npvs = new NvpBuilder();

        npvs.addRaw(PP_EC_TOKEN, payment.getTransactionRequestToken());
        npvs.addRaw(PP_EC_PAYERID, payment.getTransactionAuthorizationCode());
        npvs.addRaw("TRANSACTIONID", payment.getTransactionReferenceId());
        npvs.addEncoded("INVOICEID", payment.getOrderShipment());
        npvs.addRaw("REFUNDTYPE", "Full");

        try {

            final Map<String, String> result = performHttpCall("RefundTransaction", npvs.toMap());

            if (isAckSuccess(result) && !"none".equalsIgnoreCase(result.get("REFUNDSTATUS"))) {

                payment.setTransactionReferenceId(result.get("REFUNDTRANSACTIONID"));
                payment.setTransactionAuthorizationCode("");
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                payment.setTransactionOperationResultMessage(result.get("PENDINGREASON"));
                payment.setPaymentProcessorBatchSettlement(false);


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
            LOG.error(Markers.alert(), "PayPal transaction [" + payment.getOrderNumber() + "] failed, cause: " + exp.getMessage(), exp);
            LOG.error("PayPal transaction failed, payment: " + payment, exp);
            payment.setTransactionReferenceId(UUID.randomUUID().toString());
            payment.setTransactionAuthorizationCode("");
            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            payment.setTransactionOperationResultMessage(exp.getMessage());
            payment.setPaymentProcessorBatchSettlement(false);

        }

        return payment;
    }




    /**
     * ******************************************************************************
     * deformatNVP: Function to break up the NVP string into a HashMap
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
                try {
                    String key = URLDecoder.decode(stInternalTokenizer.nextToken(), "UTF-8");
                    String value = URLDecoder.decode(stInternalTokenizer.nextToken(), "UTF-8");
                    nvp.put(key.toUpperCase(), value);
                } catch (UnsupportedEncodingException e) {
                    LOG.error("Unable to decode NVP payload " + pPayload, e);
                }
            }
        }
        return nvp;
    }


    /**
     * Perform NPV call using API user credentials and signature.
     *
     * @param method NVP method
     * @param nvp NVP parameters
     *
     * @return response
     *
     * @throws IOException
     */
    protected Map<String, String> performHttpCall(final String method,
                                                  final Map<String, String> nvp) throws IOException {

        final NvpBuilder npvb = new NvpBuilder();

        npvb
                .addRaw(PP_EC_METHOD, method)
                //.addRaw(PP_EC_VERSION, "98.0")
                .addRaw(PP_EC_VERSION, "123.0")
                .addEncoded("PWD", getParameterValue(PP_API_USER_PASSWORD))
                .addEncoded("USER", getParameterValue(PP_API_USER_NAME))
                .addEncoded(PP_SIGNATURE, getParameterValue(PP_SIGNATURE));

        npvb.addAllRaw(nvp);

        return deformatNVP(
                performPayPalApiCall(getParameterValue(PP_EC_API_URL), npvb.toQuery())
        );

    }


    private String performPayPalApiCall(final String endpoint, final String callParams) throws IOException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("PayPal NPV call:\n{}", callParams.replace('&', '\n'));
        }

        final StringBuilder respBuilder = new StringBuilder();

        final HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setEntity(new StringEntity(callParams));

        final DefaultHttpClient client = new DefaultHttpClient();

        final HttpResponse response = client.execute(httpPost);
        final BufferedReader rd = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));

        String _line;
        while (((_line = rd.readLine()) != null)) {
            respBuilder.append(_line);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("PayPal NPV response:\n{}", respBuilder.toString().replace('&', '\n'));
        }
        return respBuilder.toString();
    }

    /**
     * Encode value as URL compatible UTF-8.
     *
     * @param value value
     *
     * @return encoded value
     */
    protected String encode(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception exp) {
            return value;
        }
    }

    /**
     * Append NVP. Automatically adds '&' and encodes the values
     *
     * @param nvps NPV's
     * @param name name
     * @param value value
     */
    protected void toAppendNVPTo(final StringBuilder nvps, final String name, final String value) {
        if (nvps.length() > 0) {
            nvps.append(AND);
        }
        nvps.append(name).append(EQ).append(value);

    }

    /**
     * Check is ACK returned a success code.
     *
     * Acknowledgement status, which is one of the following values:
     * - Success indicates a successful operation.
     * - SuccessWithWarning indicates a successful operation; however, there are messages returned in the response that you should examine.
     * - Failure indicates the operation failed; the response also contains one or more error messages explaining the failure.
     * - FailureWithWarning indicates that the operation failed and that there are messages returned in the response that you should examine.
     *
     *
     * @param result call result
     *
     * @return true if ACK denotes a successful transaction
     */
    protected boolean isAckSuccess(final Map<String, String> result) {
        return result.get("ACK") != null &&
            (
                "Success".equalsIgnoreCase(result.get("ACK"))
                || "SuccessWithWarning".equalsIgnoreCase(result.get("ACK"))
            );
    }

    /**
     * NPV builder
     */
    protected class NvpBuilder {

        private Map<String, String> npv = new LinkedHashMap<String, String>();

        /**
         * Add raw name value pair.
         *
         * @param name name
         * @param value value
         */
        public NvpBuilder addRaw(final String name, final String value) {
            npv.put(name, value);
            return this;
        }

        /**
         * Add raw name value pair.
         *
         * @param all all values
         */
        public NvpBuilder addAllRaw(final Map<String, String> all) {
            npv.putAll(all);
            return this;
        }

        /**
         * Add encoded name value pair.
         *
         * @param name name
         * @param value value (will be URL encoded in UTF-8)
         */
        public NvpBuilder addEncoded(final String name, final String value) {
            npv.put(name,  encode(value));
            return this;
        }

        /**
         * Remove NPV.
         *
         * @param name name
         */
        public NvpBuilder remove(final String name) {
            npv.remove(name);
            return this;
        }

        /**
         * To NPV map by reference (no copy).
         *
         * @return map
         */
        public Map<String, String> toMap() {
            return npv;
        }

        /**
         * To NPV query string.
         *
         * @return query string
         */
        public String toQuery() {
            final StringBuilder query = new StringBuilder();
            for (final Map.Entry<String, String> param : npv.entrySet()) {
                toAppendNVPTo(query, param.getKey(), param.getValue());
            }
            return query.toString();
        }

    }


}
