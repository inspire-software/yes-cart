
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navigation-by-price-tiers-currencyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navigation-by-price-tiers-currencyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="price-tiers" type="{}navigation-by-price-tiers-currency-tiersType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navigation-by-price-tiers-currencyType", propOrder = {
    "name",
    "priceTiers"
})
public class NavigationByPriceTiersCurrencyType {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "price-tiers", required = true)
    protected NavigationByPriceTiersCurrencyTiersType priceTiers;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the priceTiers property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByPriceTiersCurrencyTiersType }
     *     
     */
    public NavigationByPriceTiersCurrencyTiersType getPriceTiers() {
        return priceTiers;
    }

    /**
     * Sets the value of the priceTiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByPriceTiersCurrencyTiersType }
     *     
     */
    public void setPriceTiers(NavigationByPriceTiersCurrencyTiersType value) {
        this.priceTiers = value;
    }

}
