
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order-delivery-shipping-promotionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-delivery-shipping-promotionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="promotions" type="{}order-promotionsCodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applied" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-delivery-shipping-promotionsType", propOrder = {
    "promotions"
})
public class OrderDeliveryShippingPromotionsType {

    protected OrderPromotionsCodeType promotions;
    @XmlAttribute(name = "applied", required = true)
    protected boolean applied;

    /**
     * Gets the value of the promotions property.
     * 
     * @return
     *     possible object is
     *     {@link OrderPromotionsCodeType }
     *     
     */
    public OrderPromotionsCodeType getPromotions() {
        return promotions;
    }

    /**
     * Sets the value of the promotions property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderPromotionsCodeType }
     *     
     */
    public void setPromotions(OrderPromotionsCodeType value) {
        this.promotions = value;
    }

    /**
     * Gets the value of the applied property.
     * 
     */
    public boolean isApplied() {
        return applied;
    }

    /**
     * Sets the value of the applied property.
     * 
     */
    public void setApplied(boolean value) {
        this.applied = value;
    }

}
