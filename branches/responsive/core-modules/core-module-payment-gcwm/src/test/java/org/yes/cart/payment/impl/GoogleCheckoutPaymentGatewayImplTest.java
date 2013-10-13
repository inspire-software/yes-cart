package org.yes.cart.payment.impl;

import com.google.checkout.sdk.domain.CheckoutShoppingCart;
import com.google.checkout.sdk.domain.FlatRateShipping;
import com.google.checkout.sdk.domain.MerchantCheckoutFlowSupport;
import com.google.checkout.sdk.domain.ObjectFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**

 */
public class GoogleCheckoutPaymentGatewayImplTest extends TestCase {

    private String serialNumber = null;

    @Test
    public void testGetSignature() throws Exception {

        GoogleCheckoutPaymentGatewayImpl googleCheckoutPaymentGateway = new GoogleCheckoutPaymentGatewayImpl();
        assertEquals(
                "Siggnatures must be equals",
                "5v8+qcRAK9deMavsJjVR63PFo/4=",
                googleCheckoutPaymentGateway.getSignature("Happy New Year, meat bags", "Bender"));


    }


    /*@Test
    public void testHandleNotification() {

        final GoogleCheckoutPaymentGatewayImpl googleCheckoutPaymentGateway = new GoogleCheckoutPaymentGatewayImpl() {

             public String getParameterValue(final String valueLabel) {
                 if ("GC_MERCHANT_ID".equals(valueLabel)) {
                     return "951076354081708";
                 } else if ("GC_MERCHANT_KEY".equals(valueLabel)) {
                     return "1DbImipHlIEVsi0liSeA_A";
                 } else if ("GC_ENVIRONMENT".equals(valueLabel)) {
                     return "SANDBOX";
                 }
                 return null;
             }

        };


        HttpServletRequest httpServletRequest = new MockHttpServletRequest() {
            @Override
            public String getHeader(String name) {
                if (name.equals("Authorization")) {
                    return googleCheckoutPaymentGateway.getApiContext().getHttpAuth();
                } else if (name.equals("Content-Type")) {
                    return "application/xml";
                }
                fail();
                return null;
            }

            @Override
            public ServletInputStream getInputStream()  {
                return new ServletInputStream() {
                    @Override
                    public int read() throws UnsupportedEncodingException {
                        return  new ByteArrayInputStream(
                            getNewOrderNotification().getBytes("utf-8")).read();
                    }
                };
            }

        };


        HttpServletResponse httpServletResponse = new MockHttpServletResponse();

        googleCheckoutPaymentGateway.handleNotification(httpServletRequest, httpServletResponse);

        assertTrue(true);




    }     */

    @Test
    public void testGoogleCheckoutCart() {

        GoogleCheckoutPaymentGatewayImpl googleCheckoutPaymentGateway = new GoogleCheckoutPaymentGatewayImpl() {

            /** {@inheritDoc} */
            public String getParameterValue(final String valueLabel) {
                if ("GC_ENVIRONMENT".equals(valueLabel)) {
                    return "SANDBOX";
                } else if ("GC_MERCHANT_ID".equals(valueLabel)) {
                    return "951076354081708";
                } else if ("GC_MERCHANT_KEY".equals(valueLabel)) {
                    return "1DbImipHlIEVsi0liSeA_A";
                } else if ("GC_POST_URL".equals(valueLabel)) {
                    return "https://sandbox.google.com/checkout/api/checkout/v2/checkout/Merchant/951076354081708";
                }
                throw new IllegalArgumentException("The " + valueLabel + " argument is illegal");
            }


            /** {@inheritDoc} */
            @Override
            MerchantCheckoutFlowSupport.ShippingMethods createShipmentMethod(final String language, String currency, ObjectFactory objectFactory) {
                final MerchantCheckoutFlowSupport.ShippingMethods shippingMethods =
                        objectFactory.createMerchantCheckoutFlowSupportShippingMethods();


                final FlatRateShipping.Price price = objectFactory.createFlatRateShippingPrice();
                price.setCurrency(currency);
                price.setValue(new BigDecimal("3.0"));

                final FlatRateShipping flatRateShipping = objectFactory.createFlatRateShipping();
                flatRateShipping.setName("Cheap delivery");
                flatRateShipping.setPrice(price);

                shippingMethods.getAllShippingMethods().add(flatRateShipping);


                return shippingMethods;
            }
        };

        CheckoutShoppingCart checkoutShoppingCart = googleCheckoutPaymentGateway.createGoogleCart(createTesPayment(), "1234-1234-5678-5678");

        assertNotNull(checkoutShoppingCart);

        assertEquals("Two items must be in the cart",
                2, checkoutShoppingCart.getShoppingCart().getItems().getItem().size());

        assertEquals("One shipment method",
                1,
                checkoutShoppingCart.getCheckoutFlowSupport().getMerchantCheckoutFlowSupport()
                        .getShippingMethods().getAllShippingMethods().size());


        String xml = checkoutShoppingCart.toString();
        System.out.println(xml);

        String formPart = googleCheckoutPaymentGateway.getHtmlForm(
                null, null, null, null, "134-1234-1234-124", createTesPayment());

        System.out.println(formPart);

    }


