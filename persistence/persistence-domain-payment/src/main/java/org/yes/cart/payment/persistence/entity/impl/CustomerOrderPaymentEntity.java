/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class CustomerOrderPaymentEntity implements CustomerOrderPayment, Serializable {

    private static final long serialVersionUID = 20100714L;

    private long customerOrderPaymentId;
    private String cardType;
    private String cardHolderName;
    private String cardExpireYear;
    private String cardExpireMonth;
    private LocalDate cardStartDate;
    private LocalDateTime orderDate;
    private BigDecimal paymentAmount;
    private BigDecimal taxAmount;
    private String orderCurrency;
    private String orderNumber;
    private String orderShipment;
    private String shopCode;
    private String transactionReferenceId;
    private String transactionRequestToken;
    private String transactionAuthorizationCode;
    private String transactionGatewayLabel;
    private String transactionOperation;
    private String transactionOperationResultCode;
    private String transactionOperationResultMessage;
    private String paymentProcessorResult;
    private boolean paymentProcessorBatchSettlement;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;
    private String shopperIpAddress;

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
     * @return cart type
     */
    public String getCardType() {
        return this.cardType;
    }

    /**
     * Set cart type.
     *
     * @param cardType type
     */
    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardHolderName() {
        return this.cardHolderName;
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
    public String getCardExpireYear() {
        return this.cardExpireYear;
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
        return this.cardExpireMonth;
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
    public LocalDate getCardStartDate() {
        return this.cardStartDate;
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
        return this.orderDate;
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
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
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
    public String getOrderCurrency() {
        return this.orderCurrency;
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
    public String getOrderNumber() {
        return this.orderNumber;
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
        return this.orderShipment;
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
        return this.transactionReferenceId;
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
        return this.transactionRequestToken;
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
        return this.transactionAuthorizationCode;
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
    public String getTransactionGatewayLabel() {
        return this.transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionGatewayLabel(final String transactionGatewayLabel) {
        this.transactionGatewayLabel = transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionOperation() {
        return this.transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionOperation(final String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    /** {@inheritDoc} */
    @Override
    public String getTransactionOperationResultCode() {
        return this.transactionOperationResultCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionOperationResultCode(final String transactionOperationResultCode) {
        this.transactionOperationResultCode = transactionOperationResultCode;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransactionOperationResultMessage() {
        return this.transactionOperationResultMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionOperationResultMessage(final String transactionOperationResultMessage) {
        this.transactionOperationResultMessage = transactionOperationResultMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPaymentProcessorResult() {
        return this.paymentProcessorResult;
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
        return this.paymentProcessorBatchSettlement;
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
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGuid() {
        return this.guid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long getCustomerOrderPaymentId() {
        return this.customerOrderPaymentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomerOrderPaymentId(final long customerOrderPaymentId) {
        this.customerOrderPaymentId = customerOrderPaymentId;
    }


    private String cardNumber;
    private String cardIssueNumber;


    /**
     * Get 4 last digits from credit card.
     * Actual cc number not persisted.
     *
     * @return 4 last digits of credit card.
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
        if (cardNumber != null && cardNumber.length() > 4) {
            this.cardNumber = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        } else {
            this.cardNumber = cardNumber;
        }
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
    public String getShopCode() {
        return shopCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }
}


