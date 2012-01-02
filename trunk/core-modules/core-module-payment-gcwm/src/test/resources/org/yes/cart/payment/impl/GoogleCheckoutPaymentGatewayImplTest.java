package org.yes.cart.payment.impl;

import com.google.checkout.sdk.domain.CheckoutShoppingCart;
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

 */
public class GoogleCheckoutPaymentGatewayImplTest extends TestCase {

    @Test
    public void testGetSignature() throws Exception {

        GoogleCheckoutPaymentGatewayImpl googleCheckoutPaymentGateway = new GoogleCheckoutPaymentGatewayImpl();
        assertEquals(
                "Siggnatures must be equals",
                "5v8+qcRAK9deMavsJjVR63PFo/4=",
                googleCheckoutPaymentGateway.getSignature("Happy New Year, meat bags", "Bender"));


    }

    @Test
    public void testGoogleCheckoutCart() {

        GoogleCheckoutPaymentGatewayImpl googleCheckoutPaymentGateway = new GoogleCheckoutPaymentGatewayImpl() {
            /** {@inheritDoc} */
             public String getParameterValue(final String valueLabel) {
                 if ("GC_ENVIRONMENT".equals(valueLabel))  {
                     return "SANDBOX";
                 } else if ("GC_MERCHANT_ID".equals(valueLabel))  {
                     return "951076354081708";
                 } else if ("GC_MERCHANT_KEY".equals(valueLabel))  {
                     return "1DbImipHlIEVsi0liSeA_A";
                 } else if ("GC_POST_URL".equals(valueLabel))  {
                     return "https://sandbox.google.com/checkout/api/checkout/v2/checkout/Merchant/951076354081708";
                 }
                 throw new IllegalArgumentException("The " + valueLabel + " argument is illegal");
             }
        };

        CheckoutShoppingCart checkoutShoppingCart = googleCheckoutPaymentGateway.createGoogleCart(createTesPayment());

        assertNotNull(checkoutShoppingCart);

        assertEquals("Two items must be in the cart",
                2, checkoutShoppingCart.getShoppingCart().getItems().getItem().size());

        assertEquals("One shipment method",
                1,
                checkoutShoppingCart.getCheckoutFlowSupport().getMerchantCheckoutFlowSupport()
                        .getShippingMethods().getFlatRateShippingOrMerchantCalculatedShippingOrPickup().size());


        String xml = checkoutShoppingCart.toString();
        System.out.println(xml);

        String formPart = googleCheckoutPaymentGateway.getHtmlForm(
                null, null, null, null, null, createTesPayment());

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

        return payment;

    }


}
