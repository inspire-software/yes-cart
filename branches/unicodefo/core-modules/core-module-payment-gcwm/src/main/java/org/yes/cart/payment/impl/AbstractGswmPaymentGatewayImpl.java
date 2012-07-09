package org.yes.cart.payment.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayParameterService;
import org.yes.cart.util.ShopCodeContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 25-Dec-2011
 * Time: 14:12:54
 */
public abstract class AbstractGswmPaymentGatewayImpl implements PaymentGateway {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;

    /**
     * {@inheritDoc}
     */

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel());
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
     * Work around promlem with wicket param values, when it can return
     * parameter value as string or as array of strings with single value.
     * This behavior depends from url encoding strategy
     * @param param parameters
     * @return value
     */
    public static String getSingleValue(final Object param) {
        if (param instanceof String) {
            return (String) param;
        } else if (param instanceof String[]) {
            if (((String[])param).length > 0) {
                return ((String [])param)[0];
            }
        }
        return null;

    }
    

    /**
     * {@inheritDoc}
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
        final Collection<PaymentGatewayParameter> values = getPaymentGatewayParameters();
        for (PaymentGatewayParameter keyValue : values) {
            if (keyValue.getLabel().equals(valueLabel)) {
                return keyValue.getValue();
            }
        }
        return null;
    }



    /**
     * {@inheritDoc}
     */
    protected String getHiddenFiled(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> doDoExpressCheckoutPayment(String token, String payerId, BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getExpressCheckoutDetails(String token) throws IOException {
        return null;  //nothing
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


}
