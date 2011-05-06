package org.yes.cart.payment.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public interface Payment extends Serializable {


    /**
     * Failed status of whole order payment.
     */
    public String PAYMENT_STATUS_FAILED = "Failed";
    /**
     * Payment in process.
     */
    public String PAYMENT_STATUS_PROCESSING = "Processing";
    /**
     * Payment ok.
     */
    public String PAYMENT_STATUS_OK = "Ok";


    /**
     * Get delivery amount, incuding delivery price.
     *
     * @return devilevry amount.
     */
    BigDecimal getOrderDeliveryAmount();

    /**
     * Set delivery amount.
     *
     * @param orderDeliveryAmount delivery amount.
     */
    void setOrderDeliveryAmount(BigDecimal orderDeliveryAmount);


    /**
     * Get adapted from payment gateway result.
     * Not filled at PG , but on payment processor.
     *
     * @return adapted from payment gateway result.
     */
    String getPaymentProcessorResult();

    /**
     * Set adapted from payment gateway result
     *
     * @param paymentProcessorResult adapted from payment gateway result.
     */
    void setPaymentProcessorResult(final String paymentProcessorResult);


    /**
     * Get cart type.
     *
     * @return cart type.
     */
    public String getCardType();

    /**
     * Set cart type.
     *
     * @param cardType cart type.
     */
    public void setCardType(final String cardType);

    /**
     * Get card holder name.
     *
     * @return card holder name.
     */
    public String getCardHolderName();

    /**
     * Set card holder name.
     *
     * @param cardHolderName card holder name.
     */
    public void setCardHolderName(final String cardHolderName);

    /**
     * Get card number.
     *
     * @return card number.
     */
    public String getCardNumber();

    /**
     * Set card number.
     *
     * @param cardNumber card number.
     */
    public void setCardNumber(final String cardNumber);

    /**
     * Get year of expiration.
     *
     * @return year of expiration
     */
    public String getCardExpireYear();

    /**
     * Set  year of expiration
     *
     * @param cardExpireYear year of expiration
     */
    public void setCardExpireYear(final String cardExpireYear);

    /**
     * Get month of expiration.
     *
     * @return month of expiration.
     */
    public String getCardExpireMonth();

    /**
     * Set month of expiration.
     *
     * @param cardExpireMonth month of expiration.
     */
    public void setCardExpireMonth(final String cardExpireMonth);

    /**
     * Get issue card number.
     *
     * @return issue card number.
     */
    public String getCardIsuueNumber();

    /**
     * Set  issue card number.
     *
     * @param cardIsuueNumber issue card number.
     */
    public void setCardIsuueNumber(final String cardIsuueNumber);

    /**
     * Get cvv2 card number.
     *
     * @return cvv2 card number.
     */
    public String getCardCvv2Code();

    /**
     * Set cvv2 card number.
     *
     * @param cardCvv2Code cvv2 card number.
     */
    public void setCardCvv2Code(final String cardCvv2Code);

    /**
     * Get card start date.
     *
     * @return card start date.
     */
    public Date getCardStartDate();

    /**
     * Set  card start date.
     *
     * @param cardStartDate card start date.
     */
    public void setCardStartDate(final Date cardStartDate);

    /**
     * Get order date.
     *
     * @return order date.
     */
    public Date getOrderDate();

    /**
     * Set  order date.
     *
     * @param orderDate order date.
     */
    public void setOrderDate(final Date orderDate);


    /**
     * Currency of order.
     *
     * @return currency.
     */
    public String getOrderCurrency();

    /**
     * Set currency.
     *
     * @param orderCurrency currency.
     */
    public void setOrderCurrency(final String orderCurrency);

    /**
     * Get items in current shipment to pay. One of the line will hold delivery record to pay in case
     * of physical delivery.
     *
     * @return order  items.
     */
    public List<PaymentLine> getOrderItems();

    /**
     * Set  items.
     *
     * @param orderItems order  items.
     */
    public void setOrderItems(final List<PaymentLine> orderItems);

    /**
     * Original order number.
     *
     * @return order number.
     */
    public String getOrderNumber();

    /**
     * Set  order number.
     *
     * @param orderNumber order number.
     */
    public void setOrderNumber(final String orderNumber);

    /**
     * Get order shipment num.
     *
     * @return order shipment.
     */
    public String getOrderShipment();

    /**
     * Set order shipment number.
     *
     * @param orderShipment order shipment.
     */
    public void setOrderShipment(final String orderShipment);


    /**
     * Get transaction reference id. Ref id set by payment gateway. In out case it will be eq to delivery nimber
     * if PG does not set value.
     *
     * @return reference id
     */
    String getTransactionReferenceId();

    /**
     * Set transaction reference id.
     *
     * @param transactionReferenceId ref id to set.
     */
    void setTransactionReferenceId(String transactionReferenceId);

    /**
     * Get transaction request token. Request token than can be used as reference in pair oreation like
     * authorize - capture or authorize - reverce authorization.
     * Often correspond to reference id.
     *
     * @return transaction request token.
     */
    String getTransactionRequestToken();

    /**
     * Set transaction request token.
     *
     * @param transactionRequestToken transaction request token.
     */
    void setTransactionRequestToken(String transactionRequestToken);

    /**
     * Get the auth code, that filled by each transaction. Can be used as "follow to" reference number.
     *
     * @return authorization code
     */
    String getTransactionAuthorizationCode();

    /**
     * Set authorization code.
     *
     * @param transactionAuthorizationCode authorization code
     */
    void setTransactionAuthorizationCode(String transactionAuthorizationCode);


    /**
     * Label (spring id) of payment gateways.
     *
     * @return transaction pg label
     */
    String getTransactionGatewayLabel();

    /**
     * Set label of paymeng gataway.
     *
     * @param transactionGatewayLabel label of paymeng gataway.
     */
    void setTransactionGatewayLabel(String transactionGatewayLabel);

    /**
     * Get the trasaction operation.
     *
     * @return operation.
     */
    String getTransactionOperation();

    /**
     * Set transaction operation.
     *
     * @param transactionOperation transaction operation.
     */
    void setTransactionOperation(String transactionOperation);


    /**
     * Get rez code on pg.
     *
     * @return operation rez code.
     */
    String getTransactionOperationResultCode();

    /**
     * Set op rez code.
     *
     * @param transactionOperationResultCode op rez code.
     */
    void setTransactionOperationResultCode(String transactionOperationResultCode);

    /**
     * Get message if any.
     *
     * @return result messagae
     */
    String getTransactionOperationResultMessage();

    /**
     * Set transaction result message if any.
     *
     * @param transactionOperationResultMessage
     *         transaction result message.
     */
    void setTransactionOperationResultMessage(String transactionOperationResultMessage);

    /**
     * Get shipping address.
     *
     * @return shipping address.
     */
    String getShippingAddressString();

    /**
     * Set shipping address.
     *
     * @param shippingAddressString shipping address
     */
    void setShippingAddressString(String shippingAddressString);

    /**
     * Get billing address.
     *
     * @return billing address
     */
    String getBillingAddressString();

    /**
     * Set billign address.
     *
     * @param billingAddressString billilgn address.
     */
    void setBillingAddressString(String billingAddressString);

    /**
     * Get billing email
     *
     * @return get billing email
     */
    String getBillingEmail();


    /**
     * Set  billing email
     *
     * @param billingEmail email.
     */
    void setBillingEmail(String billingEmail);


    /**
     * Get address.
     *
     * @return address.
     */
    PaymentAddress getShippingAddress();


    /**
     * Set address.
     *
     * @param shippingAddress address.
     */
    void setShippingAddress(PaymentAddress shippingAddress);


    /**
     * Get address.
     *
     * @return address.
     */
    PaymentAddress getBillingAddress();


    /**
     * Set address.
     *
     * @param billingAddress address.
     */
    void setBillingAddress(PaymentAddress billingAddress);


}
