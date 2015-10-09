package org.yes.cart.payment.impl;

import org.apache.commons.codec.binary.Hex;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/10/2015
 * Time: 14:43
 */
public abstract class AbstractPostFinancePaymentGatewayImpl implements ConfigurablePaymentGateway, PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;

    private String shopCode;

    /**
     * {@inheritDoc}
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getName(final String locale) {
        String pgName = getParameterValue("name_" + locale);
        if (pgName == null) {
            pgName = getParameterValue("name");
        }
        if (pgName == null) {
            pgName = getLabel();
        }
        return pgName;
    }


    /**
     * {@inheritDoc}
     */

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel(), shopCode);
        }
        return allParameters;
    }

    /**
     * {@inheritDoc}
     */

    public void deleteParameter(final String parameterLabel) {
        paymentGatewayParameterService.deleteByLabel(getLabel(), parameterLabel);
    }

    /**
     * {@inheritDoc}
     */

    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.create(paymentGatewayParameter);
    }

    /**
     * {@inheritDoc}
     */

    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.update(paymentGatewayParameter);
    }


    /**
     * Parameter service for given gateway.
     *
     * @param paymentGatewayParameterService service
     */
    public void setPaymentGatewayParameterService(
            final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }


    /**
     * Get the parameter value from given collection.
     *
     * @param valueLabel key to search
     * @return value or null if not found
     */
    public String getParameterValue(final String valueLabel) {
        if (valueLabel == null || valueLabel.startsWith("#")) {
            return null; // Need to prevent direct access to Shop specific attributes
        }
        if (shopCode != null && !"DEFAULT".equals(shopCode)) {
            final String shopSpecific = getParameterValueInternal("#" + shopCode + "_" + valueLabel);
            if (shopSpecific != null) {
                return shopSpecific;
            }
        }
        return getParameterValueInternal(valueLabel);
    }

    private String getParameterValueInternal(final String valueLabel) {
        final Collection<PaymentGatewayParameter> values = getPaymentGatewayParameters();
        for (PaymentGatewayParameter keyValue : values) {
            if (keyValue.getLabel().equals(valueLabel)) {
                return keyValue.getValue();
            }
        }
        return null;
    }


    protected String getHiddenField(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }

    public Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    public Map<String, String> doDoExpressCheckoutPayment(String token, String payerId, BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    public Map<String, String> getExpressCheckoutDetails(String token) throws IOException {
        return null;  //nothing
    }

    protected String sha1sign(final Map<String, String> params, final String secret) {

        final StringBuilder all = new StringBuilder();
        for (final Map.Entry<String, String> entry : params.entrySet()) {

            all.append(entry.getKey().toUpperCase()).append("=").append(entry.getValue()).append(secret);

        }
        try {
            final Charset charset = Charset.forName("UTF-8");
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return new String(Hex.encodeHex(digest.digest(all.toString().getBytes(charset)))).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return "SHA-1 not available";
        }

    }

    /**
     * Dump map value into String.
     *
     * @param map given map
     * @return dump map as string
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


    /**
     * {@inheritDoc}
     */
    public void accept(final PaymentGatewayConfigurationVisitor visitor) {
        this.shopCode = visitor.getConfiguration("shopCode");
    }

}
