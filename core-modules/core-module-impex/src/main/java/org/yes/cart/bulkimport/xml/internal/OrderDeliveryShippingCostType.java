
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order-delivery-shipping-costType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-delivery-shipping-costType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list-price" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="sale-price" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price-net" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price-gross" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="tax" type="{}order-taxType"/>
 *         &lt;element name="shipping-promotions" type="{}order-delivery-shipping-promotionsType"/>
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
@XmlType(name = "order-delivery-shipping-costType", propOrder = {
    "listPrice",
    "salePrice",
    "price",
    "priceNet",
    "priceGross",
    "tax",
    "shippingPromotions"
})
public class OrderDeliveryShippingCostType {

    @XmlElement(name = "list-price", required = true)
    protected BigDecimal listPrice;
    @XmlElement(name = "sale-price", required = true)
    protected BigDecimal salePrice;
    @XmlElement(required = true)
    protected BigDecimal price;
    @XmlElement(name = "price-net", required = true)
    protected BigDecimal priceNet;
    @XmlElement(name = "price-gross", required = true)
    protected BigDecimal priceGross;
    @XmlElement(required = true)
    protected OrderTaxType tax;
    @XmlElement(name = "shipping-promotions", required = true)
    protected OrderDeliveryShippingPromotionsType shippingPromotions;
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
     * Gets the value of the salePrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /**
     * Sets the value of the salePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSalePrice(BigDecimal value) {
        this.salePrice = value;
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
     * Gets the value of the tax property.
     * 
     * @return
     *     possible object is
     *     {@link OrderTaxType }
     *     
     */
    public OrderTaxType getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderTaxType }
     *     
     */
    public void setTax(OrderTaxType value) {
        this.tax = value;
    }

    /**
     * Gets the value of the shippingPromotions property.
     * 
     * @return
     *     possible object is
     *     {@link OrderDeliveryShippingPromotionsType }
     *     
     */
    public OrderDeliveryShippingPromotionsType getShippingPromotions() {
        return shippingPromotions;
    }

    /**
     * Sets the value of the shippingPromotions property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderDeliveryShippingPromotionsType }
     *     
     */
    public void setShippingPromotions(OrderDeliveryShippingPromotionsType value) {
        this.shippingPromotions = value;
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
