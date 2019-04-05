
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order-itemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-itemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sku" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="product-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fulfilment" type="{}order-item-fulfilmentType"/>
 *         &lt;element name="item-cost" type="{}item-costType" minOccurs="0"/>
 *         &lt;element name="item-price" type="{}item-priceType"/>
 *         &lt;element name="b2b" type="{}order-item-b2bType" minOccurs="0"/>
 *         &lt;element name="custom-attributes" type="{}custom-attributesType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-itemType", propOrder = {
    "sku",
    "productName",
    "fulfilment",
    "itemCost",
    "itemPrice",
    "b2B",
    "customAttributes"
})
public class OrderItemType {

    @XmlElement(required = true)
    protected String sku;
    @XmlElement(name = "product-name", required = true)
    protected String productName;
    @XmlElement(required = true)
    protected OrderItemFulfilmentType fulfilment;
    @XmlElement(name = "item-cost")
    protected ItemCostType itemCost;
    @XmlElement(name = "item-price", required = true)
    protected ItemPriceType itemPrice;
    @XmlElement(name = "b2b")
    protected OrderItemB2BType b2B;
    @XmlElement(name = "custom-attributes")
    protected CustomAttributesType customAttributes;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "guid")
    protected String guid;

    /**
     * Gets the value of the sku property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the value of the sku property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSku(String value) {
        this.sku = value;
    }

    /**
     * Gets the value of the productName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the fulfilment property.
     * 
     * @return
     *     possible object is
     *     {@link OrderItemFulfilmentType }
     *     
     */
    public OrderItemFulfilmentType getFulfilment() {
        return fulfilment;
    }

    /**
     * Sets the value of the fulfilment property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderItemFulfilmentType }
     *     
     */
    public void setFulfilment(OrderItemFulfilmentType value) {
        this.fulfilment = value;
    }

    /**
     * Gets the value of the itemCost property.
     * 
     * @return
     *     possible object is
     *     {@link ItemCostType }
     *     
     */
    public ItemCostType getItemCost() {
        return itemCost;
    }

    /**
     * Sets the value of the itemCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemCostType }
     *     
     */
    public void setItemCost(ItemCostType value) {
        this.itemCost = value;
    }

    /**
     * Gets the value of the itemPrice property.
     * 
     * @return
     *     possible object is
     *     {@link ItemPriceType }
     *     
     */
    public ItemPriceType getItemPrice() {
        return itemPrice;
    }

    /**
     * Sets the value of the itemPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemPriceType }
     *     
     */
    public void setItemPrice(ItemPriceType value) {
        this.itemPrice = value;
    }

    /**
     * Gets the value of the b2B property.
     * 
     * @return
     *     possible object is
     *     {@link OrderItemB2BType }
     *     
     */
    public OrderItemB2BType getB2B() {
        return b2B;
    }

    /**
     * Sets the value of the b2B property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderItemB2BType }
     *     
     */
    public void setB2B(OrderItemB2BType value) {
        this.b2B = value;
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
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

}
