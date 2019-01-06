
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navigation-by-price-tiersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navigation-by-price-tiersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="currencies">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="currency" type="{}navigation-by-price-tiers-currencyType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navigation-by-price-tiersType", propOrder = {
    "currencies"
})
public class NavigationByPriceTiersType {

    @XmlElement(required = true)
    protected NavigationByPriceTiersType.Currencies currencies;

    /**
     * Gets the value of the currencies property.
     * 
     * @return
     *     possible object is
     *     {@link NavigationByPriceTiersType.Currencies }
     *     
     */
    public NavigationByPriceTiersType.Currencies getCurrencies() {
        return currencies;
    }

    /**
     * Sets the value of the currencies property.
     * 
     * @param value
     *     allowed object is
     *     {@link NavigationByPriceTiersType.Currencies }
     *     
     */
    public void setCurrencies(NavigationByPriceTiersType.Currencies value) {
        this.currencies = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="currency" type="{}navigation-by-price-tiers-currencyType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "currency"
    })
    public static class Currencies {

        @XmlElement(required = true)
        protected List<NavigationByPriceTiersCurrencyType> currency;

        /**
         * Gets the value of the currency property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the currency property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCurrency().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NavigationByPriceTiersCurrencyType }
         * 
         * 
         */
        public List<NavigationByPriceTiersCurrencyType> getCurrency() {
            if (currency == null) {
                currency = new ArrayList<NavigationByPriceTiersCurrencyType>();
            }
            return this.currency;
        }

    }

}
