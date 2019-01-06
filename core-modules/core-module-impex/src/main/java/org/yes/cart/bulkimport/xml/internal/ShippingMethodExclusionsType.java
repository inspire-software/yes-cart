
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shipping-method-exclusionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shipping-method-exclusionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customer-types" type="{}shipping-method-exclusions-customer-typesType" minOccurs="0"/>
 *         &lt;element name="weekdays" type="{}shipping-method-exclusions-weekdaysType" minOccurs="0"/>
 *         &lt;element name="dates" type="{}shipping-method-exclusions-datesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping-method-exclusionsType", propOrder = {
    "customerTypes",
    "weekdays",
    "dates"
})
public class ShippingMethodExclusionsType {

    @XmlElement(name = "customer-types")
    protected ShippingMethodExclusionsCustomerTypesType customerTypes;
    protected ShippingMethodExclusionsWeekdaysType weekdays;
    protected ShippingMethodExclusionsDatesType dates;

    /**
     * Gets the value of the customerTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingMethodExclusionsCustomerTypesType }
     *     
     */
    public ShippingMethodExclusionsCustomerTypesType getCustomerTypes() {
        return customerTypes;
    }

    /**
     * Sets the value of the customerTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingMethodExclusionsCustomerTypesType }
     *     
     */
    public void setCustomerTypes(ShippingMethodExclusionsCustomerTypesType value) {
        this.customerTypes = value;
    }

    /**
     * Gets the value of the weekdays property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingMethodExclusionsWeekdaysType }
     *     
     */
    public ShippingMethodExclusionsWeekdaysType getWeekdays() {
        return weekdays;
    }

    /**
     * Sets the value of the weekdays property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingMethodExclusionsWeekdaysType }
     *     
     */
    public void setWeekdays(ShippingMethodExclusionsWeekdaysType value) {
        this.weekdays = value;
    }

    /**
     * Gets the value of the dates property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingMethodExclusionsDatesType }
     *     
     */
    public ShippingMethodExclusionsDatesType getDates() {
        return dates;
    }

    /**
     * Sets the value of the dates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingMethodExclusionsDatesType }
     *     
     */
    public void setDates(ShippingMethodExclusionsDatesType value) {
        this.dates = value;
    }

}
