package org.yes.cart.payment.impl;

import com.google.checkout.sdk.commands.ApiContext;
import com.google.checkout.sdk.commands.CartPoster;
import com.google.checkout.sdk.commands.Environment;
import com.google.checkout.sdk.domain.*;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.security.core.codec.Base64;
import org.springframework.util.Assert;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Google checkout payment gateway
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/25/11
 * Time: 5:13 PM
 */
public class GoogleCheckoutPaymentGatewayImpl extends AbstractGswmPaymentGatewayImpl implements PaymentGatewayExternalForm {

    public static final String ORDER_GUID = "orderGuid";  //this id our order guid

    //test or lice env
    static final String GC_ENVIRONMENT = "GC_ENVIRONMENT";

    // merchant id
    static final String GC_MERCHANT_ID = "GC_MERCHANT_ID";

    // key
    static final String GC_MERCHANT_KEY = "GC_MERCHANT_KEY";

    // Where we have to post signed xml
    static final String GC_POST_URL = "GC_POST_URL";

    // html code for submit btn
    static final String GC_SUBMIT_BTN = "GC_SUBMIT_BTN";

    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, false,
            true, true,
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

        return (Payment) SerializationUtils.clone(paymentIn);


        //final CheckoutRedirect checkoutRedirect = apiContext.cartPoster().postCart(checkoutShoppingCart);
        //payment.setTransactionReferenceId(checkoutRedirect.getSerialNumber());
        // payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

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
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        Assert.notNull(payment, "The payment details require for google checkout payment gateway");

        final CheckoutShoppingCart checkoutShoppingCart = createGoogleCart(payment);

        final String  cartXml = checkoutShoppingCart.toString();

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getHiddenFiled("cart", new String(Base64.encode(cartXml.getBytes()))));

        stringBuilder.append(getHiddenFiled("signature", getSignature(cartXml, getParameterValue(GC_MERCHANT_KEY))));

        return stringBuilder.toString();

    }

    /**
     * Create {@link CheckoutShoppingCart} from given payment and payment details.
     * @param payment given {@link Payment}
     * @return created {@link CheckoutShoppingCart}
     */
    CheckoutShoppingCart createGoogleCart(final Payment payment) {

        final ApiContext apiContext = new ApiContext(
                "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                getParameterValue(GC_MERCHANT_ID),
                getParameterValue(GC_MERCHANT_KEY),
                payment.getOrderCurrency()
        );

        final ObjectFactory objectFactory = new ObjectFactory();

        final CartPoster.CheckoutShoppingCartBuilder cartBuilder = apiContext.cartPoster().makeCart();

        final MerchantCheckoutFlowSupport flowSupport = objectFactory.createMerchantCheckoutFlowSupport();

        final CheckoutShoppingCart.CheckoutFlowSupport cartFlowSupport = objectFactory.createCheckoutShoppingCartCheckoutFlowSupport();

        cartFlowSupport.setMerchantCheckoutFlowSupport(flowSupport);

        final String currency = payment.getOrderCurrency();

        for (PaymentLine paymentLine : payment.getOrderItems()) {
            if (paymentLine.isShipment()) {
                flowSupport.setShippingMethods(
                        createShipmentMethod(
                                currency,
                                objectFactory,
                                paymentLine)
                );
            } else {
                cartBuilder.addItem(
                        createShipmentItem(
                                currency,
                                apiContext,
                                paymentLine)
                );
            }
        }

        final CheckoutShoppingCart checkoutShoppingCart = cartBuilder.build();

        checkoutShoppingCart.setCheckoutFlowSupport(cartFlowSupport);

        return checkoutShoppingCart;
    }

    /**
     * Create item in cart.
     * @param currency currency
     * @param apiContext api context
     * @param paymentLine  payment line, that represent item in cart
     * @return item for google cart
     */
    private Item createShipmentItem(final String currency, final ApiContext apiContext, final PaymentLine paymentLine) {

        final Money money = apiContext.makeMoney(paymentLine.getUnitPrice());
        money.setCurrency(currency);

        final Item item = new Item();
        item.setItemName(paymentLine.getSkuName());
        item.setItemDescription(paymentLine.getSkuName());
        item.setMerchantItemId(paymentLine.getSkuCode());
        item.setQuantity(paymentLine.getQuantity().intValue());
        item.setUnitPrice(money);
        return item;

    }

    /**
     * Create shipment method. CPOINT shipment method already selected by customer, so
     * is sme cases need customise how it should be represent in google cart.
     * @param currency order curreny
     * @param objectFactory object factory
     * @param paymentLine  payment line, that represent shipment in cart
     * @return  {@link MerchantCheckoutFlowSupport.ShippingMethods}
     */
    private MerchantCheckoutFlowSupport.ShippingMethods createShipmentMethod(final String currency, final ObjectFactory objectFactory, final PaymentLine paymentLine) {

        final MerchantCheckoutFlowSupport.ShippingMethods shippingMethods =
                objectFactory.createMerchantCheckoutFlowSupportShippingMethods();

        final FlatRateShipping.Price price = objectFactory.createFlatRateShippingPrice();
        price.setCurrency(currency);
        price.setValue(paymentLine.getUnitPrice());

        final FlatRateShipping flatRateShipping = objectFactory.createFlatRateShipping();
        flatRateShipping.setName(paymentLine.getSkuName());
        flatRateShipping.setPrice(price);

        shippingMethods.getFlatRateShippingOrMerchantCalculatedShippingOrPickup().add(flatRateShipping);
        return shippingMethods;
    }


    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map parametersMap) {
        return new PaymentImpl();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "googleCheckoutPaymentGateway";
    }


    /**
     * Create signature for given xml request to google checkout.
     *
     * @param xmlToSign   given xml.
     * @param merchantKey the merchant key
     * @return xml signature.
     */
    String getSignature(final String xmlToSign, final String merchantKey) {

        Assert.notNull(xmlToSign, "XML to sign must be provided");
        Assert.notNull(merchantKey, "the merchant key must be provided");

        final SecretKey secretKey = new SecretKeySpec(merchantKey.getBytes(), "HmacSHA1");

        try {

            final Mac mac = Mac.getInstance("HmacSHA1");

            mac.init(secretKey);

            final byte[] text = xmlToSign.getBytes();

            return new String(Base64.encode(mac.doFinal(text))).trim();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (InvalidKeyException e) {

            e.printStackTrace();
        }

        return null;

    }


    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        return getParameterValue(GC_POST_URL);
    }

    /**
     * {@inheritDoc}
     */
    public String getSubmitButton() {
        return getParameterValue(GC_SUBMIT_BTN);
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(Map privateCallBackParameters) {
        return getSingleValue(privateCallBackParameters.get(ORDER_GUID));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccess(Map<String, String> nvpCallResult) {
        return true;  //todo
    }
}
