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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpUtils;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.utils.HttpQueryUtility;
import org.yes.cart.payment.utils.PaySeraRequest;
import org.yes.cart.service.payment.PaymentLocaleTranslator;
import org.yes.cart.service.payment.impl.PaymentLocaleTranslatorImpl;
import org.yes.cart.utils.HttpParamsUtils;

/**
 * User: inspiresoftware
 * Date: 07/10/2020
 * Time: 11:22
 */
public class PaySeraCheckoutPaymentGatewayImpl  extends AbstractPaySeraPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(PaySeraCheckoutPaymentGatewayImpl.class);

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, true,
            null,
            false, false
    );

    // form post acton url
    static final String PSC_POSTURL = "PSC_POSTURL";

    // Configuration parameters
    static final String PSC_ENVIRONMENT = "PSC_ENVIRONMENT";
    static final String PSC_PROJECTID = "PSC_PROJECTID";
    static final String PSC_SIGN_PASSWORD = "PSC_SIGN_PASSWORD";
    static final String PSC_API_VERSION = "PSC_API_VERSION";
    static final String PSC_BUYBUTTON = "PSC_BUYBUTTON";
    static final String PSC_CALLBACKURL = "PSC_CALLBACKURL";
    static final String PSC_ACCEPTURL = "PSC_ACCEPTURL";
    static final String PSC_CANCELURL = "PSC_CANCELURL";

    private final PaymentLocaleTranslator paymentLocaleTranslator = new PaymentLocaleTranslatorImpl();


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        return getParameterValue(PSC_POSTURL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {

        String submit = getParameterValue(PSC_BUYBUTTON + "_" + locale);
        if (submit == null) {
            submit = getParameterValue(PSC_BUYBUTTON);
        }
        if (StringUtils.isNotBlank(submit)) {
            return submit;
        }
        return null;
    }

    public static String getSingleValue(final Object param) {
        if (param instanceof String) {
            return (String) param;
        } else if (param instanceof String[]) {
            if (((String[])param).length > 0) {
                return ((String [])param)[0];
            }
        }
        return null;

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters, final boolean forceProcessing) {
        
        final Map<String, String> clean = new HashMap<>();
        if (privateCallBackParameters != null) {
            for (final Map.Entry<Object, Object> entry : ((Map<Object, Object>) privateCallBackParameters).entrySet()) {
                final String value = getSingleValue(entry.getValue());
                clean.put(entry.getKey().toString(), value);
            }
        }
            
		String dataAsBase64 = clean.get("data");
		String ss2AsBase64 = clean.get("ss2");
		String ss1AsBase64 = clean.get("ss1");
		
		String dataQuery = HttpQueryUtility.decodeBase64UrlSafeToString(dataAsBase64);
		Hashtable<String, String[]> parsedParams = HttpUtils.parseQueryString(dataQuery);
		
		String sign = HttpQueryUtility.calculateMD5(dataAsBase64 + getParameterValue(PSC_SIGN_PASSWORD));
		
        boolean valid = sign.contentEquals(ss1AsBase64);
        
// --- code for ss2 validation - not working --- TODO investigate how to validate RSA public key in java
//		String publicKeyPEMFileContents = null;
//		
//		try {
//			publicKeyPEMFileContents = getText("https://bank.paysera.com/download/public.key");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//        PemReader reader = null;
        
//		try {
//			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//			File pemFile = new File("https://bank.paysera.com/download/public.key");
//			FileUtils.copyURLToFile(new URL("https://bank.paysera.com/download/public.key"), pemFile);
			
//			reader = new PemReader(new FileReader(pemFile));
			
//			X509EncodedKeySpec publicKetSpec = new X509EncodedKeySpec(HttpQueryUtility.getPublicKeyRawDataFromPEMFile(publicKeyPEMFileContents));
//	        PublicKey publicKey = keyFactory.generatePublic(publicKetSpec);
//	        valid = HttpQueryUtility.verify(dataAsBase64.getBytes(), ss2, publicKey);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidKeySpecException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        if (valid || forceProcessing) {
            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }
            final String orderNumber = parsedParams.get("orderid")[0];
            final String paymentStatus = parsedParams.get("status")[0];
            final boolean refund = false; // TODO: does PaySera support refund notifications? If so then calculate here, if not then remove all code around refunds
            BigDecimal callbackAmount = null;
            try {
                callbackAmount = new BigDecimal(parsedParams.get("amount")[0]).movePointRight(2); // Amount in cents
            } catch (Exception exp) {
                LOG.error("Callback for {} did not have a valid amount {}", orderNumber, parsedParams.get("amount")[0]);
            }

            return new BasicCallbackInfoImpl(
                    orderNumber,
                    refund ? CallbackOperation.REFUND : CallbackOperation.PAYMENT,
                    callbackAmount, privateCallBackParameters, valid
            );
        } else {
            LOG.debug("Signature is not valid");
        }
        return new BasicCallbackInfoImpl(
                null,
                CallbackOperation.INVALID,
                null, privateCallBackParameters, valid
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

    	// I'm not getting to this point with a breakpoint, so not yet implemented
    	
        final Map<String, String[]> request = HttpParamsUtils.createArrayValueMap(callbackResult);

        final Map<String, String> data = null; // TODO: decode privateCallBackParameters data parameter

        final boolean valid = false; // TODO: ss1 validation of the parameters privateCallBackParameters

        final String paymentStatus = data.get("status");

        if (valid || forceProcessing) {

            if (valid) {
                LOG.debug("Signature is valid");
            } else {
                LOG.warn("Signature is not valid ... forced processing");
            }

            // TODO: write logic to detect payment status correctly
            /* As per documentation:
            Payment status:
                0 - payment has not been executed
                      TODO: find out how this works
                1 - payment successful
                      TODO: potentially return CallbackAware.CallbackResult.OK
                2 - payment order accepted, but not yet executed (this status does not guarantee execution of the payment)
                      TODO: potentially return CallbackAware.CallbackResult.UNSETTLED
                3 - additional payment information
                      TODO: find out how this works
             */

            final boolean settled = false; // TODO: populate right value depending on the status

            if (settled) {
                LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.OK);
                return CallbackAware.CallbackResult.OK;
            }
            LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.UNSETTLED);
            return CallbackAware.CallbackResult.UNSETTLED;
        } else {
            LOG.debug("Signature is not valid");
        }
        LOG.debug("Payment result is {}: {}", paymentStatus, CallbackAware.CallbackResult.FAILED);
        return CallbackAware.CallbackResult.FAILED;

    }


    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

        // TODO: preprocess hook, maybe usefull for paymentstatus=3 (additional payment information) messages


        if (PaymentGateway.REFUND_NOTIFY.equals(processorOperation) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            // TODO: remap parameters or pre-process payment if necessary, see PayPalButton impl
        }


    }

    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

        if ((PaymentGateway.REFUND.equals(processorOperation) || PaymentGateway.VOID_CAPTURE.equals(processorOperation)) && callback != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callback.getParameters());

            // TODO: remap parameters or post-process payment if necessary, see PayPalButton impl

            payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

        }

    }

    private void setParameterIfNotNull(final Map<String, String> params, final String key, final String valueKey) {
        final String value = getParameterValue(valueKey);
        if (StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorizeCapture(final Payment payment, final boolean forceProcessing) {
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorize(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment reverseAuthorization(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment capture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment voidCapture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment refund(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount, final String currencyCode, final String orderReference, final Payment payment) {

        final StringBuilder form = new StringBuilder();
    	
    	// Construct a PaySera request
    	PaySeraRequest request = new PaySeraRequest();
    	request.setProjectId(getParameterValue(PSC_PROJECTID));
    	request.setVersion(getParameterValue(PSC_API_VERSION));
    	final String envMode = getParameterValue(PSC_ENVIRONMENT);
    	request.setTest(!"live".equalsIgnoreCase(envMode));
    	request.setCallbackUrl(getParameterValue(PSC_CALLBACKURL));
    	request.setAcceptUrl(getParameterValue(PSC_ACCEPTURL));
    	request.setCancelUrl(getParameterValue(PSC_CANCELURL));
    	request.setCurrency(currencyCode);
    	request.setAmount(amount != null ? amount.movePointRight(2).intValueExact() : null);
    	request.setOrderId(orderReference);
    	request.setLanguage(paymentLocaleTranslator.translateLocale(this, locale));
    	if (payment.getBillingAddress() != null) {
    		request.setPayerFirstName(payment.getBillingAddress().getFirstname());
    		request.setPayerLastName(payment.getBillingAddress().getLastname());
    		request.setEmail(payment.getBillingEmail());
    	}
    	if (payment.getShippingAddress() != null) {
    		request.setPayerStreet(StringUtils.join(Arrays.asList(
                    payment.getShippingAddress().getAddrline1(),
                    payment.getShippingAddress().getAddrline2()), ' '));
    		request.setPayerCity(payment.getShippingAddress().getCity());
    		request.setPayerCountryCode(payment.getShippingAddress().getCountryCode());
    		request.setPayerState(payment.getShippingAddress().getStateCode());
    		request.setPayerZip(payment.getShippingAddress().getPostcode());
    	}
    	final StringBuilder paytext = new StringBuilder();
    	paytext.append("Atliktas mokėjimas už užsakytas prekes [site_name] parduotuvėje (užsakymo numeris - [order_nr]):");
    	paytext.append("\n");
    	for (final PaymentLine item : payment.getOrderItems()) {
    		paytext.append("* " + item.getSkuName() + " x" + item.getQuantity());
    		paytext.append("\n");
    	}
    	request.setPayText(paytext.length() < 255 ? paytext.toString() : paytext.substring(0, 255));
    	
//        onlyPayment TODO check if we need these parameters
//        disallowPayments
//        timeLimit
    	    	
    	String data = request.toBase64String();
    	form.append(getHiddenFieldValue("data", data));
    	
    	String sign = HttpQueryUtility.calculateMD5(data + getParameterValue(PSC_SIGN_PASSWORD));
    	form.append(getHiddenFieldValue("sign", sign));

        LOG.debug("PaySeraCheckout form request: {}", form);

        return form.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation,
                                          final Map map,
                                          final boolean forceProcessing) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> params = HttpParamsUtils.createSingleValueMap(map);

        // TODO: update payment object with payment callback data, see PayPalButton impl

        return payment;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }
    
    @Override
    public String getLabel() {
        return "paySeraCheckoutPaymentGateway";
    }
    
    public static String getText(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }
    
    public static String getTextFile(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

}
