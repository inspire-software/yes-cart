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

package org.yes.cart.payment.dto.impl;


import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
@SuppressWarnings("PMD.TooManyFields")
public class PaymentImpl implements Payment {

    private String cardType; //Mastercard, visa, etc.
    private String cardHolderName;
    private String cardNumber; // is 4 last digits will be enough to store ?
    private String cardExpireYear;
    private String cardExpireMonth;
    private String cardIssueNumber;
    private String cardCvv2Code;
    private LocalDate cardStartDate;

    // when order was created
    private LocalDateTime orderDate;
    private BigDecimal paymentAmount;
    private BigDecimal taxAmount;
    private String orderCurrency;
    private String orderLocale;
    private List<PaymentLine> orderItems;
    private String orderNumber;
    private String orderShipment;


    private String transactionReferenceId;
    private String transactionRequestToken;
    private String transactionAuthorizationCode;

    private String transactionOperation;
    private String transactionOperationResultCode;
    private String transactionOperationResultMessage;
    private String transactionGatewayLabel;


    private String shippingAddressString;
    private String billingAddressString;

    private String paymentProcessorResult = Payment.PAYMENT_STATUS_PROCESSING;
    private boolean paymentProcessorBatchSettlement;

    private PaymentAddress shippingAddress;

    private PaymentAddress billingAddress;

    private String billingEmail;

    private String shopperIpAddress;


    /**
     * Default constructor.
     */
    public PaymentImpl() {
        orderItems = new ArrayList<>();
        paymentAmount = BigDecimal.ZERO;
        taxAmount = BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShopperIpAddress() {
        return shopperIpAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShopperIpAddress(final String shopperIpAddress) {
        this.shopperIpAddress = shopperIpAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentAmount(final BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTaxAmount(final BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPaymentProcessorResult() {
        return paymentProcessorResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentProcessorResult(final String paymentProcessorResult) {
        this.paymentProcessorResult = paymentProcessorResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPaymentProcessorBatchSettlement() {
        return paymentProcessorBatchSettlement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPaymentProcessorBatchSettlement(final boolean paymentProcessorBatchSettlement) {
        this.paymentProcessorBatchSettlement = paymentProcessorBatchSettlement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBillingEmail() {
        return billingEmail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingEmail(final String billingEmail) {
        this.billingEmail = billingEmail;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardType() {
        return cardType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardExpireYear() {
        return cardExpireYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardExpireYear(final String cardExpireYear) {
        this.cardExpireYear = cardExpireYear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardExpireMonth() {
        return cardExpireMonth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardExpireMonth(final String cardExpireMonth) {
        this.cardExpireMonth = cardExpireMonth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardIssueNumber() {
        return cardIssueNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardIssueNumber(final String cardIssueNumber) {
        this.cardIssueNumber = cardIssueNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardCvv2Code() {
        return cardCvv2Code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardCvv2Code(final String cardCvv2Code) {
        this.cardCvv2Code = cardCvv2Code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getCardStartDate() {
        return cardStartDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardStartDate(final LocalDate cardStartDate) {
        this.cardStartDate = cardStartDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderDate(final LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderCurrency() {
        return orderCurrency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderCurrency(final String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderLocale() {
        return orderLocale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderLocale(final String orderLocale) {
        this.orderLocale = orderLocale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentLine> getOrderItems() {
        return orderItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderItems(final List<PaymentLine> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderShipment() {
        return orderShipment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOrderShipment(final String orderShipment) {
        this.orderShipment = orderShipment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionReferenceId() {
        return transactionReferenceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionReferenceId(final String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionRequestToken() {
        return transactionRequestToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionRequestToken(final String transactionRequestToken) {
        this.transactionRequestToken = transactionRequestToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionAuthorizationCode() {
        return transactionAuthorizationCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionAuthorizationCode(final String transactionAuthorizationCode) {
        this.transactionAuthorizationCode = transactionAuthorizationCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionOperation() {
        return transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionOperation(final String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionOperationResultCode() {
        return transactionOperationResultCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionOperationResultCode(final String transactionOperationResultCode) {
        this.transactionOperationResultCode = transactionOperationResultCode;
    }

    @Override
    public String getTransactionOperationResultMessage() {
        return transactionOperationResultMessage;
    }

    @Override
    public void setTransactionOperationResultMessage(final String transactionOperationResultMessage) {
        this.transactionOperationResultMessage = transactionOperationResultMessage;
    }

    @Override
    public String getTransactionGatewayLabel() {
        return transactionGatewayLabel;
    }

    @Override
    public void setTransactionGatewayLabel(final String transactionGatewayLabel) {
        this.transactionGatewayLabel = transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShippingAddressString() {
        return shippingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShippingAddressString(final String shippingAddressString) {
        this.shippingAddressString = shippingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBillingAddressString() {
        return billingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingAddressString(final String billingAddressString) {
        this.billingAddressString = billingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentAddress getShippingAddress() {
        return shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShippingAddress(final PaymentAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBillingAddress(final PaymentAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public String toString() {
        return "PaymentImpl{" +
                "cardType='" + cardType + '\'' +
                ", cardExpireYear='" + cardExpireYear + '\'' +
                ", cardExpireMonth='" + cardExpireMonth + '\'' +
                ", cardStartDate=" + cardStartDate +
                ", orderDate=" + orderDate +
                ", paymentAmount=" + paymentAmount +
                ", orderCurrency='" + orderCurrency + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderShipment='" + orderShipment + '\'' +
                ", transactionReferenceId='" + transactionReferenceId + '\'' +
                ", transactionRequestToken='" + transactionRequestToken + '\'' +
                ", transactionAuthorizationCode='" + transactionAuthorizationCode + '\'' +
                ", shippingAddress='" + shippingAddressString + '\'' +
                ", billingAddress='" + billingAddressString + '\'' +
                ", paymentProcessorResult='" + paymentProcessorResult + '\'' +
                '}';
    }
}
