
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navigationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navigationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attributes" type="{}navigation-by-attributesType" minOccurs="0"/>
 *         &lt;element name="price" type="{}navigation-by-priceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navigationType", propOrder = {
    "attributes",
    "price"
})
public class NavigationType {

    protected NavigationByAttributesType attributes;
    protected NavigationByPriceType price;

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByAttributesType }
     *     
     */
    public NavigationByAttributesType getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByAttributesType }
     *     
     */
    public void setAttributes(NavigationByAttributesType value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByPriceType }
     *     
     */
    public NavigationByPriceType getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByPriceType }
     *     
     */
    public void setPrice(NavigationByPriceType value) {
        this.price = value;
    }

}
