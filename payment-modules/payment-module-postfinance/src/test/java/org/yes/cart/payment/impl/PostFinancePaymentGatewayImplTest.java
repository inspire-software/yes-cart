/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.junit.Test;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentAddressImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 08/10/2015
 * Time: 18:08
 */
public class PostFinancePaymentGatewayImplTest {


    private Map<String, String> getHtmlFormParamsBasic() {

        final Map<String, String> params = new HashMap<>();
        params.put(PostFinancePaymentGatewayImpl.PF_POST_URL, "http://www.postfinance.com/pay");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_HOME, "http://mydomain.com/result");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_CATALOG, "http://mydomain.com/result");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_ACCEPT, "http://mydomain.com/result?hint=ok");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_DECLINE, "http://mydomain.com/result?hint=de");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_EXCEPTION, "http://mydomain.com/result?hint=ex");
        params.put(PostFinancePaymentGatewayImpl.PF_RESULT_URL_CANCEL, "http://mydomain.com/result?hint=ca");
        params.put(PostFinancePaymentGatewayImpl.PF_PSPID, "ID0001");
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_IN, "shain");
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_OUT, "shaout");
        return params;

    }

    @Test
    public void testGetHtmlFormNoAddress() throws Exception {

        final Map<String, String> params = getHtmlFormParamsBasic();

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='03DC7B3A471215058CFC4D14F201D87CF701B941'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }

    @Test
    public void testGetHtmlFormWithAddressOneLine() throws Exception {

        final Map<String, String> params = getHtmlFormParamsBasic();

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };


        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "EUR",
                "234-1324-1324-1324abc-abc",
                createTestPayment(true, false)
        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='EUR'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324abc-abc'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123b'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='31AADA74C665E6F3B94F72DB6BED8989F11A1B82'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }

    @Test
    public void testGetHtmlFormWithAddressTwoLines() throws Exception {

        final Map<String, String> params = getHtmlFormParamsBasic();

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "EUR",
                "234-1324-1324-1324abc-abc",
                createTestPayment(true, true)
        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='EUR'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324abc-abc'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='FC59E2A8FE1DE662EA53849847BB96D670A73D22'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }

    private Map<String, String> getHtmlFormParamsItemised() {

        final Map<String, String> params = getHtmlFormParamsBasic();
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED, "true");
        return params;

    }


    @Test
    public void testGetHtmlFormItemisedNoAddress() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='5.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVATCODE1' value='25.0'>\n" +
                        "<input type='hidden' name='ITEMVATCODE2' value='0.0'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='2FE15F62625775D690066416BD424F55F463AD83'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }


    @Test
    public void testGetHtmlFormItemisedNoAddressDiscount() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Payment payment = createTestPayment(false, false);
        payment.setPaymentAmount(new BigDecimal(8));

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                new BigDecimal(8),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                 payment
        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='800'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='4.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVATCODE1' value='25.0'>\n" +
                        "<input type='hidden' name='ITEMVATCODE2' value='0.0'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='DA59B8F8D3E9585588E8BB54DA175970124DF442'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }



    @Test
    public void testGetHtmlFormItemisedWithCategories() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED_ITEM_CAT, "Item");
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED_SHIP_CAT, "Shipping");


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMCATEGORY1' value='Item'>\n" +
                        "<input type='hidden' name='ITEMCATEGORY2' value='Shipping'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='5.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVATCODE1' value='25.0'>\n" +
                        "<input type='hidden' name='ITEMVATCODE2' value='0.0'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='6E994496299064381B115EA9D0D3D8F0D44F41AA'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }


    @Test
    public void testGetHtmlFormItemisedWithCategoriesAndTaxAmount() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED_ITEM_CAT, "Item");
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED_SHIP_CAT, "Shipping");
        params.put(PostFinancePaymentGatewayImpl.PF_ITEMISED_USE_TAX_AMOUNT, "true");


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMCATEGORY1' value='Item'>\n" +
                        "<input type='hidden' name='ITEMCATEGORY2' value='Shipping'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='5.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVAT1' value='1.00'>\n" +
                        "<input type='hidden' name='ITEMVAT2' value='0.00'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='961956DF6425AF437BA00D4BDFC5D9A2BFBBCE3F'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }

    @Test
    public void testGetHtmlFormItemisedWithAddressOneLine() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(true, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='5.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVATCODE1' value='25.0'>\n" +
                        "<input type='hidden' name='ITEMVATCODE2' value='0.0'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123b'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='86CBF9A6FD81EF04642D73788393365F8FE1D55C'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);



    }

    @Test
    public void testGetHtmlFormItemisedWithAddressTwoLines() throws Exception {

        final Map<String, String> params = getHtmlFormParamsItemised();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(true, true)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='ITEMID1' value='code2'>\n" +
                        "<input type='hidden' name='ITEMID2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMNAME1' value='name2'>\n" +
                        "<input type='hidden' name='ITEMNAME2' value='ship2'>\n" +
                        "<input type='hidden' name='ITEMPRICE1' value='5.0000'>\n" +
                        "<input type='hidden' name='ITEMPRICE2' value='0.0000'>\n" +
                        "<input type='hidden' name='ITEMQUANT1' value='2'>\n" +
                        "<input type='hidden' name='ITEMQUANT2' value='1'>\n" +
                        "<input type='hidden' name='ITEMVATCODE1' value='25.0'>\n" +
                        "<input type='hidden' name='ITEMVATCODE2' value='0.0'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='31E48B4A837DD6F09A2B41615CB37F2CA1AD957F'>\n" +
                        "<input type='hidden' name='TAXINCLUDED1' value='1'>\n" +
                        "<input type='hidden' name='TAXINCLUDED2' value='1'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);



    }


    private Map<String, String> getHtmlFormParamsInvoice() {

        final Map<String, String> params = getHtmlFormParamsBasic();
        params.put(PostFinancePaymentGatewayImpl.PF_DELIVERY_AND_INVOICE_ON, "true");
        return params;

    }


    @Test
    public void testGetHtmlFormInvoiceNoAddress() throws Exception {

        final Map<String, String> params = getHtmlFormParamsInvoice();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(false, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='03DC7B3A471215058CFC4D14F201D87CF701B941'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);

    }


    @Test
    public void testGetHtmlFormInvoiceWithAddressOneLine() throws Exception {

        final Map<String, String> params = getHtmlFormParamsInvoice();


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(true, false)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_LINE1' value='In the middle of'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_NUMBER' value='123b'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_LINE1' value='In the middle of'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_NUMBER' value='123b'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123b'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='A85B7608A717D533D1E2A9541EA88126878673FA'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);



    }

    @Test
    public void testGetHtmlFormInvoiceWithAddressOneLineCustomRegEx() throws Exception {

        final Map<String, String> params = getHtmlFormParamsInvoice();
        params.put(PostFinancePaymentGatewayImpl.PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX, "\\sNumber");

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Payment payment = createTestPayment(true, false);
        payment.getBillingAddress().setAddrline1("Street Number");

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                payment

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_LINE1' value='Street'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_NUMBER' value='Number'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_LINE1' value='Street'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_NUMBER' value='Number'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='Street Number'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='806A08C422655931E13D177F5B71DB207379ABD3'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);



    }

    @Test
    public void testGetHtmlFormInvoiceWithAddressTwoLines() throws Exception {

        final Map<String, String> params = getHtmlFormParamsInvoice();
        params.put(PostFinancePaymentGatewayImpl.PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER, "true");


        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImpl.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTestPayment(true, true)

        );

        assertEquals("<input type='hidden' name='ACCEPTURL' value='http://mydomain.com/result?hint=ok'>\n" +
                        "<input type='hidden' name='AMOUNT' value='1000'>\n" +
                        "<input type='hidden' name='CANCELURL' value='http://mydomain.com/result?hint=ca'>\n" +
                        "<input type='hidden' name='CATALOGURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='CN' value='holder  name'>\n" +
                        "<input type='hidden' name='COM' value='code2x2, ship2, bob@doe.com, 1234'>\n" +
                        "<input type='hidden' name='CURRENCY' value='USD'>\n" +
                        "<input type='hidden' name='DECLINEURL' value='http://mydomain.com/result?hint=de'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_LINE1' value='In the middle of'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_POSTAL_STREET_NUMBER' value='123'>\n" +
                        "<input type='hidden' name='ECOM_BILLTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_CITY' value='Nowhere'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_COUNTRYCODE' value='NA'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_POSTALCODE' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_LINE1' value='In the middle of'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_POSTAL_STREET_NUMBER' value='123'>\n" +
                        "<input type='hidden' name='ECOM_SHIPTO_TELECOM_PHONE_NUMBER' value='123412341234'>\n" +
                        "<input type='hidden' name='EMAIL' value='bob@doe.com'>\n" +
                        "<input type='hidden' name='EXCEPTIONURL' value='http://mydomain.com/result?hint=ex'>\n" +
                        "<input type='hidden' name='HOMEURL' value='http://mydomain.com/result'>\n" +
                        "<input type='hidden' name='LANGUAGE' value='en'>\n" +
                        "<input type='hidden' name='OPERATION' value='SAL'>\n" +
                        "<input type='hidden' name='ORDERID' value='234-1324-1324-1324sdf-sdf'>\n" +
                        "<input type='hidden' name='OWNERADDRESS' value='In the middle of 123'>\n" +
                        "<input type='hidden' name='OWNERCTY' value='NA'>\n" +
                        "<input type='hidden' name='OWNERTELNO' value='123412341234'>\n" +
                        "<input type='hidden' name='OWNERTOWN' value='Nowhere'>\n" +
                        "<input type='hidden' name='OWNERZIP' value='NA1 NA1'>\n" +
                        "<input type='hidden' name='PSPID' value='ID0001'>\n" +
                        "<input type='hidden' name='SHASIGN' value='2EEAD9A99A95806835B24CDF436EF581B9DE747E'>\n" +
                        "<input type='hidden' name='USERID' value='bob@doe.com'>\n",
                htmlFormPart);



    }


    private Payment createTestPayment(boolean withAddress, boolean addr2IsNumber) {

        final List<PaymentLine> orderItems = new ArrayList<PaymentLine>() {{
            add(new PaymentLineImpl("code2", "name'2", new BigDecimal(2), new BigDecimal(5), new BigDecimal(2), false));
            add(new PaymentLineImpl("ship2", "ship2", BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, true));
        }};


        final Payment payment = new PaymentImpl();

        payment.setOrderNumber("1234");
        payment.setOrderItems(orderItems);
        payment.setOrderCurrency("USD");
        payment.setOrderLocale("en");
        payment.setBillingEmail("bob@doe.com");
        payment.setPaymentAmount(BigDecimal.TEN);

        if (withAddress) {
            final PaymentAddress address = new PaymentAddressImpl();
            if (addr2IsNumber) {
                address.setAddrline1("In the middle of'");
                address.setAddrline2("123");
            } else {
                address.setAddrline1("In the middle of' 123b");
            }
            address.setCity("No'where");
            address.setStateCode("NA");
            address.setCountryCode("NA");
            address.setPostcode("NA1 NA1");
            address.setPhone1("123412341234");
            payment.setBillingAddress(address);
        }

        return payment;

    }


    @Test
    public void testIsSuccess() {


        testIsSuccessWithStatus("5", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("9", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("51", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("91", Payment.PAYMENT_STATUS_OK);
        testIsSuccessWithStatus("0", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("2", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("52", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("92", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("93", Payment.PAYMENT_STATUS_FAILED);
        testIsSuccessWithStatus("zxcvzxcvzxcv", Payment.PAYMENT_STATUS_FAILED);


    }


    private void testIsSuccessWithStatus(final String status, final String expectedStatus) {



        final Map<String, String> params = new HashMap<>();
        params.put(PostFinancePaymentGatewayImpl.PF_SHA_OUT, "Mysecretsig1875!?");

        final PostFinancePaymentGatewayImpl gatewayImpl = new PostFinancePaymentGatewayImpl() {

            @Override
            public String getParameterValue(String valueLabel) {
                if (params.containsKey(valueLabel)) {
                    return params.get(valueLabel);
                }
                return "";
            }
        };

        Map<String, String> callBackresult = new HashMap<String, String>() {{

            put("ACCEPTANCE", "1234");
            put("amount", "15");
            put("BRAND", "VISA");
            put("CARDNO", "XXXXXXXXXXXX1111");
            put("currency", "EUR");
            put("NCERROR", "0");
            put("orderID", "12");
            put("PAYID", "32100123");
            put("PM", "CreditCard");
            put("STATUS", status);
            put("SHASIGN", "TESTINVALID");

        }};

        // Always should fail with invalid signature
        assertEquals(Payment.PAYMENT_STATUS_FAILED, gatewayImpl.getExternalCallbackResult(callBackresult, false).getStatus());
        final CallbackAware.Callback badCallback = gatewayImpl.convertToCallback(callBackresult, false);
        assertEquals(CallbackAware.CallbackOperation.INVALID, badCallback.getOperation());
        assertNull(badCallback.getOrderGuid());
        assertNull(badCallback.getAmount());
        assertFalse(badCallback.isValidated());

        // Forced will not fail
        assertEquals(expectedStatus, gatewayImpl.getExternalCallbackResult(callBackresult, true).getStatus());
        final CallbackAware.Callback forcedCallback = gatewayImpl.convertToCallback(callBackresult, true);
        assertEquals("12", forcedCallback.getOrderGuid());
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, forcedCallback.getOperation());
        assertEquals(new BigDecimal("15"), forcedCallback.getAmount());
        assertFalse(forcedCallback.isValidated());


        final Map<String, String> sorted = new TreeMap<>();
        gatewayImpl.copyHttpParamsAndRemoveSignature(callBackresult, sorted);
        final String validSignature = new PostFinancePaymentGatewayImpl().sha1sign(sorted, "Mysecretsig1875!?");
        callBackresult.put("SHASIGN", validSignature);


        assertEquals(expectedStatus, gatewayImpl.getExternalCallbackResult(callBackresult, false).getStatus());
        final CallbackAware.Callback goodCallback = gatewayImpl.convertToCallback(callBackresult, false);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, goodCallback.getOperation());
        assertEquals("12", goodCallback.getOrderGuid());
        assertEquals(new BigDecimal("15"), goodCallback.getAmount());
        assertTrue(goodCallback.isValidated());
    }

    @Test
    public void testSetValueIfNotNull() throws Exception {

        final PostFinancePaymentGatewayImpl pg = new PostFinancePaymentGatewayImpl();

        final Map<String, String> params = new HashMap<>();

        pg.setValueIfNotNull(params, "key", null);

        assertFalse(params.containsKey("key"));

        pg.setValueIfNotNull(params, "key", "value");

        assertEquals("value", params.get("key"));

        pg.setValueIfNotNull(params, "key2", "value's");

        assertEquals("values", params.get("key2"));

    }
}