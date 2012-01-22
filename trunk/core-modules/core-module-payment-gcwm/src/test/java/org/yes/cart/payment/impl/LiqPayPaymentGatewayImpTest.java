package org.yes.cart.payment.impl;

import junit.framework.TestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 6:16 PM
 */
public class LiqPayPaymentGatewayImpTest extends TestCase {

    public void testGetHtmlForm() throws Exception {

        final LiqPayPaymentGatewayImp gatewayImp = new LiqPayPaymentGatewayImp() {
            @Override
            String getXmlTemplate() {
                return "xml";
            }

            @Override
            public String getParameterValue(String valueLabel) {
                if("LP_MERCHANT_KEY".endsWith(valueLabel)) {
                    return "signature";
                }
                return "";
            }
        };

        String htmlFormPart = gatewayImp.getHtmlForm(
                "holder  name",
                "en",
                BigDecimal.TEN.setScale(2),
                "USD",
                "234-1324-1324-1324sdf-sdf",
                createTesPayment()

        );

        assertTrue("Xml body must be present" , htmlFormPart.contains("eG1s"));
        assertTrue("Signature must be present" , htmlFormPart.contains("Lg7KbFKVh0aLNq7auqzqRaERFks="));


    }


    private Payment createTesPayment() {

        final List<PaymentLine> orderItems = new ArrayList<PaymentLine>() {{
            add(new PaymentLineImpl("code2", "name2", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, false));
        }};


        final Payment payment = new PaymentImpl();

        payment.setOrderItems(orderItems);
        payment.setOrderCurrency("USD");

        return payment;

    }


}
