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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:15
 */
@Dto
public class VoPayment {

    @DtoField(readOnly = true)
    private long customerOrderPaymentId;
    @DtoField(readOnly = true)
    private String cardHolderName;
    @DtoField(readOnly = true)
    private String cardNumber;
    @DtoField(readOnly = true)
    private String cardExpireYear;
    @DtoField(readOnly = true)
    private String cardExpireMonth;
    @DtoField(readOnly = true)
    private Date cardStartDate;
    @DtoField(readOnly = true)
    private Date orderDate;
    @DtoField(readOnly = true)
    private BigDecimal paymentAmount;
    @DtoField(readOnly = true)
    private BigDecimal taxAmount;
    @DtoField(readOnly = true)
    private String orderCurrency;
    @DtoField(readOnly = true)
    private String orderNumber;
    @DtoField(readOnly = true)
    private String orderShipment;
    @DtoField(readOnly = true)
    private String shopCode;
    @DtoField(readOnly = true)
    private String transactionReferenceId;
    @DtoField(readOnly = true)
    private String transactionRequestToken;
    @DtoField(readOnly = true)
    private String transactionAuthorizationCode;
    @DtoField(readOnly = true)
    private String transactionGatewayLabel;
    @DtoField(readOnly = true)
    private String transactionOperation;
    @DtoField(readOnly = true)
    private String transactionOperationResultCode;
    @DtoField(readOnly = true)
    private String transactionOperationResultMessage;
    @DtoField(readOnly = true)
    private String paymentProcessorResult;
    @DtoField(readOnly = true)
    private boolean paymentProcessorBatchSettlement;
    @DtoField(readOnly = true)
    private Date createdTimestamp;
    @DtoField(readOnly = true)
    private Date updatedTimestamp;
    @DtoField(readOnly = true)
    private String shopperIpAddress;

    public long getCustomerOrderPaymentId() {
        return customerOrderPaymentId;
    }

    public void setCustomerOrderPaymentId(final long customerOrderPaymentId) {
        this.customerOrderPaymentId = customerOrderPaymentId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpireYear() {
        return cardExpireYear;
    }

    public void setCardExpireYear(final String cardExpireYear) {
        this.cardExpireYear = cardExpireYear;
    }

    public String getCardExpireMonth() {
        return cardExpireMonth;
    }

    public void setCardExpireMonth(final String cardExpireMonth) {
        this.cardExpireMonth = cardExpireMonth;
    }

    public Date getCardStartDate() {
        return cardStartDate;
    }

    public void setCardStartDate(final Date cardStartDate) {
        this.cardStartDate = cardStartDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(final Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(final BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(final BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(final String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderShipment() {
        return orderShipment;
    }

    public void setOrderShipment(final String orderShipment) {
        this.orderShipment = orderShipment;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public String getTransactionReferenceId() {
        return transactionReferenceId;
    }

    public void setTransactionReferenceId(final String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
    }

    public String getTransactionRequestToken() {
        return transactionRequestToken;
    }

    public void setTransactionRequestToken(final String transactionRequestToken) {
        this.transactionRequestToken = transactionRequestToken;
    }

    public String getTransactionAuthorizationCode() {
        return transactionAuthorizationCode;
    }

    public void setTransactionAuthorizationCode(final String transactionAuthorizationCode) {
        this.transactionAuthorizationCode = transactionAuthorizationCode;
    }

    public String getTransactionGatewayLabel() {
        return transactionGatewayLabel;
    }

    public void setTransactionGatewayLabel(final String transactionGatewayLabel) {
        this.transactionGatewayLabel = transactionGatewayLabel;
    }

    public String getTransactionOperation() {
        return transactionOperation;
    }

    public void setTransactionOperation(final String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    public String getTransactionOperationResultCode() {
        return transactionOperationResultCode;
    }

    public void setTransactionOperationResultCode(final String transactionOperationResultCode) {
        this.transactionOperationResultCode = transactionOperationResultCode;
    }

    public String getTransactionOperationResultMessage() {
        return transactionOperationResultMessage;
    }

    public void setTransactionOperationResultMessage(final String transactionOperationResultMessage) {
        this.transactionOperationResultMessage = transactionOperationResultMessage;
    }

    public String getPaymentProcessorResult() {
        return paymentProcessorResult;
    }

    public void setPaymentProcessorResult(final String paymentProcessorResult) {
        this.paymentProcessorResult = paymentProcessorResult;
    }

    public boolean isPaymentProcessorBatchSettlement() {
        return paymentProcessorBatchSettlement;
    }

    public void setPaymentProcessorBatchSettlement(final boolean paymentProcessorBatchSettlement) {
        this.paymentProcessorBatchSettlement = paymentProcessorBatchSettlement;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getShopperIpAddress() {
        return shopperIpAddress;
    }

    public void setShopperIpAddress(final String shopperIpAddress) {
        this.shopperIpAddress = shopperIpAddress;
    }
}


