
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="navigation-by-attributes" type="{}navigation-by-attributesType" minOccurs="0"/>
 *         &lt;element name="navigation-by-price" type="{}navigation-by-priceType" minOccurs="0"/>
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
    "navigationByAttributes",
    "navigationByPrice"
})
public class NavigationType {

    @XmlElement(name = "navigation-by-attributes")
    protected NavigationByAttributesType navigationByAttributes;
    @XmlElement(name = "navigation-by-price")
    protected NavigationByPriceType navigationByPrice;

    /**
     * Gets the value of the navigationByAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByAttributesType }
     *     
     */
    public NavigationByAttributesType getNavigationByAttributes() {
        return navigationByAttributes;
    }

    /**
     * Sets the value of the navigationByAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByAttributesType }
     *     
     */
    public void setNavigationByAttributes(NavigationByAttributesType value) {
        this.navigationByAttributes = value;
    }

    /**
     * Gets the value of the navigationByPrice property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByPriceType }
     *     
     */
    public NavigationByPriceType getNavigationByPrice() {
        return navigationByPrice;
    }

    /**
     * Sets the value of the navigationByPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByPriceType }
     *     
     */
    public void setNavigationByPrice(NavigationByPriceType value) {
        this.navigationByPrice = value;
    }

}
