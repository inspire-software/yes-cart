
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navigation-by-price-tiers-currency-tiersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navigation-by-price-tiers-currency-tiersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="price-tier" type="{}navigation-by-price-tiers-currency-tiers-tierType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navigation-by-price-tiers-currency-tiersType", propOrder = {
    "priceTier"
})
public class NavigationByPriceTiersCurrencyTiersType {

    @XmlElement(name = "price-tier", required = true)
    protected List<NavigationByPriceTiersCurrencyTiersTierType> priceTier;

    /**
     * Gets the value of the priceTier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the priceTier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPriceTier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NavigationByPriceTiersCurrencyTiersTierType }
     * 
     * 
     */
    public List<NavigationByPriceTiersCurrencyTiersTierType> getPriceTier() {
        if (priceTier == null) {
            priceTier = new ArrayList<NavigationByPriceTiersCurrencyTiersTierType>();
        }
        return this.priceTier;
    }

}
