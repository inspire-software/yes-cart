
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for order-shipmentsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-shipmentsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requested-delivery-date" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="deliveries" type="{}order-deliveriesType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="multiple-shipments" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-shipmentsType", propOrder = {
    "requestedDeliveryDate",
    "deliveries"
})
public class OrderShipmentsType {

    @XmlElement(name = "requested-delivery-date")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String requestedDeliveryDate;
    protected OrderDeliveriesType deliveries;
    @XmlAttribute(name = "multiple-shipments", required = true)
    protected boolean multipleShipments;

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
     * Gets the value of the deliveries property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveriesType }
     *     
     */
    public OrderDeliveriesType getDeliveries() {
        return deliveries;
    }

    /**
     * Sets the value of the deliveries property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveriesType }
     *     
     */
    public void setDeliveries(OrderDeliveriesType value) {
        this.deliveries = value;
    }

    /**
     * Gets the value of the multipleShipments property.
     * 
     */
    public boolean isMultipleShipments() {
        return multipleShipments;
    }

    /**
     * Sets the value of the multipleShipments property.
     * 
     */
    public void setMultipleShipments(boolean value) {
        this.multipleShipments = value;
    }

}
