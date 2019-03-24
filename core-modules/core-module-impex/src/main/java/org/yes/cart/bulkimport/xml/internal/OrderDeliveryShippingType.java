
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for order-delivery-shippingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-delivery-shippingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shipping-method" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tracking-reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requested-delivery-date" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-estimated-min" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-estimated-max" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-guaranteed" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="delivery-confirmed" type="{}dateTimeType" minOccurs="0"/>
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
@XmlType(name = "order-delivery-shippingType", propOrder = {
    "shippingMethod",
    "trackingReference",
    "requestedDeliveryDate",
    "deliveryEstimatedMin",
    "deliveryEstimatedMax",
    "deliveryGuaranteed",
    "deliveryConfirmed",
    "remarks"
})
public class OrderDeliveryShippingType {

    @XmlElement(name = "shipping-method", required = true)
    protected String shippingMethod;
    @XmlElement(name = "tracking-reference")
    protected String trackingReference;
    @XmlElement(name = "requested-delivery-date")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String requestedDeliveryDate;
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
    protected String remarks;

    /**
     * Gets the value of the shippingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingMethod() {
        return shippingMethod;
    }

    /**
     * Sets the value of the shippingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingMethod(String value) {
        this.shippingMethod = value;
    }

    /**
     * Gets the value of the trackingReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackingReference() {
        return trackingReference;
    }

    /**
     * Sets the value of the trackingReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackingReference(String value) {
        this.trackingReference = value;
    }

    /**
     * Gets the value of the requestedDeliveryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    /**
     * Sets the value of the requestedDeliveryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedDeliveryDate(String value) {
        this.requestedDeliveryDate = value;
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
