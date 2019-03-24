
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
 * <p>Java class for customer-orderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customer-orderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="configuration" type="{}order-configurationType" minOccurs="0"/>
 *         &lt;element name="order-state" type="{}order-stateType" minOccurs="0"/>
 *         &lt;element name="contact-details" type="{}order-contact-detailsType" minOccurs="0"/>
 *         &lt;element name="amount-due" type="{}order-amount-dueType" minOccurs="0"/>
 *         &lt;element name="b2b" type="{}order-b2bType" minOccurs="0"/>
 *         &lt;element name="order-items" type="{}order-itemsType" minOccurs="0"/>
 *         &lt;element name="shipments" type="{}order-shipmentsType" minOccurs="0"/>
 *         &lt;element name="custom-attributes" type="{}custom-attributesType" minOccurs="0"/>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="order-number" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shop-code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer-orderType", propOrder = {
    "configuration",
    "orderState",
    "contactDetails",
    "amountDue",
    "b2B",
    "orderItems",
    "shipments",
    "customAttributes",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class CustomerOrderType {

    protected OrderConfigurationType configuration;
    @XmlElement(name = "order-state")
    protected OrderStateType orderState;
    @XmlElement(name = "contact-details")
    protected OrderContactDetailsType contactDetails;
    @XmlElement(name = "amount-due")
    protected OrderAmountDueType amountDue;
    @XmlElement(name = "b2b")
    protected OrderB2BType b2B;
    @XmlElement(name = "order-items")
    protected OrderItemsType orderItems;
    protected OrderShipmentsType shipments;
    @XmlElement(name = "custom-attributes")
    protected CustomAttributesType customAttributes;
    @XmlElement(name = "created-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String createdTimestamp;
    @XmlElement(name = "created-by")
    protected String createdBy;
    @XmlElement(name = "updated-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String updatedTimestamp;
    @XmlElement(name = "updated-by")
    protected String updatedBy;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "order-number", required = true)
    protected String orderNumber;
    @XmlAttribute(name = "shop-code", required = true)
    protected String shopCode;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link OrderConfigurationType }
     *     
     */
    public OrderConfigurationType getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderConfigurationType }
     *     
     */
    public void setConfiguration(OrderConfigurationType value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the orderState property.
     * 
     * @return
     *     possible object is
     *     {@link OrderStateType }
     *     
     */
    public OrderStateType getOrderState() {
        return orderState;
    }

    /**
     * Sets the value of the orderState property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderStateType }
     *     
     */
    public void setOrderState(OrderStateType value) {
        this.orderState = value;
    }

    /**
     * Gets the value of the contactDetails property.
     * 
     * @return
     *     possible object is
     *     {@link OrderContactDetailsType }
     *     
     */
    public OrderContactDetailsType getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the value of the contactDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderContactDetailsType }
     *     
     */
    public void setContactDetails(OrderContactDetailsType value) {
        this.contactDetails = value;
    }

    /**
     * Gets the value of the amountDue property.
     * 
     * @return
     *     possible object is
     *     {@link OrderAmountDueType }
     *     
     */
    public OrderAmountDueType getAmountDue() {
        return amountDue;
    }

    /**
     * Sets the value of the amountDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderAmountDueType }
     *     
     */
    public void setAmountDue(OrderAmountDueType value) {
        this.amountDue = value;
    }

    /**
     * Gets the value of the b2B property.
     * 
     * @return
     *     possible object is
     *     {@link OrderB2BType }
     *     
     */
    public OrderB2BType getB2B() {
        return b2B;
    }

    /**
     * Sets the value of the b2B property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderB2BType }
     *     
     */
    public void setB2B(OrderB2BType value) {
        this.b2B = value;
    }

    /**
     * Gets the value of the orderItems property.
     * 
     * @return
     *     possible object is
     *     {@link OrderItemsType }
     *     
     */
    public OrderItemsType getOrderItems() {
        return orderItems;
    }

    /**
     * Sets the value of the orderItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderItemsType }
     *     
     */
    public void setOrderItems(OrderItemsType value) {
        this.orderItems = value;
    }

    /**
     * Gets the value of the shipments property.
     * 
     * @return
     *     possible object is
     *     {@link OrderShipmentsType }
     *     
     */
    public OrderShipmentsType getShipments() {
        return shipments;
    }

    /**
     * Sets the value of the shipments property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderShipmentsType }
     *     
     */
    public void setShipments(OrderShipmentsType value) {
        this.shipments = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link CustomAttributesType }
     *     
     */
    public CustomAttributesType getCustomAttributes() {
        return customAttributes;
    }

    /**
     * Sets the value of the customAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomAttributesType }
     *     
     */
    public void setCustomAttributes(CustomAttributesType value) {
        this.customAttributes = value;
    }

    /**
     * Gets the value of the createdTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the value of the createdTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedTimestamp(String value) {
        this.createdTimestamp = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the updatedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /**
     * Sets the value of the updatedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedTimestamp(String value) {
        this.updatedTimestamp = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
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
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    /**
     * Gets the value of the shopCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * Sets the value of the shopCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShopCode(String value) {
        this.shopCode = value;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityImportModeType }
     *     
     */
    public EntityImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityImportModeType }
     *     
     */
    public void setImportMode(EntityImportModeType value) {
        this.importMode = value;
    }

}
