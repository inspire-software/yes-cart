package org.yes.cart.payment.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Map;

import org.springframework.security.core.codec.Base64;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 12:53 PM
 */
public class LiqPayPaymentGatewayImp extends AbstractGswmPaymentGatewayImpl
        implements PaymentGatewayExternalForm {

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, true,
            true, false,
            null
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


    private static final Logger LOG = LoggerFactory.getLogger(LiqPayPaymentGatewayImp.class);

    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        return getParameterValue(LP_POST_URL);
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
    public String restoreOrderGuid(Map privateCallBackParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccess(Map<String, String> nvpCallResult) {

        final String operationXmlEncoded = nvpCallResult.get("operation_xml");
        final String signatureEncoded = nvpCallResult.get("signature");

        if (StringUtils.isNotBlank(operationXmlEncoded)) {

            final String merchantKey = getParameterValue(LP_MERCHANT_KEY);

            final String operationXml = new String(Base64.decode(operationXmlEncoded.getBytes()));

            final String signToCheck = createSignatire(merchantKey + operationXml + merchantKey);

            if (signToCheck.equals(signatureEncoded)) {

                /*DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document dDoc = builder.parse("E:/test.xml");

            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (NodeList) xPath.evaluate("/Request/@name", dDoc, XPathConstants.NODE);
            System.out.println(node.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
                //todo

            } else {
                LOG.error(
                        MessageFormat.format(
                                "Calculated signature {0} not correspond to provided  {1} for xml {2}. Req. params {3}",
                                signToCheck,
                                signatureEncoded,
                                operationXml,
                                dump(nvpCallResult))

                        );
                return false;
            }

        } else {

            LOG.error("Cant get operation_xml. " + dump(nvpCallResult));

        }


        return false;
    }


    /**
     * Dump map value into String.
     * @param map given map
     * @return  dump map as string
     */
    public static String dump(Map<?, ?> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" : ");
            stringBuilder.append(entry.getValue());
        }
        return stringBuilder.toString();
    }




    public void handleNotification(HttpServletRequest request, HttpServletResponse response) {
        ;
    }

    private final String xmlTemplate =
            "<request>\n" +
                    "      <version>1.2</version>\n" +
                    "      <merchant_id>{0}</merchant_id>\n" +
                    "      <result_url>{1}</result_url>\n" +
                    "      <server_url>{2}</server_url>\n" +
                    "      <order_id>{3}</order_id>\n" +
                    "      <amount>{4}</amount>\n" +
                    "      <currency>{5}</currency>\n" +
                    "      <description>{6}</description>\n" +
                    "      <default_phone>{7}</default_phone>\n" +
                    "      <pay_way>{8}</pay_way>\n" +
                    "      <goods_id>1234</goods_id>\n" +
                    "</request>";

    /**
     * Get xml template .
     *
     * @return xml template
     */
    String getXmlTemplate() {
        return xmlTemplate;
    }


    /**
     * {@inheritDoc}
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {


        final String operationXml = MessageFormat.format(
                getXmlTemplate(),
                getParameterValue(LP_MERCHANT_ID),
                getParameterValue(LP_RESULT_URL),
                getParameterValue(LP_SERVER_URL),
                orderGuid,
                "" + amount,
                currencyCode,
                getDescription(payment),
                payment.getBillingAddress() == null ? "" : StringUtils.defaultIfEmpty(payment.getBillingAddress().getPhoneList(), ""),
                getParameterValue(LP_PAYWAY_URL)
        );

        LOG.info(" Raw liqpay xml [" + operationXml + "]");

        final String merchantKey = getParameterValue(LP_MERCHANT_KEY);
        final String sign = createSignatire(merchantKey + operationXml + merchantKey);


        return getHiddenFiled("operation_xml", new String(Base64.encode(operationXml.getBytes())))
                + getHiddenFiled("signature", sign);
    }


    /**
     * Create signatore for given xml.
     *
     * @param xmlToSign xml to sign.
     * @return signed xml.
     */
    private String createSignatire(final String xmlToSign) {
        return new String(
                Base64.encode(
                        DigestUtils.sha(xmlToSign)
                )
        ).trim();
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
            stringBuilder.append(line.getSkuName());
            stringBuilder.append(" x ");
            stringBuilder.append(line.getQuantity());
            stringBuilder.append("\n");
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
    public Payment createPaymentPrototype(Map map) {
        return new PaymentImpl();
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
