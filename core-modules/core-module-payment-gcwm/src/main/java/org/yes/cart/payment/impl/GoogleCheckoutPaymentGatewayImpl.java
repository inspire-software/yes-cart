package org.yes.cart.payment.impl;

import com.google.checkout.sdk.commands.ApiContext;
import com.google.checkout.sdk.commands.CartPoster;
import com.google.checkout.sdk.commands.Environment;
import com.google.checkout.sdk.domain.*;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.util.Assert;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Google checkout payment gateway
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/25/11
 * Time: 5:13 PM
 */
public class GoogleCheckoutPaymentGatewayImpl extends AbstractGswmPaymentGatewayImpl implements PaymentGateway {

    public static final String ORDER_GUID = "orderGuid";  //this id our order guid

    //test or lice env
    private static final String GC_ENVIRONMENT = "GC_ENVIRONMENT";

    // merchant id
    private static final String GC_MERCHANT_ID = "GC_MERCHANT_ID";

    // key
    private static final String GC_MERCHANT_KEY = "GC_MERCHANT_KEY";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            true, false, true, true,
            false, false, false, false,
            true,  true,
            null
    );

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Shipment not included. Will be added at capture operation.
     */
    public Payment authorize(final Payment paymentIn) {

        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);

        final ApiContext apiContext = new ApiContext(
                "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                getParameterValue(GC_MERCHANT_ID),
                getParameterValue(GC_MERCHANT_KEY),
                payment.getOrderCurrency()
        );

        final CartPoster.CheckoutShoppingCartBuilder cartBuilder = apiContext.cartPoster().makeCart();

        for (PaymentLine paymentLine : payment.getOrderItems()) {
            if (!paymentLine.isShipment()) {
                Item item = new Item();
                item.setItemName(paymentLine.getSkuName());
                item.setItemDescription(paymentLine.getSkuName());
                item.setMerchantItemId(paymentLine.getSkuCode());
                item.setQuantity(paymentLine.getQuantity().intValue());
                item.setUnitPrice(apiContext.makeMoney(paymentLine.getUnitPrice()));
                cartBuilder.addItem(item);
            }
        }


        //cartBuilder.build().s

        //final CheckoutRedirect checkoutRedirect = cartBuilder.buildAndPost();

        final CheckoutShoppingCart checkoutShoppingCart = cartBuilder.build();

        final MerchantCheckoutFlowSupport flowSupport = new MerchantCheckoutFlowSupport();

        final CheckoutShoppingCart.CheckoutFlowSupport  cartFlowSupport = new  CheckoutShoppingCart.CheckoutFlowSupport();

        cartFlowSupport.setMerchantCheckoutFlowSupport(flowSupport);



        checkoutShoppingCart.setCheckoutFlowSupport(cartFlowSupport);

        final CheckoutRedirect checkoutRedirect = apiContext.cartPoster().postCart(checkoutShoppingCart);


        payment.setTransactionReferenceId(checkoutRedirect.getSerialNumber());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
        //todo redirect url ?????

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment payment) {
        payment.setTransactionOperation(CAPTURE);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     *
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {
        Assert.notNull(payment, "The payment details require for google checkout payment gateway");
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getHiddenFiled(ORDER_GUID, orderGuid));  // this will be bypassed via payment gateway to restore it latter
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map parametersMap) {
        final Payment payment = new PaymentImpl();
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "googleCheckoutPaymentGateway";
    }


}
