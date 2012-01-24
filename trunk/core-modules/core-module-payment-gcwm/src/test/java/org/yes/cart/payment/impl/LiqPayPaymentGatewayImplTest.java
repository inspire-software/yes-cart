package org.yes.cart.payment.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.TestCase;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 6:16 PM
 */
public class LiqPayPaymentGatewayImplTest extends TestCase {

    @Test
    public void testGetHtmlForm() throws Exception {

        final LiqPayPaymentGatewayImpl gatewayImpl = new LiqPayPaymentGatewayImpl() {
            @Override
            String getXmlTemplate() {
                return "xml";
            }

            @Override
            public String getParameterValue(String valueLabel) {
                if ("LP_MERCHANT_KEY".endsWith(valueLabel)) {
                    return "signature";
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
                createTesPayment()

        );

        assertTrue("Xml body must be present", htmlFormPart.contains("eG1s"));
        assertTrue("Signature must be present", htmlFormPart.contains("Lg7KbFKVh0aLNq7auqzqRaERFks="));


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


    @Test
    public void testUmramshalLiqResponce() throws Exception {
        final String xml = "<response>\n" +
                "      <version>1.2</version>\n" +
                "      <merchant_id>2134</merchant_id>\n" +
                "      <order_id>ORDER_123456</order_id>\n" +
                "      <amount>1.01</amount>\n" +
                "      <currency>UAH</currency>\n" +
                "      <description>Comment</description>\n" +
                "      <status>success</status>\n" +
                "      <code>42</code>\n" +
                "      <transaction_id>31</transaction_id>\n" +
                "      <pay_way>card</pay_way>\n" +
                "      <sender_phone>+3801234567890</sender_phone>\n" +
                "      <goods_id>1234</goods_id>\n" +
                "      <pays_count>5</pays_count>\n" +
                "</response>";
        final LiqPayResponce liqPayResponce = (LiqPayResponce) getXStream().fromXML(xml);

        assertEquals("1.2", liqPayResponce.getVersion());
        assertEquals("2134", liqPayResponce.getMerchant_id());
        assertEquals("ORDER_123456", liqPayResponce.getOrder_id());
        assertEquals("1.01", liqPayResponce.getAmount());
        assertEquals("UAH", liqPayResponce.getCurrency());
        assertEquals("Comment", liqPayResponce.getDescription());
        assertEquals("success", liqPayResponce.getStatus());
        assertEquals("42", liqPayResponce.getCode());
        assertEquals("31", liqPayResponce.getTransaction_id());
        assertEquals("card", liqPayResponce.getPay_way());
        assertEquals("+3801234567890", liqPayResponce.getSender_phone());
        assertEquals("1234", liqPayResponce.getGoods_id());
        assertEquals("5", liqPayResponce.getPays_count());


    }


    private XStream getXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("response", LiqPayResponce.class);
        return xStream;
    }


}
