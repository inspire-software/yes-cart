
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for delivery-item-fulfilmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="delivery-item-fulfilmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ordered-quantity" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="delivered-quantity" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="supplier-code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="delivery-estimated-min" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-estimated-max" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-guaranteed" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-confirmed" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="invoice-number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invoice-date" type="{}dateType" minOccurs="0"/>
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delivery-item-fulfilmentType", propOrder = {
    "orderedQuantity",
    "deliveredQuantity",
    "supplierCode",
    "deliveryEstimatedMin",
    "deliveryEstimatedMax",
    "deliveryGuaranteed",
    "deliveryConfirmed",
    "invoiceNumber",
    "invoiceDate",
    "remarks"
})
public class DeliveryItemFulfilmentType {

    @XmlElement(name = "ordered-quantity", required = true)
    protected BigDecimal orderedQuantity;
    @XmlElement(name = "delivered-quantity")
    protected BigDecimal deliveredQuantity;
    @XmlElement(name = "supplier-code", required = true)
    protected String supplierCode;
    @XmlElement(name = "delivery-estimated-min")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String deliveryEstimatedMin;
    @XmlElement(name = "delivery-estimated-max")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String deliveryEstimatedMax;
    @XmlElement(name = "delivery-guaranteed")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String deliveryGuaranteed;
    @XmlElement(name = "delivery-confirmed")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String deliveryConfirmed;
    @XmlElement(name = "invoice-number")
    protected String invoiceNumber;
    @XmlElement(name = "invoice-date")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String invoiceDate;
    protected String remarks;

    /**
     * Gets the value of the orderedQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOrderedQuantity() {
        return orderedQuantity;
    }

    /**
     * Sets the value of the orderedQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOrderedQuantity(BigDecimal value) {
        this.orderedQuantity = value;
    }

    /**
     * Gets the value of the deliveredQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDeliveredQuantity() {
        return deliveredQuantity;
    }

    /**
     * Sets the value of the deliveredQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDeliveredQuantity(BigDecimal value) {
        this.deliveredQuantity = value;
    }

    /**
     * Gets the value of the supplierCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * Sets the value of the supplierCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierCode(String value) {
        this.supplierCode = value;
    }

    /**
     * Gets the value of the deliveryEstimatedMin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    /**
     * Sets the value of the deliveryEstimatedMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryEstimatedMin(String value) {
        this.deliveryEstimatedMin = value;
    }

    /**
     * Gets the value of the deliveryEstimatedMax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    /**
     * Sets the value of the deliveryEstimatedMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryEstimatedMax(String value) {
        this.deliveryEstimatedMax = value;
    }

    /**
     * Gets the value of the deliveryGuaranteed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    /**
     * Sets the value of the deliveryGuaranteed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryGuaranteed(String value) {
        this.deliveryGuaranteed = value;
    }

    /**
     * Gets the value of the deliveryConfirmed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryConfirmed() {
        return deliveryConfirmed;
    }

    /**
     * Sets the value of the deliveryConfirmed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryConfirmed(String value) {
        this.deliveryConfirmed = value;
    }

    /**
     * Gets the value of the invoiceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the value of the invoiceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceNumber(String value) {
        this.invoiceNumber = value;
    }

    /**
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceDate(String value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the remarks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the value of the remarks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemarks(String value) {
        this.remarks = value;
    }

}
