/*
 * Copyright 2009 Inspire-Software.com
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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.ConfigurablePaymentGateway;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.payment.service.impl.PaymentGatewayConfigurationVisitorImpl;
import org.yes.cart.utils.MessageFormatUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * User: inspiresoftware
 * Date: 29/12/2020
 * Time: 12:47
 */
public class PaySeraCheckoutPaymentGatewayImplIntTest extends PaymentModuleDBTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(PaySeraCheckoutPaymentGatewayImpl.class);

    private PaymentProcessorSurrogate paymentProcessor;
    private PaymentGatewayExternalForm paySeraPaymentGateway;
    private CustomerOrderPaymentService customerOrderPaymentService;

    private Boolean enabled;

    private String merchantId;
    private String merchantPwd;

    private boolean isTestAllowed() {

        enabled = Boolean.valueOf(System.getProperty("testPgPaySera"));

        merchantId = System.getProperty("testPgPaySeraID");
        merchantPwd = System.getProperty("testPgPaySeraPWD");

        return enabled;

    }

    @Override
    protected String testContextName() {
        return "test-payment-module-paysera.xml";
    }

    @Override
    @Before
    public void setUp() throws Exception {

        final boolean allowed = isTestAllowed();

        if (!allowed) {
            LOG.warn("\n\n" +
                    "***\n" +
                    "PaySera integration test is DISABLED.\n" +
                    "You can enable test in /env/maven/${env}/config-module-paysera-[on/off].properties\n" +
                    "Set:\n" +
                    "testPgPaySera=true\n" +
                    "testPgPaySeraID=123455\n" +
                    "testPgPaySeraPWD=123455\n" +
                    "***\n\n\n");
        }

        assumeTrue(allowed);

        if (allowed) {
            super.setUp();

            customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean("customerOrderPaymentService");
            paySeraPaymentGateway = (PaymentGatewayExternalForm) ctx().getBean("paySeraCheckoutPaymentGateway");

            final Map<String, Object> context = new HashMap<>();
            context.put("shopCode", createShop().getCode());
            context.put("pgLabel", "paySeraCheckoutPaymentGatewayLabel");
            context.put("label", "paySeraCheckoutPaymentGateway");
            ((ConfigurablePaymentGateway) paySeraPaymentGateway).accept(new PaymentGatewayConfigurationVisitorImpl(context));

            paymentProcessor = new PaymentProcessorSurrogate(customerOrderPaymentService, paySeraPaymentGateway);


            final Map<String, PaymentGatewayParameter> params = new HashMap<>();
            for (final PaymentGatewayParameter param : paySeraPaymentGateway.getPaymentGatewayParameters()) {
                params.put(param.getLabel(), param);
            }

            final PaymentGatewayParameter projectId = params.get("PSC_PROJECTID");
            final PaymentGatewayParameter signPwd = params.get("PSC_SIGN_PASSWORD");

            projectId.setValue(merchantId);
            signPwd.setValue(merchantPwd);

            paySeraPaymentGateway.updateParameter(projectId);
            paySeraPaymentGateway.updateParameter(signPwd);

        }
    }


    @Test
    public void testGetPaymentGatewayParameters() throws Exception {
        assumeTrue(isTestAllowed());
        //not sure is proxy will be used or not
        for (PaymentGatewayParameter parameter : paySeraPaymentGateway.getPaymentGatewayParameters()) {
            assertEquals("paySeraCheckoutPaymentGateway", parameter.getPgLabel());
        }

        dumpDataBase("psdata" , "TPAYMENTGATEWAYPARAMETER");
    }

    @Test
    public void testIntegration() throws Exception {

        final CustomerOrder order = createCustomerOrder(UUID.randomUUID().toString(), "EUR", Location.UK);
        final Customer customer = order.getCustomer();

        // Perform post onto https://www.paysera.com/pay/ with "orderGuid" POST parameter

        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                order,
                true,
                false,
                Collections.emptyMap(),
                PaymentGateway.AUTH).get(0);

        // receive redirect to external form to PaySera or goto /paymentresult?orderNum=XXXXXXX&hint=failed

        final String submitBtnValue = paySeraPaymentGateway.getSubmitButton(order.getLocale());
        final String postActionUrl = paySeraPaymentGateway.getPostActionUrl();

        final String htmlFragment = paySeraPaymentGateway.getHtmlForm(
                order.getFirstname() + " " + order.getLastname(),
                order.getLocale(),
                order.getOrderTotal(),
                order.getCurrency(),
                order.getOrdernum(),
                payment);


        assertNotNull(submitBtnValue);
        assertNotNull(postActionUrl);
        assertNotNull(htmlFragment);

        final String testForm = MessageFormatUtils.format(
                "<html><body><form method=\"POST\" action=\"{}\" class=\"form-horizontal\">\n" +
                        "{}\n" +
                        "<div id=\"paymentDiv\">\n" +
                        "{}" +
                        "</div></form></body></html>",
                postActionUrl,
                htmlFragment,
                submitBtnValue);


        LOG.warn("FORM: {}", testForm);

        final Map<String, String[]> cb = getCallbackResponse();

        final CallbackAware.Callback cbObj = ((CallbackAware) paySeraPaymentGateway).convertToCallback(cb, false);

        assertNotNull(cbObj);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, cbObj.getOperation());
        assertTrue(cbObj.isValidated());

        final Payment paymentCb = paymentProcessor.createPaymentsToAuthorize(
                order,
                true,
                false,
                cb,
                PaymentGateway.AUTH_CAPTURE).get(0);

        assertNotNull(paymentCb);
        assertEquals(CallbackAware.CallbackResult.OK.getStatus(), paymentCb.getPaymentProcessorResult());
        assertEquals(CallbackAware.CallbackResult.OK.isSettled(), paymentCb.isPaymentProcessorBatchSettlement());
        assertEquals("EUR", paymentCb.getOrderCurrency());
        assertEquals(cbObj.getAmount(), paymentCb.getPaymentAmount());
        assertEquals("429044346", paymentCb.getTransactionReferenceId());
        assertEquals("429044346", paymentCb.getTransactionAuthorizationCode());
        assertEquals("Name Last name", paymentCb.getCardHolderName());
        assertEquals("1 EMA wallet 429044346 TEST1234567890", paymentCb.getTransactionOperationResultMessage());

    }

    Map<String, String[]> getCallbackResponse() {
        final String data =
                "c3VibWl0X3g9ODEmc3VibWl0X3k9MjImcF9sYXN0bmFtZT1Eb3UmcF96aXA9TlcxKzZYRSZwYXl0ZXh0PUF0bGlrdGFzK21vayVDNCU5N2ppbWFzK3UlQzUlQkUrdSVDNSVCRXNha3l0YXMrcHJla2VzK2h0dHAlM0ElMkYlMkZsb2NhbGhvc3QrcGFyZHVvdHV2J" +
                "UM0JTk3amUrJTI4dSVDNSVCRXNha3ltbytudW1lcmlzKy0rYWU4YWQwNWMtZDkxZi00NWUyLTk1MzgtN2ViYjE3MDYwMmZmJTI5JTNBKyUyQStwcm9kdWN0K3NrdSsxK3gzKyUyQStwcm9kdWN0K3NrdSsyK3gxKyUyQStOZXh0K2RleStkZWxpdmVyeSt4MSslMkErcHJ" +
                "vZHVjdCtza3UrMSt4MislMkErTmV4dCtkZXkrZGVsaXZlcnkreDEmcF9jaXR5PUxvbmRvbiZwX2VtYWlsPWpvaG4uZG91LWMzNDYxM2IzLWVhMmItNDllOC1iNmNjLWNlZGNhN2JhMzM3ZiU0MGRvbWFpbi5jb20mY3VycmVuY3k9RVVSJmxhbmc9ZW5nJnByb2plY3RpZ" +
                "D0xODgzNzUmYW1vdW50PTEyOTM1JnBfZmlyc3RuYW1lPUpvaG4mdGVzdD0xJm9yZGVyaWQ9YWU4YWQwNWMtZDkxZi00NWUyLTk1MzgtN2ViYjE3MDYwMmZmJnZlcnNpb249MS42JnBfY291bnRyeWNvZGU9R0ImcF9zdHJlZXQ9MjIxQitCYWtlcitTdHJlZXQrJm9yaWd" +
                "pbmFsX3BheXRleHQ9QXRsaWt0YXMrbW9rJUM0JTk3amltYXMrdSVDNSVCRSt1JUM1JUJFc2FreXRhcytwcmVrZXMrJTVCc2l0ZV9uYW1lJTVEK3BhcmR1b3R1diVDNCU5N2plKyUyOHUlQzUlQkVzYWt5bW8rbnVtZXJpcystKyU1Qm9yZGVyX25yJTVEJTI5JTNBJTBBJ" +
                "TJBK3Byb2R1Y3Qrc2t1KzEreDMlMEElMkErcHJvZHVjdCtza3UrMit4MSUwQSUyQStOZXh0K2RleStkZWxpdmVyeSt4MSUwQSUyQStwcm9kdWN0K3NrdSsxK3gyJTBBJTJBK05leHQrZGV5K2RlbGl2ZXJ5K3gxJTBBJnR5cGU9RU1BJnBheW1lbnQ9d2FsbGV0JmNvdW5" +
                "0cnk9R0ImX2NsaWVudF9sYW5ndWFnZT1lbmcmbV9wYXlfcmVzdG9yZWQ9NDI5MDQ0MzQ2JmZyYW1lPTAmYWNjb3VudD1URVNUMTIzNDU2Nzg5MCZzdGF0dXM9MSZwYXlhbW91bnQ9MTI5MzUmcGF5Y3VycmVuY3k9RVVSJnJlcXVlc3RpZD00MjkwNDQzNDYmbmFtZT1OY" +
                "W1lJnN1cmVuYW1lPUxhc3QrbmFtZSZwYXllcl9pcF9jb3VudHJ5PUdCJnBheWVyX2NvdW50cnk9R0I=";
        final String ss1 = "59053ea89a474f5303b728a29b3bbf20";
        final String ss2 = "VdTS5cI-oj_zquvhJka3qCY9bFbbpBirx6YfMYa6DOttwZPFNs7lHotC0aHmgKEEtIc1nOWB-jHFgfakthxcEgoOv6jBUvnOIqKHctiRZFS7icFFs_rJMeM_kZF_js7bMBvfZzmqZEBwUtHEqyS6F-GGTVHMS0HhQ3WsMioIbjA=";

        final Map<String, String[]> cb = new LinkedHashMap<>();
        cb.put("data", new String[] { data });
        cb.put("ss1", new String[] { ss1 });
        cb.put("ss2", new String[] { ss2 });
        return cb;
    }


    @Override
    public String getVisaCardNumber() {
        return "4111111111111111";
    }

}