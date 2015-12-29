package org.yes.cart.service.payment.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.service.payment.PaymentLocaleTranslator;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/12/2015
 * Time: 14:58
 */
public class PaymentLocaleTranslatorImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testTranslateLocaleInMapping() throws Exception {

        final PaymentGateway pg = context.mock(PaymentGateway.class);

        context.checking(new Expectations() {{
            allowing(pg).getParameterValue("LANGUAGE_MAP"); will(returnValue("de=de_DE,fr=fr_FR"));
        }});

        final PaymentLocaleTranslator translator = new PaymentLocaleTranslatorImpl();

        assertEquals("de_DE", translator.translateLocale(pg, "de"));

        context.assertIsSatisfied();

    }

    @Test
    public void testTranslateLocaleNotInMapping() throws Exception {

        final PaymentGateway pg = context.mock(PaymentGateway.class);

        context.checking(new Expectations() {{
            allowing(pg).getParameterValue("LANGUAGE_MAP"); will(returnValue("de=de_DE,fr=fr_FR"));
        }});

        final PaymentLocaleTranslator translator = new PaymentLocaleTranslatorImpl();

        assertEquals("ru", translator.translateLocale(pg, "ru"));

        context.assertIsSatisfied();

    }


    @Test
    public void testTranslateLocaleNoMapping() throws Exception {

        final PaymentGateway pg = context.mock(PaymentGateway.class);

        context.checking(new Expectations() {{
            allowing(pg).getParameterValue("LANGUAGE_MAP"); will(returnValue(""));
        }});

        final PaymentLocaleTranslator translator = new PaymentLocaleTranslatorImpl();

        assertEquals("de", translator.translateLocale(pg, "de"));

        context.assertIsSatisfied();

    }


    @Test
    public void testTranslateLocaleInMappingWithSpaces() throws Exception {

        final PaymentGateway pg = context.mock(PaymentGateway.class);

        context.checking(new Expectations() {{
            allowing(pg).getParameterValue("LANGUAGE_MAP"); will(returnValue("de=     de_DE   \n    ,fr  =  fr_FR \n"));
        }});

        final PaymentLocaleTranslator translator = new PaymentLocaleTranslatorImpl();

        assertEquals("fr_FR", translator.translateLocale(pg, "fr"));

        context.assertIsSatisfied();

    }

}