
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order-amount-dueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-amount-dueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list-price" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price-net" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price-gross" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="order-promotions" type="{}order-order-promotionsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="currency" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-amount-dueType", propOrder = {
    "listPrice",
    "price",
    "priceNet",
    "priceGross",
    "orderPromotions"
})
public class OrderAmountDueType {

    @XmlElement(name = "list-price", required = true)
    protected BigDecimal listPrice;
    @XmlElement(required = true)
    protected BigDecimal price;
    @XmlElement(name = "price-net", required = true)
    protected BigDecimal priceNet;
    @XmlElement(name = "price-gross", required = true)
    protected BigDecimal priceGross;
    @XmlElement(name = "order-promotions", required = true)
    protected OrderOrderPromotionsType orderPromotions;
    @XmlAttribute(name = "currency", required = true)
    protected String currency;

    /**
     * Gets the value of the listPrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /**
     * Sets the value of the listPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setListPrice(BigDecimal value) {
        this.listPrice = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPrice(BigDecimal value) {
        this.price = value;
    }

    /**
     * Gets the value of the priceNet property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPriceNet() {
        return priceNet;
    }

    /**
     * Sets the value of the priceNet property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPriceNet(BigDecimal value) {
        this.priceNet = value;
    }

    /**
     * Gets the value of the priceGross property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPriceGross() {
        return priceGross;
    }

    /**
     * Sets the value of the priceGross property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPriceGross(BigDecimal value) {
        this.priceGross = value;
    }

    /**
     * Gets the value of the orderPromotions property.
     * 
     * @return
     *     possible object is
     *     {@link OrderOrderPromotionsType }
     *     
     */
    public OrderOrderPromotionsType getOrderPromotions() {
        return orderPromotions;
    }

    /**
     * Sets the value of the orderPromotions property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderOrderPromotionsType }
     *     
     */
    public void setOrderPromotions(OrderOrderPromotionsType value) {
        this.orderPromotions = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

}
