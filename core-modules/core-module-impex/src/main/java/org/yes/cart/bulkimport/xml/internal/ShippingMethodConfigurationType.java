
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shipping-method-configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shipping-method-configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="script" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="min-days" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="max-days" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="guaranteed-delivery" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="named-day-delivery" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="billing-address-not-required" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="delivery-address-not-required" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping-method-configurationType", propOrder = {
    "script"
})
public class ShippingMethodConfigurationType {

    protected String script;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "min-days")
    protected Integer minDays;
    @XmlAttribute(name = "max-days")
    protected Integer maxDays;
    @XmlAttribute(name = "guaranteed-delivery", required = true)
    protected boolean guaranteedDelivery;
    @XmlAttribute(name = "named-day-delivery", required = true)
    protected boolean namedDayDelivery;
    @XmlAttribute(name = "billing-address-not-required", required = true)
    protected boolean billingAddressNotRequired;
    @XmlAttribute(name = "delivery-address-not-required", required = true)
    protected boolean deliveryAddressNotRequired;

    /**
     * Gets the value of the script property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the value of the script property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScript(String value) {
        this.script = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the minDays property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinDays() {
        return minDays;
    }

    /**
     * Sets the value of the minDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinDays(Integer value) {
        this.minDays = value;
    }

    /**
     * Gets the value of the maxDays property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDays() {
        return maxDays;
    }

    /**
     * Sets the value of the maxDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxDays(Integer value) {
        this.maxDays = value;
    }

    /**
     * Gets the value of the guaranteedDelivery property.
     * 
     */
    public boolean isGuaranteedDelivery() {
        return guaranteedDelivery;
    }

    /**
     * Sets the value of the guaranteedDelivery property.
     * 
     */
    public void setGuaranteedDelivery(boolean value) {
        this.guaranteedDelivery = value;
    }

    /**
     * Gets the value of the namedDayDelivery property.
     * 
     */
    public boolean isNamedDayDelivery() {
        return namedDayDelivery;
    }

    /**
     * Sets the value of the namedDayDelivery property.
     * 
     */
    public void setNamedDayDelivery(boolean value) {
        this.namedDayDelivery = value;
    }

    /**
     * Gets the value of the billingAddressNotRequired property.
     * 
     */
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    /**
     * Sets the value of the billingAddressNotRequired property.
     * 
     */
    public void setBillingAddressNotRequired(boolean value) {
        this.billingAddressNotRequired = value;
    }

    /**
     * Gets the value of the deliveryAddressNotRequired property.
     * 
     */
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    /**
     * Sets the value of the deliveryAddressNotRequired property.
     * 
     */
    public void setDeliveryAddressNotRequired(boolean value) {
        this.deliveryAddressNotRequired = value;
    }

}
