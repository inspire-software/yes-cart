
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order-deliveryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-deliveryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shipping" type="{}order-delivery-shippingType"/>
 *         &lt;element name="shipping-cost" type="{}order-delivery-shipping-costType"/>
 *         &lt;element name="delivery-state" type="{}order-delivery-stateType"/>
 *         &lt;element name="delivery-items" type="{}order-delivery-itemsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delivery-number" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="delivery-group" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-deliveryType", propOrder = {
    "shipping",
    "shippingCost",
    "deliveryState",
    "deliveryItems"
})
public class OrderDeliveryType {

    @XmlElement(required = true)
    protected OrderDeliveryShippingType shipping;
    @XmlElement(name = "shipping-cost", required = true)
    protected OrderDeliveryShippingCostType shippingCost;
    @XmlElement(name = "delivery-state", required = true)
    protected OrderDeliveryStateType deliveryState;
    @XmlElement(name = "delivery-items", required = true)
    protected OrderDeliveryItemsType deliveryItems;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "delivery-number", required = true)
    protected String deliveryNumber;
    @XmlAttribute(name = "delivery-group", required = true)
    protected String deliveryGroup;

    /**
     * Gets the value of the shipping property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveryShippingType }
     *     
     */
    public OrderDeliveryShippingType getShipping() {
        return shipping;
    }

    /**
     * Sets the value of the shipping property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveryShippingType }
     *     
     */
    public void setShipping(OrderDeliveryShippingType value) {
        this.shipping = value;
    }

    /**
     * Gets the value of the shippingCost property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveryShippingCostType }
     *     
     */
    public OrderDeliveryShippingCostType getShippingCost() {
        return shippingCost;
    }

    /**
     * Sets the value of the shippingCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveryShippingCostType }
     *     
     */
    public void setShippingCost(OrderDeliveryShippingCostType value) {
        this.shippingCost = value;
    }

    /**
     * Gets the value of the deliveryState property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveryStateType }
     *     
     */
    public OrderDeliveryStateType getDeliveryState() {
        return deliveryState;
    }

    /**
     * Sets the value of the deliveryState property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveryStateType }
     *     
     */
    public void setDeliveryState(OrderDeliveryStateType value) {
        this.deliveryState = value;
    }

    /**
     * Gets the value of the deliveryItems property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveryItemsType }
     *     
     */
    public OrderDeliveryItemsType getDeliveryItems() {
        return deliveryItems;
    }

    /**
     * Sets the value of the deliveryItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveryItemsType }
     *     
     */
    public void setDeliveryItems(OrderDeliveryItemsType value) {
        this.deliveryItems = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the deliveryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    /**
     * Sets the value of the deliveryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryNumber(String value) {
        this.deliveryNumber = value;
    }

    /**
     * Gets the value of the deliveryGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    /**
     * Sets the value of the deliveryGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryGroup(String value) {
        this.deliveryGroup = value;
    }

}
