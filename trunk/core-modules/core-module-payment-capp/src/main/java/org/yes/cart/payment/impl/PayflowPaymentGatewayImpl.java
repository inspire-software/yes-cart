package org.yes.cart.payment.impl;

import org.apache.commons.lang.SerializationUtils;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.exception.PaymentException;
import org.yes.cart.util.ShopCodeContext;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for payflow payment gateway.
 * <p/>
 * API (https://www.x.com/community/ppx/sdks#PayflowPro).
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PayflowPaymentGatewayImpl extends AbstractCappPaymentGatewayImpl implements PaymentGatewayInternalForm {

    private static final String PF_HOST = "HOST";
    private static final String PF_PORT = "PORT";
    private static final String PF_TIMEOUT = "TIMEOUT"; // sec ?

    private static final String PF_LOG_FILENAME = "LOG_FILENAME";
    private static final String PF_LOG_LEVEL = "LOG_LEVEL"; // allowed values      SEVERITY_FATAL    SEVERITY_ERROR     SEVERITY_WARN     SEVERITY_INFO    SEVERITY_DEBUG

    private static final String PF_LOG_FILESIZE = "LOG_SIZESIZE";  //bytes ?
    private static final String PF_LOG_ENABLED = "LOG_ENABLED";

    private static final String PF_PROXY_HOST = "PROXY_HOST";
    private static final String PF_PROXY_PORT = "PROXY_PORT";
    private static final String PF_PROXY_USER = "PROXY_USER";
    private static final String PF_PROXY_PASSWORD = "PROXY_PASSWORD";
    private static final String PF_PROXY_ENABLED = "PROXY_ENABLED";

    private static final String PF_USER_NAME = "USER_NAME";
    private static final String PF_USER_PASSWORD = "USER_PASSWORD";
    private static final String PF_VENDOR = "VENDOR";
    private static final String PF_PARTNER = "PARTNER";

    private static final int PF_RESULT_CODE_ACCEPTED = 0; // accepted
    private static final int PF_RESULT_CODE_ACCEPTED_WITH_FRAUD = 126; // accepted  with fraud warnings need review


    private Map<String, String> props;
    private static final Map<Integer, String> ERROR_CODE_DESC_MAP;
    private static final PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, true, true, true,
            true, true, true, false,
            true, false,
            null,
            true, true
    );

    static {

        ERROR_CODE_DESC_MAP = new HashMap<Integer, String>() {{

            put(0, "Approved");
            put(1, "User authentication failed. Error is caused by one or more of the following: a) Invalid Processor information entered. Contact merchant bank to verify. b) Allowed IP Address security feature implemented. The transaction is coming from an unknown IP address. For more information, refer to Allowed IP Addresses. c) You are using a test (not active) account to submit a transaction to the live PayPal servers. Change the URL from pilot-payflowpro.paypal.com to payflowpro.paypal.com.");
            put(2, "Invalid tender type. Your merchant bank account does not support the following credit card type that was submitted.");
            put(3, "Invalid transaction type. Transaction type is not appropriate for this transaction. For example, you cannot credit an authorization-only transaction.");
            put(4, "Invalid amount format. Use the format: #####.##  Do not include currency symbols or commas.");
            put(5, "Invalid merchant information. Processor does not recognize your merchant account information. Contact your bank account acquirer to resolve this problem.");
            put(6, "Invalid or unsupported currency code");
            put(7, "Field format error. Invalid information entered. See RESPMSG.");
            put(8, "Not a transaction server");
            put(9, "Too many parameters or invalid stream");
            put(10, "Too many line items");
            put(11, "Client time-out waiting for response");
            put(12, "Declined. Check the credit card number, expiration date, and transaction information to make sure they were entered correctly. If this does not resolve the problem, have the customer call their card issuing bank to resolve.");
            put(13, "Referral. Transaction cannot be approved electronically but can be approved with a verbal authorization. Contact your merchant bank to obtain an authorization and submit a manual Voice Authorization transaction.  ");
            put(14, "Invalid Client Certification ID. Check the HTTP header. If the tag, X-VPS-VIT-CLIENT-CERTIFICATION-ID, is missing, RESULT code 14 is returned.");
            put(19, "Original transaction ID not found. The transaction ID you entered for this transaction is not valid. See RESPMSG.");
            put(20, "Cannot find the customer reference number");
            put(22, "Invalid ABA number");
            put(23, "Invalid account number. Check credit card number and re-submit.");
            put(24, "Invalid expiration date. Check and re-submit.");
            put(25, "Invalid Host Mapping. You are trying to process a tender type such as Discover Card, but you are not set up with your merchant bank to accept this card type.");
            put(26, "Invalid vendor account");
            put(27, "Insufficient partner permissions");
            put(28, "Insufficient user permissions");
            put(29, "Invalid XML document. This could be caused by an unrecognized XML tag or a bad XML format that cannot be parsed by the system.");
            put(30, "Duplicate transaction");
            put(31, "Error in adding the recurring profile");
            put(32, "Error in modifying the recurring profile");
            put(33, "Error in canceling the recurring profile");
            put(34, "Error in forcing the recurring profile");
            put(35, "Error in reactivating the recurring profile");
            put(36, "OLTP Transaction failed");
            put(37, "Invalid recurring profile ID");
            put(50, "Insufficient funds available in account");
            put(51, "Exceeds per transaction limit");
            put(99, "General error. See RESPMSG.");
            put(100, "Transaction type not supported by host");
            put(101, "Time-out value too small");
            put(102, "Processor not available");
            put(103, "Error reading response from host");
            put(104, "Timeout waiting for processor response. Try your transaction again.");
            put(105, "Credit error. Make sure you have not already credited this transaction, or that this transaction ID is for a creditable transaction. (For example, you cannot credit an authorization.)");
            put(106, "Host not available");
            put(107, "Duplicate suppression time-out");
            put(108, "Void error. See RESPMSG. Make sure the transaction ID entered has not already been voided. If not, then look at the Transaction Detail screen for this transaction to see if it has settled. (The Batch field is set to a number greater than zero if the transaction has been settled). If the transaction has already settled, your only recourse is a reversal (credit a payment or submit a payment for a credit).");
            put(109, "Time-out waiting for host response");
            put(110, "Referenced auth (against order) Error");
            put(111, "Capture error. Either an attempt to capture a transaction that is not an authorization transaction type, or an attempt to capture an authorization transaction that has already been captured.");
            put(112, "Failed AVS check. Address and ZIP code do not match. An authorization may still exist on the cardholders account.");
            put(113, "Merchant sale total will exceed the sales cap with current transaction. ACH transactions only.");
            put(114, "Card Security Code mismatch. An authorization may still exist on the cardholders account.");
            put(115, "System busy, try again later");
            put(116, "PayPal Internal error. Failed to lock terminal number");
            put(117, "Failed merchant rule check. One or more of the following three failures occurred: An attempt was made to submit a transaction that failed to meet the security settings specified on the PayPal Manager Security Settings page. If the transaction exceeded the Maximum Amount security setting, then no values are returned for AVS or Card Security Code. AVS validation failed. The AVS return value should appear in the RESPMSG. Card Security Code validation failed. The Card Security Code return value should appear in the RESPMSG.");
            put(118, "Invalid keywords found in string fields");
            put(119, "General failure within PIM Adapter");
            put(120, "Attempt to reference a failed transaction");
            put(121, "Not enabled for feature");
            put(122, "Merchant sale total will exceed the credit cap with current transaction. ACH transactions only.");
            put(125, "Fraud Protection Services Filter  Declined by filters");
            put(126, "Fraud Protection Services Filter  Flagged for review by filters Important Note: Result code 126 indicates that a transaction triggered a fraud filter. This is not an error, but a notice that the transaction is in a review status. The transaction has been authorized but requires you to review and to manually accept the transaction before it will be allowed to settle. If this is a new Payflow Pro account, this result occurred because all new accounts include a test drive of the Fraud Protection Services at no charge. The filters are on by default. You can modify these settings based on your business needs. Result code 126 is intended to give you an idea of the kind of transaction that is considered suspicious to enable you to evaluate whether you can benefit from using the Fraud Protection Services. To eliminate result 126, turn the filters off.");
            put(127, "Fraud Protection Services Filter  Not processed by filters");
            put(128, "Fraud Protection Services Filter  Declined by merchant after being flagged for review by filters");
            put(131, "Version 1 Payflow SDK client no longer supported. Upgrade to the most recent version of the Payflow Pro client.");
            put(132, "Card has not been submitted for update");
            put(133, "Data mismatch in HTTP retry request");
            put(150, "Issuing bank timed out");
            put(151, "Issuing bank unavailable");
            put(200, "Reauth error");
            put(201, "Order error");
            put(402, "PIM Adapter Unavailable");
            put(403, "PIM Adapter stream error");
            put(404, "PIM Adapter Timeout");
            put(600, "Cybercash Batch Error");
            put(601, "Cybercash Query Error");
            put(1000, "Generic host error. This is a generic message returned by your credit card processor. The RESPMSG will contain more information describing the error.");
            put(1001, "Buyer Authentication unavailable");
            put(1002, "Buyer Authentication  Transaction timeout");
            put(1003, "Buyer Authentication  Invalid client version");
            put(1004, "Buyer Authentication  Invalid timeout value");
            put(1011, "Buyer Authentication unavailable");
            put(1012, "Buyer Authentication unavailable");
            put(1013, "Buyer Authentication unavailable");
            put(1014, "Buyer Authentication  Merchant is not enrolled for Buyer Authentication Service (3-D Secure).");
            put(1016, "Buyer Authentication  3-D Secure error response received. Instead of receiving a PARes response to a Validate Authentication transaction, an error response was received.");
            put(1017, "Buyer Authentication  3-D Secure error response is invalid. An error response is received and the response is not well formed for a Validate Authentication transaction.");
            put(1021, "Buyer Authentication  Invalid card type");
            put(1022, "Buyer Authentication  Invalid or missing currency code");
            put(1023, "Buyer Authentication  merchant status for 3D secure is invalid");
            put(1041, "Buyer Authentication  Validate Authentication failed: missing or invalid PARES");
            put(1042, "Buyer Authentication  Validate Authentication failed: PARES format is invalid");
            put(1043, "Buyer Authentication  Validate Authentication failed: Cannot find successful Verify Enrollment");
            put(1044, "Buyer Authentication  Validate Authentication failed: Signature validation failed for PARES");
            put(1045, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid amount in PARES");
            put(1046, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid acquirer in PARES");
            put(1047, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid Merchant ID in PARES");
            put(1048, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid card number in PARES");
            put(1049, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid currency code in PARES");
            put(1050, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid XID in PARES");
            put(1051, "Buyer Authentication  Validate Authentication failed: Mismatched or invalid order date in PARES");
            put(1052, "Buyer Authentication  Validate Authentication failed: This PARES was already validated for a previous Validate Authentication transaction");


        }};

    }


    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "payflowPaymentGateway";
    }


    /**
     * Configure payflow client.
     */
    private synchronized void configurePayFlowSystem() {
        try {
            paypal.payflow.SDKProperties.setHostAddress(getProperties().get(PF_HOST));
            paypal.payflow.SDKProperties.setHostPort(Integer.parseInt(getProperties().get(PF_PORT)));
            paypal.payflow.SDKProperties.setTimeOut(Integer.parseInt(getProperties().get(PF_TIMEOUT)));

            if (Boolean.parseBoolean(getProperties().get(PF_LOG_ENABLED))) {
                paypal.payflow.SDKProperties.setLogFileName(getProperties().get(PF_LOG_FILENAME));

                if ("SEVERITY_FATAL".equalsIgnoreCase(getProperties().get(PF_LOG_LEVEL))) {
                    paypal.payflow.SDKProperties.setLoggingLevel(paypal.payflow.PayflowConstants.SEVERITY_FATAL);
                } else if ("SEVERITY_ERROR".equalsIgnoreCase(getProperties().get(PF_LOG_LEVEL))) {
                    paypal.payflow.SDKProperties.setLoggingLevel(paypal.payflow.PayflowConstants.SEVERITY_ERROR);
                } else if ("SEVERITY_WARN".equalsIgnoreCase(getProperties().get(PF_LOG_LEVEL))) {
                    paypal.payflow.SDKProperties.setLoggingLevel(paypal.payflow.PayflowConstants.SEVERITY_WARN);
                } else if ("SEVERITY_INFO".equalsIgnoreCase(getProperties().get(PF_LOG_LEVEL))) {
                    paypal.payflow.SDKProperties.setLoggingLevel(paypal.payflow.PayflowConstants.SEVERITY_INFO);
                } else {
                    paypal.payflow.SDKProperties.setLoggingLevel(paypal.payflow.PayflowConstants.SEVERITY_DEBUG);
                }
                paypal.payflow.SDKProperties.setMaxLogFileSize(Integer.valueOf(getProperties().get(PF_LOG_FILESIZE)));
            }

            if (Boolean.parseBoolean(getProperties().get(PF_PROXY_ENABLED))) {
                paypal.payflow.SDKProperties.setProxyAddress(getProperties().get(PF_PROXY_HOST));
                paypal.payflow.SDKProperties.setProxyLogin(getProperties().get(PF_PROXY_USER));
                paypal.payflow.SDKProperties.setProxyPassword(getProperties().get(PF_PROXY_PASSWORD));
                paypal.payflow.SDKProperties.setProxyPort(Integer.parseInt(getProperties().get(PF_PROXY_PORT)));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create payflow user.
     *
     * @return payflow user.
     */
    private paypal.payflow.UserInfo createUserInfo() {
        return new paypal.payflow.UserInfo(
                getProperties().get(PF_USER_NAME),
                getProperties().get(PF_VENDOR),
                getProperties().get(PF_PARTNER),
                getProperties().get(PF_USER_PASSWORD));
    }

    /**
     * Convert given payment to payflow invoice.
     *
     * @param paymentIn given payment.
     * @return payflow invoice.
     */
    private paypal.payflow.Invoice createInvoice(final Payment paymentIn) {

        final paypal.payflow.Invoice invoice = new paypal.payflow.Invoice();

        paypal.payflow.BillTo billTo = createBillingAddress(paymentIn);

        paypal.payflow.ShipTo shipTo = createShippingAddress(paymentIn);

        invoice.setBillTo(billTo);
        invoice.setShipTo(shipTo);
        invoice.setAmt(new paypal.payflow.Currency(
                paymentIn.getPaymentAmount().doubleValue(),
                paymentIn.getOrderCurrency()
        ));
        invoice.setPoNum(paymentIn.getOrderShipment());
        invoice.setInvNum(paymentIn.getOrderShipment().replace("-", "")); //alphachar , the - prohibited

        fillDeliveryDetails(paymentIn, invoice);

        return invoice;

    }

    /**
     * Fill delivery details in invoice from payment.
     *
     * @param paymentIn payment
     * @param invoice   invoice to fill
     */
    private void fillDeliveryDetails(final Payment paymentIn, final paypal.payflow.Invoice invoice) {
        for (PaymentLine line : paymentIn.getOrderItems()) {
            paypal.payflow.LineItem lineItem = new paypal.payflow.LineItem();
            lineItem.setSku(line.getSkuCode());
            lineItem.setDesc(line.getSkuName());
            lineItem.setQty(line.getQuantity().longValue());
            lineItem.setCost(new paypal.payflow.Currency(
                    line.getUnitPrice().doubleValue(),
                    paymentIn.getOrderCurrency()
            ));
            lineItem.setAmt(
                    new paypal.payflow.Currency(
                            line.getQuantity().multiply(line.getUnitPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                            paymentIn.getOrderCurrency())
            );
            invoice.addLineItem(lineItem);
        }
    }

    /**
     * Create shipping address.
     *
     * @param paymentIn payment
     * @return shipping address.
     */
    private paypal.payflow.ShipTo createShippingAddress(final Payment paymentIn) {
        paypal.payflow.ShipTo shipTo = new paypal.payflow.ShipTo();
        shipTo.setShipToCountry(paymentIn.getShippingAddress().getCountryCode());
        shipTo.setShipToCity((paymentIn.getShippingAddress().getCity()));
        shipTo.setShipToEmail(paymentIn.getBillingEmail());
        shipTo.setShipToFirstName(paymentIn.getShippingAddress().getFirstname());
        shipTo.setShipToLastName(paymentIn.getShippingAddress().getLastname());
        shipTo.setShipToState(paymentIn.getShippingAddress().getStateCode());
        shipTo.setShipToZip(paymentIn.getShippingAddress().getPostcode());
        shipTo.setShipToStreet(
                getStreetAddress(paymentIn.getShippingAddress().getAddrline1(), paymentIn.getShippingAddress().getAddrline2())
        );
        shipTo.setShipToPhone(paymentIn.getShippingAddress().getPhoneList());
        return shipTo;
    }

    /**
     * Create billing address
     *
     * @param paymentIn payment
     * @return billing address.
     */
    private paypal.payflow.BillTo createBillingAddress(final Payment paymentIn) {
        paypal.payflow.BillTo billTo = new paypal.payflow.BillTo();
        billTo.setBillToCountry(paymentIn.getBillingAddress().getCountryCode());
        billTo.setCity(paymentIn.getBillingAddress().getCity());
        billTo.setEmail(paymentIn.getBillingEmail());
        billTo.setFirstName(paymentIn.getBillingAddress().getFirstname());
        billTo.setLastName(paymentIn.getBillingAddress().getLastname());
        billTo.setState(paymentIn.getBillingAddress().getStateCode());
        billTo.setZip(paymentIn.getBillingAddress().getPostcode());
        billTo.setStreet(
                getStreetAddress(paymentIn.getBillingAddress().getAddrline1(), paymentIn.getBillingAddress().getAddrline2())
        );
        billTo.setPhoneNum(paymentIn.getBillingAddress().getPhoneList());
        return billTo;
    }


    /**
     * Create cc object from given payment.
     *
     * @param paymentIn given payment
     * @return credit card
     */
    private paypal.payflow.CreditCard createCreditCard(final Payment paymentIn) {
        paypal.payflow.CreditCard creditCard = new paypal.payflow.CreditCard(
                paymentIn.getCardNumber(),
                paymentIn.getCardExpireMonth() +
                        paymentIn.getCardExpireYear().substring(paymentIn.getCardExpireYear().length() - 2, paymentIn.getCardExpireYear().length())
        );
        creditCard.setCvv2(paymentIn.getCardCvv2Code());
        creditCard.setName(paymentIn.getCardHolderName());
        //CP some other info if need
        return creditCard;
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
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.Invoice inv = createInvoice(paymentIn);
        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.AuthorizationTransaction trans =
                new paypal.payflow.AuthorizationTransaction(
                        createUserInfo(),
                        connection,
                        inv,
                        new paypal.payflow.CardTender(createCreditCard(paymentIn)),
                        transRequestId

                );

        runTransaction(payment, trans, false);

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH_CAPTURE);
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.Invoice inv = createInvoice(paymentIn);
        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.SaleTransaction trans =
                new paypal.payflow.SaleTransaction(
                        createUserInfo(),
                        connection,
                        inv,
                        new paypal.payflow.CardTender(createCreditCard(paymentIn)),
                        transRequestId

                );

        runTransaction(payment, trans, true);

        return payment;

    }


    /**
     * Run transaction and process responce.
     *
     * @param payment        payment
     * @param trans          transaction
     * @param withClientInfo process responce wiht client info
     */
    private void runTransaction(final Payment payment,
                                final paypal.payflow.BaseTransaction trans,
                                final boolean withClientInfo) {
        try {
            paypal.payflow.Response responce = trans.submitTransaction();
            if (responce == null) {
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                throw new PaymentException("Can not get the response, try to increase TIMEOUT parameter");
            } else {
                paypal.payflow.TransactionResponse trxnResponse = responce.getTransactionResponse();

                if (withClientInfo) {
                    // Create a new Client Information data object.
                    paypal.payflow.ClientInfo clInfo = new paypal.payflow.ClientInfo();
                    // Set the ClientInfo object of the transaction object.
                    trans.setClientInfo(clInfo);
                }

                payment.setTransactionAuthorizationCode(trxnResponse.getAuthCode());
                if (PF_RESULT_CODE_ACCEPTED == trxnResponse.getResult()
                    /*|| PF_RESULT_CODE_ACCEPTED_WITH_FRAUD == trxnResponse.getResult()*/) {
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                    payment.setTransactionAuthorizationCode(trxnResponse.getPnref());
                } else {
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                }
                payment.setTransactionOperationResultCode(String.valueOf(trxnResponse.getResult()));
                payment.setTransactionOperationResultMessage(
                        responce.getTransactionResponse().getRespMsg()
                                + ' '
                                + ERROR_CODE_DESC_MAP.get(trxnResponse.getResult()));
            }
        } catch (Throwable th) {
            th.printStackTrace();
            ShopCodeContext.getLog().error("Can not execute transaction. Client exception : " + payment, th);
            throw new PaymentException("Can not execute transaction. Client exception : " + payment, th);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.VoidTransaction trans = new paypal.payflow.VoidTransaction(
                payment.getTransactionAuthorizationCode(),
                createUserInfo(),
                connection,
                transRequestId
        );

        runTransaction(payment, trans, true);

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.CaptureTransaction trans = new paypal.payflow.CaptureTransaction(
                payment.getTransactionAuthorizationCode(),
                createUserInfo(),
                connection,
                transRequestId
        );

        runTransaction(payment, trans, false);

        return payment;

    }


    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.VoidTransaction trans = new paypal.payflow.VoidTransaction(
                payment.getTransactionAuthorizationCode(),
                createUserInfo(),
                connection,
                transRequestId
        );

        runTransaction(payment, trans, true);

        return payment;

    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment paymentIn) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        configurePayFlowSystem();

        final String transRequestId = paypal.payflow.PayflowUtility.getRequestId();
        payment.setTransactionReferenceId(transRequestId);

        paypal.payflow.PayflowConnectionData connection = new paypal.payflow.PayflowConnectionData();
        paypal.payflow.CreditTransaction trans = new paypal.payflow.CreditTransaction(
                payment.getTransactionAuthorizationCode(),
                createUserInfo(),
                connection, /*here can be invoice to change amount from original transaction */
                transRequestId
        );

        runTransaction(payment, trans, true);

        return payment;

    }


    /**
     * Create properties for cs client.
     *
     * @return propersties.
     */
    Map<String, String> getProperties() {
        if (props == null) {
            props = new HashMap<String, String>();
            for (String paramLabel : new String[]{
                    PF_HOST, PF_PORT, PF_TIMEOUT
                    , PF_LOG_FILENAME, PF_LOG_LEVEL, PF_LOG_FILESIZE, PF_LOG_ENABLED
                    , PF_PROXY_HOST, PF_PROXY_PORT, PF_PROXY_USER, PF_PROXY_PASSWORD, PF_PROXY_ENABLED
                    , PF_USER_NAME, PF_USER_PASSWORD, PF_VENDOR, PF_PARTNER

            }) {
                final String value = getParameterValue(paramLabel);
                if (value != null) {
                    props.put(paramLabel, value);
                }
            }
        }

        return props;
    }


}
