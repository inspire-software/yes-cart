package org.yes.cart.payment.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.codec.Base64;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.util.ShopCodeContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 * LiqPay payment gateway implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 12:53 PM
 */
public class LiqPayPaymentGatewayImpl extends AbstractGswmPaymentGatewayImpl
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


    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

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
    public String restoreOrderGuid(final Map privateCallBackParameters) {

        final LiqPayResponce liqPayResponce = getLiqPayResponce(privateCallBackParameters);

        if (liqPayResponce != null) {
            return liqPayResponce.getOrder_id();
        }

        return null;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccess(final Map<String, String> nvpCallResult) {

        final LiqPayResponce liqPayResponce = getLiqPayResponce(nvpCallResult);

        LOG.info("LiqPayPaymentGatewayImpl#isSuccess " + liqPayResponce);

        return liqPayResponce != null && "success".equalsIgnoreCase(liqPayResponce.getStatus());

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


        final String operationXml = MessageFormat.format(
                getXmlTemplate(),
                getParameterValue(LP_MERCHANT_ID),
                getParameterValue(LP_RESULT_URL),
                getParameterValue(LP_SERVER_URL),
                orderGuid,
                "" + amount,
                currencyCode,
                getDescription(payment),
                payment == null ? "" : payment.getBillingAddress() == null ? "" : StringUtils.defaultIfEmpty(payment.getBillingAddress().getPhoneList(), ""),
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
        if (payment != null) {
            for (PaymentLine line : payment.getOrderItems()) {
                stringBuilder.append(line.getSkuName());
                stringBuilder.append(" x ");
                stringBuilder.append(line.getQuantity());
                stringBuilder.append("\n");
            }
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
    public Payment createPaymentPrototype(final Map map) {
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

    /**
     * Get {@link XStream}  .
     *
     * @return {@link XStream} instance.
     */
    private XStream getXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("response", LiqPayResponce.class);
        return xStream;
    }


    /**
     * Get the {@link LiqPayResponce} from request parameter map.
     *
     * @param nvpCallResult request parameter map
     * @return {@link LiqPayResponce}.
     */
    private LiqPayResponce getLiqPayResponce(final Map<String, String> nvpCallResult) {

        final String operationXmlEncoded = nvpCallResult.get("operation_xml");

        final String signatureEncoded = nvpCallResult.get("signature");

        if (StringUtils.isNotBlank(operationXmlEncoded)) {

            final String merchantKey = getParameterValue(LP_MERCHANT_KEY);

            final String operationXml = new String(Base64.decode(operationXmlEncoded.getBytes()));

            final String signToCheck = createSignatire(merchantKey + operationXml + merchantKey);

            if (signToCheck.equals(signatureEncoded)) {

                return (LiqPayResponce) getXStream().fromXML(operationXml);

            } else {
                LOG.error(
                        MessageFormat.format(
                                "Calculated signature {0} not correspond to provided  {1} for xml {2}. Req. params {3}",
                                signToCheck,
                                signatureEncoded,
                                operationXml,
                                dump(nvpCallResult))

                );
            }

        } else {
            LOG.error("Cant get operation_xml. " + dump(nvpCallResult));
        }
        return null;

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

}
