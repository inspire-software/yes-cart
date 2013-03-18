package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractPaymentGatewayImpl implements PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;


    /**
    * {@inheritDoc}
    */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {
        return getHtmlForm(cardHolderName, locale);
    }


    /**
     * Default implementation.
     * @param cardHolderName card holder name.
     * @param locale locale
     * @return part of html form. 
     */
    protected String getHtmlForm(final String cardHolderName, final String locale) {
        String htmlForm = getParameterValue("htmlForm");
        if (htmlForm != null) {
            return htmlForm.replace("@CARDHOLDERNAME@", cardHolderName);
        }
        return StringUtils.EMPTY;
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
    public Payment createPaymentPrototype(final Map parametersMap) {

        final Payment payment = new PaymentImpl();

        payment.setCardHolderName(getSingleValue(parametersMap.get("ccHolderName")));
        payment.setCardNumber(getSingleValue(parametersMap.get("ccNumber")));
        payment.setCardExpireMonth(getSingleValue(parametersMap.get("ccExpireMonth")));
        payment.setCardExpireYear(getSingleValue(parametersMap.get("ccExpireYear")));
        payment.setCardCvv2Code(getSingleValue(parametersMap.get("ccSecCode")));
        payment.setCardType(getSingleValue(parametersMap.get("ccType")));


        return payment;
    }

    /**
     * Work around problem with wicket param values, when it can return
     * parameter value as string or as array of strings with single value.
     * This behavior depends from url encoding strategy
     * @param param parameters
     * @return value
     */
    public String getSingleValue(final Object param) {
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
     * {@inheritDoc}
     */
    public void setPaymentGatewayParameterService(
            final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }


}
