package org.yes.cart.payment.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/14/12
 * Time: 6:13 PM
 * <p/>
 * Implementation of http://www.robokassa.ru payment gateway.
 */
public class RobokassaPaymentGatewayImpl extends AbstractGswmPaymentGatewayImpl
        implements PaymentGatewayExternalForm {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    // robokassa bypath parameters, that will be restored at call back
    static final String SHP_ORDER_ID = "SHP_ORDER_ID";

    // merchant password2
    static final String RB_MERCHANT_PASS2 = "RB_MERCHANT_PASS2";

    // merchant login
    static final String RB_MERCHANT_LOGIN = "RB_MERCHANT_LOGIN";

    // merchant password
    static final String RB_MERCHANT_PASS = "RB_MERCHANT_PASS";

    // order description
    static final String RB_ORDER_DESRIPTION = "RB_ORDER_DESRIPTION";

    // merchant password
    static final String RB_URL = "RB_URL";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, true,
            true, false,
            null
    );

    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        return "#"; //because we get form via javascript
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

        return String.valueOf(privateCallBackParameters.get(SHP_ORDER_ID));


    }

    /**
     * {@inheritDoc}
     * Will be called by ResultURL handler.
     */
    public boolean isSuccess(final Map<String, String> nvpCallResult) {

        final String outSumm = nvpCallResult.get("OutSum");
        final String invId = nvpCallResult.get("InvId");
        final String signatureValue = nvpCallResult.get("SignatureValue");
        final String shpOrderId = nvpCallResult.get(SHP_ORDER_ID);
        final String mrhPass2 = getParameterValue(RB_MERCHANT_PASS2);

        final String toCheck = outSumm + ":"
                + invId + ":"
                + mrhPass2 + ":"
                + SHP_ORDER_ID + "=" + shpOrderId;

        final String md5 = DigestUtils.md5Hex(toCheck);

        return signatureValue.equalsIgnoreCase(md5);

    }

    /**
     * {@inheritDoc}
     */
    public void handleNotification(HttpServletRequest request, HttpServletResponse response) {
        ;
    }


    /**
     * {@inheritDoc}
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        final String toSign = "" + amount + ":0:"
                + getParameterValue(RB_MERCHANT_PASS2) + ":"
                + SHP_ORDER_ID + "=" + orderGuid;

        final String operationJS = MessageFormat.format(
                "<script \n" +
                    "\tlanguage=''javascript'' \n" +
                    "\ttype=''text/javascript''\n" +
                    "\tsrc=''{0}?\n" +
                    "\t\tMrchLogin={1}&\n" +
                    "\t\tOutSum={2}&\n" +
                    "\t\tInvId={3}&\n" +
                    "\t\tDesc={4}&\n" +
                    "\t\tSignatureValue={5}&\n" +
                    "\t\tCulture={6}&\n" +
                    "\t\tIncCurrLabel={7}&\n" +
                    "\t\tSHP_ORDER_ID={8}&\n" +
                    "\t\tEncoding=utf-8''>\n" +
                    "</script>",
                getParameterValue(RB_URL),
                getParameterValue(RB_MERCHANT_LOGIN),
                "" + amount,
                0,
                getDescription(payment),
                DigestUtils.md5Hex(toSign),
                locale,
                payment.getOrderCurrency(),
                orderGuid
        );


        return operationJS;

    }


    /**
     * Get payment description.
     * @param payment payment
     * @return  patment description.
     */
    private String getDescription(final Payment payment) {

        return getParameterValue(RB_ORDER_DESRIPTION) + " N:" + payment.getOrderNumber();

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
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map map) {
        return new PaymentImpl();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "robokassaPaymentGateway";
    }


    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }


}
