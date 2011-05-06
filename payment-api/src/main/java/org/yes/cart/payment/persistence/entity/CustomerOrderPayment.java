package org.yes.cart.payment.persistence.entity;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represent peymwnt per order. Each order has at least one payment. In case of several shipments order has several payments.
 * This is limited by Visa and Mastercard policy. Fund capture can be performed for delivered order.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface CustomerOrderPayment extends Auditable {

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getCustomerOrderPaymentId();

    /**
     * Set pk value.
     *
     * @param customerOrderPaymentId pk value.
     */
    void setCustomerOrderPaymentId(long customerOrderPaymentId);


    /**
     * Get card holder name.
     *
     * @return card holder name.
     */
    String getCardHolderName();

    /**
     * Set card holder name.
     *
     * @param cardHolderName card holder name.
     */
    void setCardHolderName(String cardHolderName);

    /**
     * Card expire year.
     *
     * @return expire year.
     */
    String getCardExpireYear();

    /**
     * Set expire year.
     *
     * @param cardExpireYear expire year.
     */
    void setCardExpireYear(String cardExpireYear);

    /**
     * Get expire month.
     *
     * @return expire month.
     */
    String getCardExpireMonth();

    /**
     * Set  expire month.
     *
     * @param cardExpireMonth expire month.
     */
    void setCardExpireMonth(String cardExpireMonth);

    /**
     * Card start date.
     *
     * @return card start date.
     */
    Date getCardStartDate();

    /**
     * Set card start date.
     *
     * @param cardStartDate card start date.
     */
    void setCardStartDate(Date cardStartDate);

    /**
     * Get order date.
     *
     * @return order date.
     */
    Date getOrderDate();

    /**
     * Set order date.
     *
     * @param orderDate order date.
     */
    void setOrderDate(Date orderDate);

    /**
     * Get payment amount, incuding delivery price.
     *
     * @return devilevry amount.
     */
    BigDecimal getPaymentAmount();

    /**
     * Set delivery amount.
     *
     * @param paymentAmount delivery amount.
     */
    void setPaymentAmount(BigDecimal paymentAmount);


    /**
     * Get currency.
     *
     * @return currency.
     */
    String getOrderCurrency();

    /**
     * Set currency.
     *
     * @param orderCurrency currency.
     */
    void setOrderCurrency(String orderCurrency);

    /**
     * Get order number.
     *
     * @return order number.
     */
    String getOrderNumber();

    /**
     * Set order number.
     *
     * @param orderNumber number to set.
     */
    void setOrderNumber(String orderNumber);

    /**
     * Get delivery / shipment number.
     *
     * @return delivery / shipment number
     */
    String getOrderShipment();

    /**
     * Set delivery / shipment number.
     *
     * @param orderShipment delivery / shipment number
     */
    void setOrderShipment(String orderShipment);

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
     * @param transactionReferenceId
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
     * Get the trasaction operation. Like AUTH, AUTH_CAPTURE, etc
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
    void setPaymentProcessorResult(String paymentProcessorResult);

    /**
     * Is AUTH or AUTH_CAPTURE payment was submited to bank for batch settlement.
     *
     * @return true if payment was submited to bank for batch settlement.
     */
    boolean isPaymentProcessorBatchSettlement();

    /**
     * Set batch settlement flag.
     *
     * @param paymentProcessorBatchSettlement
     *         batch settlement flag
     */
    void setPaymentProcessorBatchSettlement(boolean paymentProcessorBatchSettlement);


    /**
     * Get card number.
     *
     * @return card number.
     */
    String getCardNumber();

    /**
     * Set card number.
     * Store last 4 digits only, because of:
     * 1. security reason
     * 2. authorize net need for credit operation  4 last digits
     *
     * @param cardNumber card number.
     */
    void setCardNumber(String cardNumber);

    /**
     * Get card issue number.
     *
     * @return card issue number.
     */
    String getCardIsuueNumber();

    /**
     * Set  card issue number.
     *
     * @param cardIsuueNumber card issue number.
     */
    void setCardIsuueNumber(String cardIsuueNumber);


}
