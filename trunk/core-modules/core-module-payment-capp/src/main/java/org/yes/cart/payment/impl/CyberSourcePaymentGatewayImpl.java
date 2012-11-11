package org.yes.cart.payment.impl;

import com.cybersource.ws.client.Client;
import com.cybersource.ws.client.ClientException;
import com.cybersource.ws.client.FaultException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.exception.PaymentException;
import org.yes.cart.util.ShopCodeContext;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;


/**
 * Adapter for cybersource (www.cybersource.com) simple order
 * <p/>
 * API (http://www.cybersource.com/support_center/implementation/downloads/simple_order/).
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CyberSourcePaymentGatewayImpl extends AbstractCappPaymentGatewayImpl implements PaymentGatewayInternalForm {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private static final String CS_MERCHANT_ID = "merchantID";
    private static final String CS_KEYS_DIRECTORY = "keysDirectory";
    private static final String CS_TARGET_API_VERSION = "targetAPIVersion";
    private static final String CS_SEND_TO_PRODUCTION = "sendToProduction";
    private static final String CS_USE_HTTP_CLIENT = "useHttpClient";

    private static final String CS_ENABLE_LOG = "enableLog";
    private static final String CS_LOG_DIRECTORY = "logDirectory";
    private static final String CS_LOG_MAXSIZE = "logMaximumSize";

    private static final String CS_PROXY_HOST = "proxyHost";
    private static final String CS_PROXY_PORT = "proxyPort";
    private static final String CS_PROXY_USER = "proxyUser";
    private static final String CS_PROXY_PASSWORD = "proxyPassword";

    private static final Map<String, String> CARD_LABEL_CODE_MAP;
    private static final Map<String, String> ERROR_CODE_DESC_MAP;

    static {

        ERROR_CODE_DESC_MAP = new HashMap<String, String>() {{
            put("100", "Successful transaction.");
            put("101", "The request is missing one or more required fields. Possible action: See the reply fields missingField_0...N for the missing fields. Resend the request with the complete information.");
            put("102", "One or more fields in the request contains invalid data. Possible action: See the reply fields invalidField_0...N for the invalid fields. If you use the Hosted Order Page, see InvalidField_0...N and MissingField_0...N. Resend the request with the correct information.");
            put("104", "The transaction is declined because the merchant reference number sent matches the merchant reference number of another transaction sent in the last 15 minutes. Possible action: Ensure that the merchant reference number is unique.");
            put("110", "Only a partial amount was approved. Possible action: See Partial Authorizations.");
            put("150", "Error: General system failure.See the documentation for your CyberSource client (SDK) for information about how to handle retries in the case of system errors.");
            put("151", "Error: The request was received but a server time-out occurred. This error does not include time-outs between the client and the server.Possible action: To avoid duplicating the transaction, do not resend the request until you have reviewed the transaction status in the Business Center.");
            put("152", "Error: The request was received, but a service did not finish running in time. Possible action: To avoid duplicating the transaction, do not resend the request until you have reviewed the transaction status in the Business Center. See the documentation for your CyberSource client (SDK) for information about how to handle retries in the case of system errors.");
            put("200", "The authorization request was approved by the issuing bank but declined by CyberSource because it did not pass the Address Verification Service (AVS) check. Possible action: You can capture the authorization, but consider reviewing the order for the possibility of fraud.");
            put("201", "The issuing bank has questions about the request. You do not receive an authorization code programmatically, but you might receive one verbally by calling the processor. Possible action: Call your processor to possibly receive a verbal authorization. For contact phone numbers, refer to your merchant bank information.");
            put("202", "Expired card. You might also receive this if the expiration date you provided does not match the date the issuing bank has on file. Possible action: Request a different card or other form of payment.");
            put("203", "The card was declined. No other information was provided by the issuing bank. Possible action: Request a different card or other form of payment.");
            put("204", "Insufficient funds in the account. Possible action: Request a different card or other form of payment.");
            put("205", "Stolen or lost card. Possible action: Refer the transaction to your customer support center for manual review.");
            put("207", "The issuing bank was unavailable. Possible action: Wait a few minutes and resend the request.");
            put("208", "Inactive card or card not authorized for card-not-present transactions. Possible action: Request a different card or other form of payment.");
            put("209", "American Express Card Identification Digits (CID) did not match.Possible action: Request a different card or other form of payment.");
            put("210", "The card has reached the credit limit. Possible action: Request a different card or other form of payment.");
            put("211", "Invalid CVN. Possible action: Request a different card or other form of payment.");
            put("221", "The customer matched an entry on the processors negative file. Possible action: Review the order and contact the payment processor.");
            put("230", "The authorization request was approved by the issuing bank but declined by CyberSource because it did not pass the CVN check. Possible action: You can capture the authorization, but consider reviewing the order for the possibility of fraud.");
            put("231", "Invalid account number. Possible action: Request a different card or other form of payment.");
            put("232", "The card type is not accepted by the payment processor. Possible action: Contact your merchant bank to confirm that your account is set up to receive the card in question.");
            put("233", "The processor declined the request based on an issue with the request itself. Possible action: Request a different card or other form of payment.");
            put("234", "There is a problem with your CyberSource merchant configuration. Possible action: Do not resend the request. Contact Customer Support to correct the configuration problem.");
            put("235", "The requested amount exceeds the originally authorized amount. Occurs, for example, if you try to capture an amount larger than the original authorization amount. Possible action: Issue a new authorization and capture request for the new amount.");
            put("236", "Processor failure. Possible action: Wait a few minutes and resend the request.");
            put("237", "The authorization has already been reversed. Possible action: No action required.");
            put("238", "The authorization has already been captured. Possible action: No action required.");
            put("239", "The requested transaction amount must match the previous transaction amount. Possible action: Correct the amount and resend the request.");
            put("240", "The card type sent is invalid or does not correlate with the credit card number. Possible action: Confirm that the card type correlates with the credit card number specified in the request, then resend the request.");
            put("241", "The request ID is invalid for the follow-on request. Possible action: Verify the request ID is valid and resend the request.");
            put("242", "You requested a capture, but there is no corresponding, unused authorization record. Occurs if there was not a previously successful authorization request or if the previously successful authorization has already been used by another capture request. Possible action: Request a new authorization, and if successful, proceed with the capture.");
            put("243", "The transaction has already been settled or reversed. Possible action: No action required.");
            put("246", "The capture or credit is not voidable because the capture or credit information has already been submitted to your processor. Or, you requested a void for a type of transaction that cannot be voided. Possible action: No action required.");
            put("247", "You requested a credit for a capture that was previously voided. Possible action: No action required.");
            put("250", "Error: The request was received, but there was a timeout at the payment processor. Possible action: To avoid duplicating the transaction, do not resend the request until you have reviewed the transaction status in the Business Center.");
            put("342", "An error occurred during settlement. Suggested action: Verify the information in your request and resend the order.");
            put("400", "The Advanced Fraud Screen score exceeds your threshold. Review the customer's order.");
            put("510", "The authorization request was approved by the issuing bank but declined by CyberSource because it did not pass the Smart Authorization check. Do not capture the authorization without further review. The Smart Authorization codes give you additional information as to why CyberSource refused the request.");
            put("520", "The authorization request was approved by the issuing bank but declined by CyberSource based on your Smart Authorization settings. Possible action: Do not capture the authorization without further review. Review theccAuthReply_avsCode, ccAuthReply_cvCode, and ccAuthReply_authFactorCode fields to determine why CyberSource rejected the request.");
            put("700", "The customer is on a list issued by the U.S. government containing entities with whom trade is restricted. Reject the customer's order.");

        }};

        CARD_LABEL_CODE_MAP = new HashMap<String, String>() {{
            put("Visa", "001");
            put("MasterCard", "002");
            put("Eurocard", "002");
            put("American Express", "003");
            put("Discover", "004");
            put("Diners Club", "005");
            put("Carte Blanche", "006");
            put("JCB", "007");
            put("EnRoute", "014");
            put("JAL", "021");
            put("Maestro (UK Domestic), Solo", "024");
            put("Delta", "031");
            put("Visa Electron", "033");
            put("Dankort", "034");
            put("Laser", "035");
            put("Carte Bleue", "036");
            put("Carta Si", "037");
            put("UATP", "040");
        }};

    }


    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, true, true, false,
            true, true, true, false,
            true, false,
            null
    );


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "cyberSourcePaymentGateway";
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
    public Payment authorize(final Payment payment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorize " + payment);
        }

        final HashMap<String, String> request = new HashMap<String, String>();
        request.put("ccAuthService_run", "true");
        request.put("businessRules_ignoreAVSResult", "true"); //CP
        request.put("merchantReferenceCode",
                StringUtils.isBlank(payment.getTransactionReferenceId()) ?
                        UUID.randomUUID().toString() : payment.getTransactionReferenceId()
        );

        request.put("billTo_email", payment.getBillingEmail());

        addAddress(request, "billTo_", payment.getBillingAddress());
        addAddress(request, "shipTo_", payment.getShippingAddress());


        if (CARD_LABEL_CODE_MAP.get(payment.getCardType()) != null) {
            request.put("card_cardType", CARD_LABEL_CODE_MAP.get(payment.getCardType()));
        }

        request.put("card_accountNumber", payment.getCardNumber());
        request.put("card_expirationYear", payment.getCardExpireYear());
        request.put("card_expirationMonth", payment.getCardExpireMonth());
        if (payment.getCardCvv2Code() != null) {
            request.put("card_cvNumber", payment.getCardCvv2Code());
        }

        request.put("purchaseTotals_currency", payment.getOrderCurrency());
        request.put("purchaseTotals_grandTotalAmount",
                payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());

        if (payment.getOrderItems() != null) {
            for (int index = 0; index < payment.getOrderItems().size(); index++) {
                final PaymentLine paymentLine = payment.getOrderItems().get(index);
                request.put("item_" + index + "_productName", paymentLine.getSkuName());
                request.put("item_" + index + "_productSKU", paymentLine.getSkuCode());
                request.put("item_" + index + "_unitPrice", paymentLine.getUnitPrice().setScale(2, RoundingMode.HALF_UP).toString());
                if (paymentLine.getTaxAmount() != null) {
                    request.put("item_" + index + "_taxAmount", paymentLine.getTaxAmount().setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        }


        return runTransaction(request, payment, AUTH);
    }


    private void addAddress(final HashMap<String, String> request, final String addressPrefix, final PaymentAddress address) {
        if (address != null) {
            request.put(addressPrefix + "firstName", address.getFirstname());
            request.put(addressPrefix + "lastName", address.getLastname());
            request.put(addressPrefix + "street1", address.getAddrline1());
            if (address.getAddrline2() != null) {
                request.put(addressPrefix + "street2", address.getAddrline2());
            }
            request.put(addressPrefix + "city", address.getCity());
            request.put(addressPrefix + "postalCode", address.getPostcode());
            request.put(addressPrefix + "country", address.getCountryCode());
            if (address.getStateCode() != null) {
                request.put(addressPrefix + "state", address.getStateCode());
            }

            /**
             * Commented out becaues of
             * invalidField_0=billTo_phoneNumber
             * invalidField_1=shipTo_phoneNumber

             if (address.getPhoneList() != null) {
             request.put(addressPrefix + "phoneNumber", address.getCountryCode());
             }
             */

        }


    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment payment) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Reverse authorization " + payment);
        }

        final HashMap<String, String> request = new HashMap<String, String>();

        request.put("ccAuthReversalService_run", "true");

        request.put("ccAuthReversalService_authRequestID", payment.getTransactionAuthorizationCode());
        request.put("ccAuthReversalService_authRequestToken", payment.getTransactionRequestToken());
        request.put("purchaseTotals_currency", payment.getOrderCurrency());
        request.put("purchaseTotals_grandTotalAmount",
                payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
        request.put("merchantID", (String) getProperties().get(CS_MERCHANT_ID));
        request.put("requestID", payment.getTransactionAuthorizationCode());
        request.put("requestToken", payment.getTransactionRequestToken());
        request.put("merchantReferenceCode", payment.getTransactionReferenceId());

        return runTransaction(request, payment, REVERSE_AUTH);
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment payment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Capture " + payment);
        }

        final HashMap<String, String> request = new HashMap<String, String>();
        request.put("ccCaptureService_run", "true");
        request.put("ccCaptureService_authRequestID", payment.getTransactionAuthorizationCode());
        request.put("ccCaptureService_authRequestToken", payment.getTransactionRequestToken());
        request.put("purchaseTotals_currency", payment.getOrderCurrency());
        request.put("purchaseTotals_grandTotalAmount",
                payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());

        request.put("merchantID", (String) getProperties().get(CS_MERCHANT_ID));
        request.put("requestID", payment.getTransactionAuthorizationCode());
        request.put("requestToken", payment.getTransactionRequestToken());
        request.put("merchantReferenceCode", payment.getTransactionReferenceId());

        return runTransaction(request, payment, CAPTURE);
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment payment) {
        Payment authPayment = authorize(payment);
        return capture(payment);
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment payment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Void capture " + payment);
        }

        final HashMap<String, String> request = new HashMap<String, String>();

        request.put("voidService_run", "true");

        request.put("voidService_voidRequestID", payment.getTransactionAuthorizationCode());
        request.put("voidService_voidRequestToken", payment.getTransactionRequestToken());
        request.put("merchantID", (String) getProperties().get(CS_MERCHANT_ID));
        request.put("merchantReferenceCode", payment.getTransactionReferenceId());
        request.put("requestID", payment.getTransactionAuthorizationCode());
        request.put("requestToken", payment.getTransactionRequestToken());

        return runTransaction(request, payment, VOID_CAPTURE);

    }


    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment payment) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Credit on prev auth " + payment);
        }

        final HashMap<String, String> request = new HashMap<String, String>();

        request.put("ccCreditService_run", "true");

        request.put("ccCreditService_captureRequestID", payment.getTransactionAuthorizationCode());
        request.put("ccCreditService_captureRequestToken", payment.getTransactionRequestToken());

        request.put("purchaseTotals_currency", payment.getOrderCurrency());
        request.put("purchaseTotals_grandTotalAmount",
                payment.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());

        request.put("merchantID", (String) getProperties().get(CS_MERCHANT_ID));
        request.put("requestID", payment.getTransactionAuthorizationCode());
        request.put("requestToken", payment.getTransactionRequestToken());
        request.put("merchantReferenceCode", payment.getTransactionReferenceId());

        return runTransaction(request, payment, REFUND);
    }


    /**
     * Perform cybersource call.
     *
     * @param request   request.
     * @param paymentIn input payment
     * @param operation op name
     * @return updated payment
     */
    Payment runTransaction(final Map<String, String> request, final Payment paymentIn, final String operation) {

        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(operation);

        try {
            displayMap("Cybersource requset:", request);
            HashMap<String, String> reply = Client.runTransaction(request, getProperties());
            displayMap("Cybersource responce:", reply);
            if ("ACCEPT".equalsIgnoreCase(reply.get("decision"))) {
                payment.setTransactionAuthorizationCode(reply.get("requestID"));
                payment.setTransactionRequestToken(reply.get("requestToken"));
                payment.setTransactionReferenceId(reply.get("requestID"));
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
            } else {
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
            }
            payment.setTransactionOperationResultCode(reply.get("reasonCode"));
            payment.setTransactionOperationResultMessage(ERROR_CODE_DESC_MAP.get(reply.get("reasonCode")));

        } catch (ClientException e) {
            LOG.error("Can not execute transaction. Client exception : " + payment, e);
            throw new PaymentException("Can not execute transaction. Client exception : " + payment, e);
        } catch (FaultException e) {
            LOG.error("Can not execute transaction. Fault exception : " + payment, e);
            throw new PaymentException("Can not execute transaction. Client exception : " + payment, e);
        }
        return payment;
    }


    /**
     * Create properties for cs client.
     *
     * @return propersties.
     */
    Properties getProperties() {
        final Properties props = new Properties();
        for (String paramLabel : new String[]{
                CS_MERCHANT_ID,
                CS_KEYS_DIRECTORY, CS_TARGET_API_VERSION, CS_SEND_TO_PRODUCTION, CS_USE_HTTP_CLIENT,
                CS_ENABLE_LOG, CS_LOG_DIRECTORY, CS_LOG_MAXSIZE,
                CS_PROXY_HOST, CS_PROXY_PORT, CS_PROXY_USER, CS_PROXY_PASSWORD
        }) {
            final String value = getParameterValue(paramLabel);
            if ( StringUtils.isNotBlank(value) ) {
                props.put(paramLabel, value);
            }
        }



        return props;
    }


}
