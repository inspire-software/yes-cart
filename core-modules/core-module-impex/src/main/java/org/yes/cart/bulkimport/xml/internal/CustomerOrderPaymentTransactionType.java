
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customer-order-payment-transactionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customer-order-payment-transactionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="request-token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="auth-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operation-result-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operation-result-message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payment-processor-result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batch-settlement" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer-order-payment-transactionType", propOrder = {
    "reference",
    "requestToken",
    "authCode",
    "operation",
    "operationResultCode",
    "operationResultMessage",
    "paymentProcessorResult",
    "batchSettlement"
})
public class CustomerOrderPaymentTransactionType {

    @XmlElement(required = true)
    protected String reference;
    @XmlElement(name = "request-token")
    protected String requestToken;
    @XmlElement(name = "auth-code")
    protected String authCode;
    @XmlElement(required = true)
    protected String operation;
    @XmlElement(name = "operation-result-code")
    protected String operationResultCode;
    @XmlElement(name = "operation-result-message")
    protected String operationResultMessage;
    @XmlElement(name = "payment-processor-result")
    protected String paymentProcessorResult;
    @XmlElement(name = "batch-settlement")
    protected boolean batchSettlement;

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the requestToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestToken() {
        return requestToken;
    }

    /**
     * Sets the value of the requestToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestToken(String value) {
        this.requestToken = value;
    }

    /**
     * Gets the value of the authCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * Sets the value of the authCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthCode(String value) {
        this.authCode = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperation(String value) {
        this.operation = value;
    }

    /**
     * Gets the value of the operationResultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationResultCode() {
        return operationResultCode;
    }

    /**
     * Sets the value of the operationResultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationResultCode(String value) {
        this.operationResultCode = value;
    }

    /**
     * Gets the value of the operationResultMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationResultMessage() {
        return operationResultMessage;
    }

    /**
     * Sets the value of the operationResultMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationResultMessage(String value) {
        this.operationResultMessage = value;
    }

    /**
     * Gets the value of the paymentProcessorResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentProcessorResult() {
        return paymentProcessorResult;
    }

    /**
     * Sets the value of the paymentProcessorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentProcessorResult(String value) {
        this.paymentProcessorResult = value;
    }

    /**
     * Gets the value of the batchSettlement property.
     * 
     */
    public boolean isBatchSettlement() {
        return batchSettlement;
    }

    /**
     * Sets the value of the batchSettlement property.
     * 
     */
    public void setBatchSettlement(boolean value) {
        this.batchSettlement = value;
    }

}
