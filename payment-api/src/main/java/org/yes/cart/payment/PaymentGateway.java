package org.yes.cart.payment;

import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGateway extends Serializable {

    String AUTH = "AUTH";
    String REVERSE_AUTH = "REVERSE_AUTH";
    String CAPTURE = "CAPTURE";
    String AUTH_CAPTURE = "AUTH_CAPTURE";
    String VOID_CAPTURE = "VOID_CAPTURE";
    String REFUND = "REFUND";

    /**
     * Get the whole or part of html form to fill data for payment.
     *
     * @param cardHolderName card  hiolder name hint based on customer profile
     * @param locale         to get localized html
     * @param amount         amount payment , used for external payment processing
     * @return html.
     */
    String getHtmlForm(String cardHolderName, String locale, BigDecimal amount, String orderGuid);

    /**
     * Authorize and capture payment. Not all gataways allow to capture payment without order delivery.
     * A credit card transaction request to authorize and capture, or settle,
     * funds for a purchase. The payment gateway submits the request to the card issuing bank
     * for authorization and upon approval, will automatically submit the
     * transaction for settlement.
     * <p/>
     * Also this method must be used in case of external form
     * processing like Authorize.net Sim method & etc.
     *
     * @param payment to capture.
     * @return payment
     */
    Payment authorizeCapture(Payment payment);


    /**
     * Authorize a payment. The response from a card issuing bank to a
     * merchant's transaction authorization request indicating that payment information is valid
     * and funds are available on the customer's credit card.
     *
     * @param payment to authorize.
     * @return payment. Gateways can return reference id , token , etc
     */
    Payment authorize(Payment payment);

    /**
     * Reverse the authorization.
     *
     * @param payment payment
     * @return payment.
     */
    Payment reverseAuthorization(Payment payment);

    /**
     * Payment capture on authorized card.
     * Prior Authorization Capture A credit card transaction request to capture funds for
     * a separate, previously authorized authorization-only transaction.
     * With this type of transaction, the merchant will submit an authorization code
     * that was received from the issuing bank at the time of the original authorization-only
     * transaction.
     *
     * @param payment to capture.
     * @return payment
     */
    Payment capture(Payment payment);


    /**
     * Void transactions of this type are used to cancel original charge transactions
     * that have not yet been submitted for batch settlement. For this type of transaction,
     * the merchant must submit the transaction ID of the original charge transaction against
     * which the Void is being submitted.
     * <p/>
     * No further action may be taken for Void transactions.
     * To cancel a transaction that has already settled, a <b>refund</b> must be submitted.
     *
     * @param payment to void capture.
     * @return payment
     */
    Payment voidCapture(Payment payment);


    /**
     * A credit card transaction request to post funds from the merchant's
     * bank account back to the customer's credit card account as a
     * refund for a previous charge transaction. For this type of
     * transaction, the merchant must submit the transaction ID of the
     * original charge transaction against which the refund is being applied.
     * <p/>
     * AKA as Credit operation.
     *
     * @param payment to refund
     * @return payment
     */
    Payment refund(Payment payment);


    /**
     * Create payment prototype from given map.
     * In case of external form processing return value must be complettely filled , because  #authorizeCapture
     * will simple return given argument
     *
     * @param map given map of parameters, from http request. Each Payment gateway know how to
     *            create template payment from HttpServletRequest#getParameterMap and configuration parameters.
     * @return payment template.
     */
    Payment createPaymentPrototype(Map map);


    /**
     * Get unique payment gateway label. <b>Must</b> correspond to
     * descriptor entry in configaration and vise versa
     *
     * @return unique payment gateway label.
     */
    String getLabel();

    /**
     * Get payment gateway supported features.
     *
     * @return feature set of mayment gateway.
     */
    PaymentGatewayFeature getPaymentGatewayFeatures();

    /**
     * Get parameters.
     *
     * @return PG parameters.
     */
    Collection<PaymentGatewayParameter> getPaymentGatewayParameters();

    /**
     * Delete configuration parameter.
     *
     * @param parameterLabel configuration parameter label.
     */
    void deleteParameter(String parameterLabel);

    /**
     * Add new configuration parameter.
     *
     * @param paymentGatewayParameter configuraionparameter.
     */
    void addParameter(PaymentGatewayParameter paymentGatewayParameter);

    /**
     * Update configurationparameter.
     *
     * @param paymentGatewayParameter {@link PaymentGatewayParameter}
     */
    void updateParameter(PaymentGatewayParameter paymentGatewayParameter);

}