    private Payment createTesPayment() {

        final List<PaymentLine> orderItems = new ArrayList<PaymentLine>() {{
            add(new PaymentLineImpl("code1", "name1", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, false));
            add(new PaymentLineImpl("code2", "name2", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, false));
            add(new PaymentLineImpl("delivery1", "Cheap delivery", BigDecimal.ONE, new BigDecimal("3.0"), BigDecimal.ZERO, true));
        }};


        final Payment payment = new PaymentImpl();

        payment.setOrderItems(orderItems);
        payment.setOrderCurrency("USD");
        payment.setOrderLocale("en");

        return payment;

    }


    private String getNewSerialNumber() {
        serialNumber = UUID.randomUUID().toString();
        return serialNumber;
    }

    private String getSerialNumber() {
        if (serialNumber != null) {
            return serialNumber;
        }
        return getNewSerialNumber();

    }


    private String getNewOrderNotification() {
        return "<a>asdfsdf</a>";
        /*return
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><new-order-notification serial-number=\""+getSerialNumber()+"\"xmlns=\"http://checkout.google.com/schema/2\">"+
                        "<buyer-billing-address>"+
                        "<email>yescarttestbuyer@gmail.com</email>"+
                        "<phone>+3809971231231</phone>"+
                        "<contact-name>TEST BUYER</contact-name>"+
                        "<company-name></company-name>"+
                        "<fax></fax>"+
                        "<address1>billing</address1>"+
                        "<address2>address</address2>"+
                        "<structured-name>"+
                        "<first-name>TEST</first-name>"+
                        "<last-name>BUYER</last-name>"+
                        "</structured-name>"+
                        "<country-code>UA</country-code>"+
                        "<region></region>"+
                        "<city>Kiev</city>"+
                        "<postal-code>11111</postal-code>"+
                        "</buyer-billing-address>"+
                        "<timestamp>2012-01-10T19:35:47.334Z</timestamp>"+
                        "<google-order-number>412601321126243</google-order-number>"+
                        "<order-summary>"+
                        "<google-order-number>412601321126243</google-order-number>"+
                        "<total-refund-amount currency=\"USD\">0.0</total-refund-amount>"+
                        "<total-charge-amount currency=\"USD\">0.0</total-charge-amount>"+
                        "<total-chargeback-amount currency=\"USD\">0.0</total-chargeback-amount>"+
                        "<purchase-date>2012-01-10T19:35:47.000Z</purchase-date>"+
                        "<archived>false</archived>"+
                        "<shopping-cart>"+
                        "<merchant-private-data>870a6eef-378b-4455-a9ae-7e851320db09</merchant-private-data>"+
                        "<items>"+
                        "<item>"+
                        "<item-name>HP TouchPad (16GB)</item-name>"+
                        "<item-description>HP TouchPad (16GB)</item-description>"+
                        "<unit-price currency=\"USD\">499.99</unit-price>"+
                        "<quantity>1</quantity>"+
                        "<merchant-item-id>hp-touchpad-16gb</merchant-item-id>"+
                        "</item>"+
                        "</items>"+
                        "</shopping-cart>"+
                        "<order-adjustment>"+
                        "<merchant-codes/>"+
                        "<total-tax currency=\"USD\">0.0</total-tax>"+
                        "<shipping>"+
                        "<flat-rate-shipping-adjustment>"+
                        "<shipping-name>World wide</shipping-name>"+
                        "<shipping-cost currency=\"USD\">30.0</shipping-cost>"+
                        "</flat-rate-shipping-adjustment>"+
                        "</shipping>"+
                        "<adjustment-total currency=\"USD\">30.0</adjustment-total>"+
                        "</order-adjustment>"+
                        "<buyer-shipping-address>"+
                        "<email>yescarttestbuyer@gmail.com</email>"+
                        "<phone>(201) 815-9112</phone>"+
                        "<contact-name>Test address 2</contact-name>"+
                        "<company-name></company-name>"+
                        "<fax></fax>"+
                        "<address1>12 sell street</address1>"+
                        "<address2></address2>"+
                        "<structured-name>"+
                        "<first-name>Test address</first-name>"+
                        "<last-name>2</last-name>"+
                        "</structured-name>"+
                        "<country-code>US</country-code>"+
                        "<region>GA</region>"+
                        "<city>Atlanta</city>"+
                        "<postal-code>39899</postal-code>"+
                        "</buyer-shipping-address>"+
                        "<buyer-marketing-preferences>"+
                        "<email-allowed>true</email-allowed>"+
                        "</buyer-marketing-preferences>"+
                        "<promotions/>"+
                        "<order-total currency=\"USD\">529.99</order-total>"+
                        "<fulfillment-order-state>NEW</fulfillment-order-state>"+
                        "<financial-order-state>REVIEWING</financial-order-state>"+
                        "<buyer-id>937836576210944</buyer-id>"+
                        "</order-summary>"+
                        "<shopping-cart>"+
                        "<merchant-private-data>870a6eef-378b-4455-a9ae-7e851320db09</merchant-private-data>"+
                        "<items>"+
                        "<item>"+
                        "<item-name>HP TouchPad (16GB)</item-name>"+
                        "<item-description>HP TouchPad (16GB)</item-description>"+
                        "<unit-price currency=\"USD\">499.99</unit-price>"+
                        "<quantity>1</quantity>"+
                        "<merchant-item-id>hp-touchpad-16gb</merchant-item-id>"+
                        "</item>"+
                        "</items>"+
                        "</shopping-cart>"+
                        "<order-adjustment>"+
                        "<merchant-codes/>"+
                        "<total-tax currency=\"USD\">0.0</total-tax>"+
                        "<shipping>"+
                        "<flat-rate-shipping-adjustment>"+
                        "<shipping-name>World wide</shipping-name>"+
                        "<shipping-cost currency=\"USD\">30.0</shipping-cost>"+
                        "</flat-rate-shipping-adjustment>"+
                        "</shipping>"+
                        "<adjustment-total currency=\"USD\">30.0</adjustment-total>"+
                        "</order-adjustment>"+
                        "<buyer-shipping-address>"+
                        "<email>yescarttestbuyer@gmail.com</email>"+
                        "<phone>(201) 815-9112</phone>"+
                        "<contact-name>Test address 2</contact-name>"+
                        "<company-name></company-name>"+
                        "<fax></fax>"+
                        "<address1>12 sell street</address1>"+
                        "<address2></address2>"+
                        "<structured-name>"+
                        "<first-name>Test address</first-name>"+
                        "<last-name>2</last-name>"+
                        "</structured-name>"+
                        "<country-code>US</country-code>"+
                        "<region>GA</region>"+
                        "<city>Atlanta</city>"+
                        "<postal-code>39899</postal-code>"+
                        "</buyer-shipping-address>"+
                        "<buyer-marketing-preferences>"+
                        "<email-allowed>true</email-allowed>"+
                        "</buyer-marketing-preferences>"+
                        "<promotions/>"+
                        "<order-total currency=\"USD\">529.99</order-total>"+
                        "<fulfillment-order-state>NEW</fulfillment-order-state>"+
                        "<financial-order-state>REVIEWING</financial-order-state>"+
                        "<buyer-id>937836576210944</buyer-id>"+
                        "</new-order-notification>";  */
    }


}
