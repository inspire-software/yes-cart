
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for item-promotionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="item-promotionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="promotions" type="{}order-promotionsCodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="applied" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="gift" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="fixed-price" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "item-promotionsType", propOrder = {
    "promotions"
})
public class ItemPromotionsType {

    protected OrderPromotionsCodeType promotions;
    @XmlAttribute(name = "applied", required = true)
    protected boolean applied;
    @XmlAttribute(name = "gift", required = true)
    protected boolean gift;
    @XmlAttribute(name = "fixed-price", required = true)
    protected boolean fixedPrice;

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

    /**
     * Gets the value of the gift property.
     * 
     */
    public boolean isGift() {
        return gift;
    }

    /**
     * Sets the value of the gift property.
     * 
     */
    public void setGift(boolean value) {
        this.gift = value;
    }

    /**
     * Gets the value of the fixedPrice property.
     * 
     */
    public boolean isFixedPrice() {
        return fixedPrice;
    }

    /**
     * Sets the value of the fixedPrice property.
     * 
     */
    public void setFixedPrice(boolean value) {
        this.fixedPrice = value;
    }

}
