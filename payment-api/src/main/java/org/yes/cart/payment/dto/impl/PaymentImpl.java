package org.yes.cart.payment.dto.impl;


import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public class PaymentImpl implements Payment {

    private String cardType; //Mastercard, visa, etc.
    private String cardHolderName;
    private String cardNumber; // is 4 last digits will be enough to store ?
    private String cardExpireYear;
    private String cardExpireMonth;
    private String cardIsuueNumber;
    private String cardCvv2Code;
    private Date cardStartDate;

    // when order was created
    private Date orderDate;
    private BigDecimal orderDeliveryAmount;
    private String orderCurrency;
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

    private PaymentAddress shippingAddress;

    private PaymentAddress billingAddress;

    private String billingEmail;


    /**
     * Default constructor.
     */
    public PaymentImpl() {
        orderItems = new ArrayList<PaymentLine>();
        orderDeliveryAmount = BigDecimal.ZERO;
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal getOrderDeliveryAmount() {
        return this.orderDeliveryAmount;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderDeliveryAmount(BigDecimal orderDeliveryAmount) {
        this.orderDeliveryAmount = orderDeliveryAmount;
    }

    /**
     * {@inheritDoc}
     */
    public String getPaymentProcessorResult() {
        return paymentProcessorResult;
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
    public String getBillingEmail() {
        return billingEmail;
    }

    /**
     * {@inheritDoc}
     */
    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }


    /**
     * {@inheritDoc}
     */
    public String getCardType() {
        return cardType;
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
        return cardHolderName;
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
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getCardExpireYear() {
        return cardExpireYear;
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
        return cardExpireMonth;
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
    public String getCardIsuueNumber() {
        return cardIsuueNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardIsuueNumber(final String cardIsuueNumber) {
        this.cardIsuueNumber = cardIsuueNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getCardCvv2Code() {
        return cardCvv2Code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardCvv2Code(final String cardCvv2Code) {
        this.cardCvv2Code = cardCvv2Code;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCardStartDate() {
        return cardStartDate;
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
        return orderDate;
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
    public String getOrderCurrency() {
        return orderCurrency;
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
    public List<PaymentLine> getOrderItems() {
        return orderItems;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrderItems(final List<PaymentLine> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderNumber() {
        return orderNumber;
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
        return orderShipment;
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
        return transactionReferenceId;
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
        return transactionRequestToken;
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
        return transactionAuthorizationCode;
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
    public String getTransactionOperation() {
        return transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperation(String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransactionOperationResultCode() {
        return transactionOperationResultCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperationResultCode(String transactionOperationResultCode) {
        this.transactionOperationResultCode = transactionOperationResultCode;
    }

    public String getTransactionOperationResultMessage() {
        return transactionOperationResultMessage;
    }

    public void setTransactionOperationResultMessage(String transactionOperationResultMessage) {
        this.transactionOperationResultMessage = transactionOperationResultMessage;
    }

    public String getTransactionGatewayLabel() {
        return transactionGatewayLabel;
    }

    public void setTransactionGatewayLabel(String transactionGatewayLabel) {
        this.transactionGatewayLabel = transactionGatewayLabel;
    }

    /**
     * {@inheritDoc}
     */
    public String getShippingAddressString() {
        return shippingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    public void setShippingAddressString(final String shippingAddressString) {
        this.shippingAddressString = shippingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    public String getBillingAddressString() {
        return billingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    public void setBillingAddressString(final String billingAddressString) {
        this.billingAddressString = billingAddressString;
    }

    /**
     * {@inheritDoc}
     */
    public PaymentAddress getShippingAddress() {
        return shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void setShippingAddress(final PaymentAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * {@inheritDoc}
     */
    public PaymentAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * {@inheritDoc}
     */
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
                ", orderAmount=" + orderDeliveryAmount +
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
