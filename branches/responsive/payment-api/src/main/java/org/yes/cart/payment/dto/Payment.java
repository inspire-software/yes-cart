/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
    String PAYMENT_STATUS_FAILED = "Failed";
    /**
     * Payment in process.
     */
    String PAYMENT_STATUS_PROCESSING = "Processing";
    /**
     * Payment ok.
     */
    String PAYMENT_STATUS_OK = "Ok";


    /**
     * Get delivery amount, incuding delivery price.
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
    String getCardType();

    /**
     * Set cart type.
     *
     * @param cardType cart type.
     */
    void setCardType(final String cardType);

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
    void setCardHolderName(final String cardHolderName);

    /**
     * Get card number.
     *
     * @return card number.
     */
    String getCardNumber();

    /**
     * Set card number.
     *
     * @param cardNumber card number.
     */
    void setCardNumber(final String cardNumber);

    /**
     * Get year of expiration.
     *
     * @return year of expiration
     */
    String getCardExpireYear();

    /**
     * Set  year of expiration
     *
     * @param cardExpireYear year of expiration
     */
    void setCardExpireYear(final String cardExpireYear);

    /**
     * Get month of expiration.
     *
     * @return month of expiration.
     */
    String getCardExpireMonth();

    /**
     * Set month of expiration.
     *
     * @param cardExpireMonth month of expiration.
     */
    void setCardExpireMonth(final String cardExpireMonth);

    /**
     * Get issue card number.
     *
     * @return issue card number.
     */
    String getCardIsuueNumber();

    /**
     * Set  issue card number.
     *
     * @param cardIsuueNumber issue card number.
     */
    void setCardIsuueNumber(final String cardIsuueNumber);

    /**
     * Get cvv2 card number.
     *
     * @return cvv2 card number.
     */
    String getCardCvv2Code();

    /**
     * Set cvv2 card number.
     *
     * @param cardCvv2Code cvv2 card number.
     */
    void setCardCvv2Code(final String cardCvv2Code);

    /**
     * Get card start date.
     *
     * @return card start date.
     */
    Date getCardStartDate();

    /**
     * Set  card start date.
     *
     * @param cardStartDate card start date.
     */
    void setCardStartDate(final Date cardStartDate);

    /**
     * Get order date.
     *
     * @return order date.
     */
    Date getOrderDate();

    /**
     * Set  order date.
     *
     * @param orderDate order date.
     */
    void setOrderDate(final Date orderDate);


    /**
     * Currency of order.
     *
     * @return currency.
     */
    String getOrderCurrency();

    /**
     * Set currency.
     *
     * @param orderCurrency currency.
     */
    void setOrderCurrency(final String orderCurrency);

    /**
     * Locale of order.
     *
     * @return locale.
     */
    String getOrderLocale();

    /**
     * Set locale.
     *
     * @param orderLocale locale.
     */
    void setOrderLocale(final String orderLocale);

    /**
     * Get items in current shipment to pay. One of the line will hold delivery record to pay in case
     * of physical delivery.
     *
     * @return order  items.
     */
    List<PaymentLine> getOrderItems();

    /**
     * Set  items.
     *
     * @param orderItems order  items.
     */
    void setOrderItems(final List<PaymentLine> orderItems);

    /**
     * Original order number.
     *
     * @return order number.
     */
    String getOrderNumber();

    /**
     * Set  order number.
     *
     * @param orderNumber order number.
     */
    void setOrderNumber(final String orderNumber);

    /**
     * Get order shipment num.
     *
     * @return order shipment.
     */
    String getOrderShipment();

    /**
     * Set order shipment number.
     *
     * @param orderShipment order shipment.
     */
    void setOrderShipment(final String orderShipment);


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
