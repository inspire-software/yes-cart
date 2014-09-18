/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.payment.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayParameterService;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 
 */
public abstract class AbstractCappPaymentGatewayImpl implements PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;


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
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {
        return getHtmlForm(cardHolderName, locale);
    }


    /**
     * Dafault implementation.
     * @param cardHolderName card holder name.
     * @param locale locale
     * @return html form.
     */
    protected String getHtmlForm(final String cardHolderName, final String locale) {
        String htmlForm = getParameterValue("htmlForm_" + locale);
        if (htmlForm == null) {
            htmlForm = getParameterValue("htmlForm");
        }
        if (htmlForm != null) {
            return htmlForm.replace("@CARDHOLDERNAME@", cardHolderName);
        }
        return StringUtils.EMPTY;
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
    public Payment createPaymentPrototype(final Map parametersMap) {
        final Payment payment = new PaymentImpl();

        payment.setCardHolderName(getSingleValue(parametersMap.get("ccHolderName")));
        payment.setCardNumber(getSingleValue(parametersMap.get("ccNumber")));
        payment.setCardExpireMonth(getSingleValue(parametersMap.get("ccExpireMonth")));
        payment.setCardExpireYear(getSingleValue(parametersMap.get("ccExpireYear")));
        payment.setCardCvv2Code(getSingleValue(parametersMap.get("ccSecCode")));
        payment.setCardType(getSingleValue(parametersMap.get("ccType")));
        payment.setShopperIpAddress(getSingleValue(parametersMap.get(PaymentMiscParam.CLIENT_IP)));

        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            displayMap("Payment prototype from map", parametersMap);
        }


        return payment;
    }

    /**
     * Work around problem with wicket param values, when it can return
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
     * Displays the content of the Map object.
     *
     * @param header Header text.
     * @param map    Map object to display.
     */
    protected void displayMap(final String header, final Map<String, String> map) {
        StringBuilder dest = new StringBuilder();
        dest.append(header);
        if (map != null && !map.isEmpty()) {
            List<String> keys = new ArrayList<String>(map.keySet());
            Collections.sort(keys);

            Iterator<String> iter = keys.iterator();

            String key, val;
            while (iter.hasNext()) {
                key = iter.next();
                val = getSingleValue(map.get(key));
                dest.append(key);
                dest.append('=');
                dest.append(val);
                dest.append('\n');
            }
        }
        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug(dest.toString());
        }

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
     * Get street address.
     *
     * @param addreLine1 line 1
     * @param addreLine2 line 2
     * @return street address
     */
    protected String getStreetAddress(final String addreLine1, final String addreLine2) {
        return addreLine1
                + (
                StringUtils.isNotBlank(addreLine2) ?
                        " " + addreLine2 :
                        StringUtils.EMPTY)
                ;

    }


    protected String getHiddenField(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }


}
