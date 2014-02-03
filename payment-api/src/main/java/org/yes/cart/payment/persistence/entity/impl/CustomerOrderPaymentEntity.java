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

package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    private Date cardStartDate;
    private Date orderDate;
    private BigDecimal paymentAmount;
    private String orderCurrency;
    private String orderNumber;
    private String orderShipment;
    private String transactionReferenceId;
    private String transactionRequestToken;
    private String transactionAuthorizationCode;
    private String transactionGatewayLabel;
    private String transactionOperation;
    private String transactionOperationResultCode;
    private String transactionOperationResultMessage;
    private String paymentProcessorResult;
    private boolean paymentProcessorBatchSettlement;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;
    private String shopperIpAddress;

    /**
     * {@inheritDoc}
     */
    public String getShopperIpAddress() {
        return shopperIpAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void setShopperIpAddress(final String shopperIpAddress) {
        this.shopperIpAddress = shopperIpAddress;
    }

    /**
     * {@inheritDoc}
     */
    public String getCardType() {
        return this.cardType;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }

    /**
     * {@inheritDoc}
     */
    public String getCardHolderName() {
        return this.cardHolderName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


    /**
     * {@inheritDoc}
     */
    public String getCardExpireYear() {
        return this.cardExpireYear;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardExpireYear(final String cardExpireYear) {
        this.cardExpireYear = cardExpireYear;
    }

    /**
     * {@inheritDoc}
     */
    public String getCardExpireMonth() {
        return this.cardExpireMonth;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardExpireMonth(final String cardExpireMonth) {
        this.cardExpireMonth = cardExpireMonth;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCardStartDate() {
        return this.cardStartDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardStartDate(final Date cardStartDate) {
        this.cardStartDate = cardStartDate;
    }

    /**
     * {@inheritDoc}
     */
    public Date getOrderDate() {
        return this.orderDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderDate(final Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaymentAmount(final BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderCurrency() {
        return this.orderCurrency;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderCurrency(final String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderShipment() {
        return this.orderShipment;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderShipment(final String orderShipment) {
        this.orderShipment = orderShipment;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionReferenceId() {
        return this.transactionReferenceId;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionReferenceId(final String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionRequestToken() {
        return this.transactionRequestToken;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionRequestToken(final String transactionRequestToken) {
        this.transactionRequestToken = transactionRequestToken;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionAuthorizationCode() {
        return this.transactionAuthorizationCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionAuthorizationCode(final String transactionAuthorizationCode) {
        this.transactionAuthorizationCode = transactionAuthorizationCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionGatewayLabel() {
        return this.transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionGatewayLabel(final String transactionGatewayLabel) {
        this.transactionGatewayLabel = transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionOperation() {
        return this.transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperation(final String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    /** {@inheritDoc} */
    public String getTransactionOperationResultCode() {
        return this.transactionOperationResultCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperationResultCode(final String transactionOperationResultCode) {
        this.transactionOperationResultCode = transactionOperationResultCode;
    }


    /**
     * {@inheritDoc}
     */
    public String getTransactionOperationResultMessage() {
        return this.transactionOperationResultMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperationResultMessage(final String transactionOperationResultMessage) {
        this.transactionOperationResultMessage = transactionOperationResultMessage;
    }

    /**
     * {@inheritDoc}
     */
    public String getPaymentProcessorResult() {
        return this.paymentProcessorResult;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaymentProcessorResult(final String paymentProcessorResult) {
        this.paymentProcessorResult = paymentProcessorResult;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPaymentProcessorBatchSettlement() {
        return this.paymentProcessorBatchSettlement;
    }

    /**
     * {@inheritDoc}
     */
    public void setPaymentProcessorBatchSettlement(final boolean paymentProcessorBatchSettlement) {
        this.paymentProcessorBatchSettlement = paymentProcessorBatchSettlement;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * {@inheritDoc}
     */
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return this.guid;
    }

    /**
     * {@inheritDoc}
     */
    public void setGuid(final String guid) {
        this.guid = guid;
    }


    /**
     * {@inheritDoc}
     */
    public long getCustomerOrderPaymentId() {
        return this.customerOrderPaymentId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCustomerOrderPaymentId(final long customerOrderPaymentId) {
        this.customerOrderPaymentId = customerOrderPaymentId;
    }


    private String cardNumber;
    private String cardIsuueNumber;


    /**
     * Get 4 last digits from credit card.
     * Actual cc number not persisted.
     *
     * @return 4 last digits of credit card.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Store last 4 digits only, because of:
     * 1. security reason
     * 2. authorize net need for credit operation  4 last digits
     *
     * @param cardNumber card number.
     */
    /**
     * {@inheritDoc}
     */
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
    public String getCardIssueNumber() {
        return cardIsuueNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardIssueNumber(final String cardIssueNumber) {
        this.cardIsuueNumber = cardIssueNumber;
    }


}


