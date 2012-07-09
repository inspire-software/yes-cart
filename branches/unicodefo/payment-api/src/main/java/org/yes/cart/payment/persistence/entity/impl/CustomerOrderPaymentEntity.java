package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
@Entity
@Table(name = "TCUSTOMERORDERPAYMENT")
@SuppressWarnings("PMD.TooManyFields")
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



    /**
     * {@inheritDoc}
     */
    @Column(name = "CARD_TYPE", length = 64)
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
    @Column(name = "CARD_HOLDER_NAME", length = 128)
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
    @Column(name = "CARD_EXPIRY_YEAR", length = 4)
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
    @Column(name = "CARD_EXPIRY_MONTH", length = 2)
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CARD_START_DATE")
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ORDER_DATE", nullable = false)
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
    @Column(name = "ORDER_DELIVERY_AMOUNT", nullable = false)
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
    @Column(name = "ORDER_CURRENCY", nullable = false, length = 3)
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
    @Column(name = "ORDER_NUMBER", nullable = false, length = 128)
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
    @Column(name = "ORDER_SHIPMENT_NUMBER", nullable = false, length = 128)
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
    @Column(name = "TRAN_REFERENCE_ID", length = 128)
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
    @Column(name = "TRAN_REQUEST_TOKEN", length = 256)
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
    @Column(name = "TRAN_AUTH_CODE", length = 256)
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
    @Column(name = "TRAN_PAYMENT_GATEWAY", nullable = false, length = 128)
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
    @Column(name = "TRAN_PAYMENT_OP", nullable = false, length = 128)
    public String getTransactionOperation() {
        return this.transactionOperation;
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionOperation(final String transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    @Column(name = "TRAN_PAYMENT_REZCODE", length = 128)
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
    @Column(name = "TRAN_PAYMENT_REZMSG", length = 1024)
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
    @Column(name = "PP_REZCODE", nullable = false, length = 128)

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
    @Column(name = "PP_BATCH_SETTLEMENT", nullable = false, length = 1)
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
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
    @Column(name = "CREATED_BY", length = 64)
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
    @Column(name = "UPDATED_BY", length = 64)
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
    @Column(name = "GUID", length = 36, nullable = false)
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
    @Id
    @GeneratedValue
    @Column(name = "CUSTOMERORDERPAYMENT_ID", nullable = false)
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
    @Column(name = "CARD_NUMBER", length = 4)
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
    @Transient
    public String getCardIsuueNumber() {
        return cardIsuueNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void setCardIsuueNumber(final String cardIsuueNumber) {
        this.cardIsuueNumber = cardIsuueNumber;
    }


}


