package org.yes.cart.payment.impl;

import com.google.checkout.sdk.commands.ApiContext;
import com.google.checkout.sdk.commands.CartPoster;
import com.google.checkout.sdk.commands.Environment;
import com.google.checkout.sdk.domain.*;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.Assert;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.util.ShopCodeContext;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Google checkout payment gateway
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/25/11
 * Time: 5:13 PM
 */
public class GoogleCheckoutPaymentGatewayImpl
        extends AbstractGswmPaymentGatewayImpl
        implements PaymentGatewayExternalForm, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private CarrierSlaService carrierSlaService;



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

    private final ObjectFactory objectFactory = new ObjectFactory();


    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, false,
            true, true,
            null ,
            false, false
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
        ShopCodeContext.getLog(this).info("#authorize");

        return (Payment) SerializationUtils.clone(paymentIn);


        //final CheckoutRedirect checkoutRedirect = apiContext.cartPoster().postCart(checkoutShoppingCart);
        //payment.setTransactionReferenceId(checkoutRedirect.getSerialNumber());
        // payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment payment) {
        ShopCodeContext.getLog(this).info("#reverseAuthorization");

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment payment) {
        ShopCodeContext.getLog(this).info("#capture");

        payment.setTransactionOperation(CAPTURE);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment payment) {
        ShopCodeContext.getLog(this).info("#voidCapture");

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment payment) {
        ShopCodeContext.getLog(this).info("#refund");

        return payment;
    }

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        Assert.notNull(payment, "The payment details require for google checkout payment gateway");

        final CheckoutShoppingCart checkoutShoppingCart = createGoogleCart(payment, orderGuid);

        final String cartXml = checkoutShoppingCart.toString();

        //System.out.println(">>>>> " + cartXml);

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getHiddenFiled("cart", new String(Base64.encode(cartXml.getBytes()))));

        stringBuilder.append(getHiddenFiled("signature", getSignature(cartXml, getParameterValue(GC_MERCHANT_KEY))));

        return stringBuilder.toString();

    }


    /**
     * Actual  notification flow is here
     * http://code.google.com/intl/uk-UA/apis/checkout/developer/Google_Checkout_XML_API_Alternate_Order_Flow_Diagrams.html
     *
     * @param request  http request
     * @param response http responce.
     */
    public void handleNotification(final HttpServletRequest request, final HttpServletResponse response) {
        final ApiContext apiContext = getApiContext();
        dumpRequest(request);
        apiContext.handleNotification(
                new GoogleNotificationDispatcherImpl(applicationContext, request, response)
        );
    }

    private ApiContext apiContext;


    /**
     * Getr google api context;
     *
     * @return {@link ApiContext}
     */
    ApiContext getApiContext() {
        if (apiContext == null) {
            apiContext = new ApiContext(
                    "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                    getParameterValue(GC_MERCHANT_ID),
                    getParameterValue(GC_MERCHANT_KEY),
                    "USD" //wtf
            );
        }
        return apiContext;
    }



    /**
     * Create {@link CheckoutShoppingCart} from given payment and payment details.
     *
     * @param payment   given {@link Payment}
     * @param orderGuid order guid.
     * @return created {@link CheckoutShoppingCart}
     */
    CheckoutShoppingCart createGoogleCart(final Payment payment, final String orderGuid) {

        final ApiContext apiContext = new ApiContext(
                "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                getParameterValue(GC_MERCHANT_ID),
                getParameterValue(GC_MERCHANT_KEY),
                payment.getOrderCurrency()
        );


        final CartPoster.CheckoutShoppingCartBuilder cartBuilder = apiContext.cartPoster().makeCart();

        final MerchantCheckoutFlowSupport flowSupport = objectFactory.createMerchantCheckoutFlowSupport();

        /* ATM i am not going to support partner tracking
        flowSupport.setParameterizedUrls(objectFactory.createMerchantCheckoutFlowSupportParameterizedUrls());
        flowSupport.getParameterizedUrls().getParameterizedUrl().add()
        final ParameterizedUrl.Parameters params = objectFactory.createParameterizedUrlParameters();
        params.getUrlParameter();
        final MerchantCheckoutFlowSupport.ParameterizedUrls   parameterizedUrls    = ;
        final ParameterizedUrl purl = objectFactory.createParameterizedUrl();
        purl.setParameters(params);
        parameterizedUrls.getParameterizedUrl().add(purl); */


        final CheckoutShoppingCart.CheckoutFlowSupport cartFlowSupport = objectFactory.createCheckoutShoppingCartCheckoutFlowSupport();

        cartFlowSupport.setMerchantCheckoutFlowSupport(flowSupport);

        final String currency = payment.getOrderCurrency();
        final String language = payment.getOrderLocale();

        for (PaymentLine paymentLine : payment.getOrderItems()) {
            //Products only
            if (!paymentLine.isShipment()) {
                cartBuilder.addItem(
                        createShipmentItem(
                                currency,
                                apiContext,
                                paymentLine)
                );
            }
        }

        //set shipping methods to select on google
        flowSupport.setShippingMethods(
                createShipmentMethod(
                        language,
                        currency,
                        objectFactory)
        );


        final AnyMultiple anyMultiple = objectFactory.createAnyMultiple(); //just put our order id

        anyMultiple.getContent().add(orderGuid);

        final CheckoutShoppingCart checkoutShoppingCart = cartBuilder.build();

        checkoutShoppingCart.setCheckoutFlowSupport(cartFlowSupport);

        /**
         * The <merchant-private-data> tag contains any well-formed XML sequence that should accompany an order.
         * Google Checkout will return this XML in the <merchant-calculation-callback> and the <new-order-notification> for the order.
         */
        checkoutShoppingCart.getShoppingCart().setMerchantPrivateData(anyMultiple);

        return checkoutShoppingCart;
    }

    /**
     * Create item in cart.
     *
     * @param currency    currency
     * @param apiContext  api context
     * @param paymentLine payment line, that represent item in cart
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
     *
     *
     * @param language      order language
     * @param currency      order currency
     * @param objectFactory object factory
     * @return {@link MerchantCheckoutFlowSupport.ShippingMethods}
     */
    MerchantCheckoutFlowSupport.ShippingMethods createShipmentMethod(final String language,
                                                                     final String currency,
                                                                     final ObjectFactory objectFactory) {

        final MerchantCheckoutFlowSupport.ShippingMethods shippingMethods =
                objectFactory.createMerchantCheckoutFlowSupportShippingMethods();

        for (CarrierSla sla : getCarrierSlaService().findByCurrency(currency)) {

            final FlatRateShipping.Price price = objectFactory.createFlatRateShippingPrice();
            price.setCurrency(currency);
            price.setValue(sla.getPrice());

            final FlatRateShipping flatRateShipping = objectFactory.createFlatRateShipping();
            flatRateShipping.setName(new FailoverStringI18NModel(sla.getDisplayName(), sla.getName()).getValue(language));
            flatRateShipping.setPrice(price);

            shippingMethods.getAllShippingMethods().add(flatRateShipping);

        }


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
            ShopCodeContext.getLog(this).error("Cant create signature", e);
        } catch (InvalidKeyException e) {
            ShopCodeContext.getLog(this).error("Cant create signature", e);
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
        return true;  //TODO: YC-146
    }


    public void dumpRequest(final ServletRequest request) {

        final Logger log = ShopCodeContext.getLog(this);

        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            log.info(MessageFormat.format("HttpUtil#dumpRequest local at gc param key = [{0}] value = [{1}]",
                    key,
                    request.getParameter((String) key)));
        }

        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            log.info(MessageFormat.format("HttpUtil#dumpRequest local at gc attr  key = [{0}] value = [{1}]",
                    key,
                    request.getAttribute((String) key)));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * Set the {@link CarrierSlaService} to work with sla.
     *
     * @return {@link CarrierSlaService}
     */
    private CarrierSlaService getCarrierSlaService() {
        if (carrierSlaService == null) {
            carrierSlaService = applicationContext.getBean("carrierSlaService", CarrierSlaService.class);
        }
        return carrierSlaService;
    }


}
