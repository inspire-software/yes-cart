package org.yes.cart.payment.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayConfigurationVisitor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 08/10/2015
 * Time: 17:11
 */
public class AbstractPostFinancePaymentGatewayImplTest {

    private Mockery mockery = new JUnit4Mockery();


    @Test
    public void testGetParameterValue() throws Exception {

        final PaymentGatewayParameter defParam = mockery.mock(PaymentGatewayParameter.class, "defParam");
        final PaymentGatewayParameter shopParam = mockery.mock(PaymentGatewayParameter.class, "shopParam");

        final PaymentGatewayConfigurationVisitor visitor = mockery.mock(PaymentGatewayConfigurationVisitor.class, "visitor");

        mockery.checking(new Expectations() {{
            allowing(defParam).getLabel();
            will(returnValue("param"));
            allowing(defParam).getValue();
            will(returnValue("value"));
            allowing(shopParam).getLabel();
            will(returnValue("#SHOP10_param"));
            allowing(shopParam).getValue();
            will(returnValue("shopvalue"));
            allowing(visitor).getConfiguration("shopCode");
            will(returnValue("SHOP10"));
        }});

        final AbstractPostFinancePaymentGatewayImpl pg = new AbstractPostFinancePaymentGatewayImpl() {
            @Override
            public Payment authorizeCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment authorize(final Payment payment) {
                return null;
            }

            @Override
            public Payment reverseAuthorization(final Payment payment) {
                return null;
            }

            @Override
            public Payment capture(final Payment payment) {
                return null;
            }

            @Override
            public Payment voidCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment refund(final Payment payment) {
                return null;
            }

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public PaymentGatewayFeature getPaymentGatewayFeatures() {
                return null;
            }

            @Override
            public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
                return Arrays.asList(defParam, shopParam);
            }

            @Override
            public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount, final String currencyCode, final String orderReference, final Payment payment) {
                return null;
            }

            @Override
            public Payment createPaymentPrototype(final String operation, final Map map) {
                return null;
            }
        };

        assertEquals("value", pg.getParameterValue("param"));
        pg.accept(visitor);
        assertEquals("shopvalue", pg.getParameterValue("param"));
        assertNull(pg.getParameterValue(null));
        assertNull(pg.getParameterValue("#SHOP10_param")); // Do not allow explicit access

    }


    @Test
    public void testSha1() throws Exception {

        final AbstractPostFinancePaymentGatewayImpl pg = new AbstractPostFinancePaymentGatewayImpl() {
            @Override
            public Payment authorizeCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment authorize(final Payment payment) {
                return null;
            }

            @Override
            public Payment reverseAuthorization(final Payment payment) {
                return null;
            }

            @Override
            public Payment capture(final Payment payment) {
                return null;
            }

            @Override
            public Payment voidCapture(final Payment payment) {
                return null;
            }

            @Override
            public Payment refund(final Payment payment) {
                return null;
            }

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public PaymentGatewayFeature getPaymentGatewayFeatures() {
                return null;
            }

            @Override
            public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
                return null;
            }

            @Override
            public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount, final String currencyCode, final String orderReference, final Payment payment) {
                return null;
            }

            @Override
            public Payment createPaymentPrototype(final String operation, final Map map) {
                return null;
            }
        };


        final Map<String, String> params1 = new LinkedHashMap<String, String>();
        params1.put("P1", "123");
        params1.put("P2", "345");
        final Map<String, String> params2 = new LinkedHashMap<String, String>();
        params2.put("P1", "123");
        params2.put("P2", "345");

        // Test different secrets
        final String sha1_1 = pg.sha1sign(params1, "secret1");
        assertNotNull(sha1_1);
        final String sha1_2 = pg.sha1sign(params1, "secret2");
        assertNotNull(sha1_2);
        assertFalse(sha1_1.equals(sha1_2));

        // Test different params
        final String sha1_3 = pg.sha1sign(params1, "secret1");
        assertNotNull(sha1_3);
        final String sha1_4 = pg.sha1sign(params2, "secret1");
        assertNotNull(sha1_4);
        assertEquals(sha1_3, sha1_4);

        // Example from PostFinance dev manual
        final Map<String, String> params3 = new LinkedHashMap<String, String>();
        params3.put("AMOUNT", "1500");
        params3.put("CURRENCY", "EUR");
        params3.put("LANGUAGE", "en_US");
        params3.put("ORDERID", "1234");
        params3.put("PSPID", "MyPSPID");
        final String sha1_5 = pg.sha1sign(params3, "Mysecretsig1875!?");
        assertEquals("F4CC376CD7A834D997B91598FA747825A238BE0A", sha1_5);

    }


}