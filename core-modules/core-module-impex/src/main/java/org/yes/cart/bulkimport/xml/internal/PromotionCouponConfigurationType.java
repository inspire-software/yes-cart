
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for promotion-coupon-configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="promotion-coupon-configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="max-limit" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="customer-limit" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "promotion-coupon-configurationType")
public class PromotionCouponConfigurationType {

    @XmlAttribute(name = "max-limit", required = true)
    protected int maxLimit;
    @XmlAttribute(name = "customer-limit", required = true)
    protected int customerLimit;

    /**
     * Gets the value of the maxLimit property.
     * 
     */
    public int getMaxLimit() {
        return maxLimit;
    }

    /**
     * Sets the value of the maxLimit property.
     * 
     */
    public void setMaxLimit(int value) {
        this.maxLimit = value;
    }

    /**
     * Gets the value of the customerLimit property.
     * 
     */
    public int getCustomerLimit() {
        return customerLimit;
    }

    /**
     * Sets the value of the customerLimit property.
     * 
     */
    public void setCustomerLimit(int value) {
        this.customerLimit = value;
    }

}
