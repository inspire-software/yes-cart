
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for promotion-configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="promotion-configurationType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="action-context" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="coupon-triggered" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="can-be-combined" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "promotion-configurationType", propOrder = {
    "value"
})
public class PromotionConfigurationType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "action", required = true)
    protected String action;
    @XmlAttribute(name = "action-context")
    protected String actionContext;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "coupon-triggered", required = true)
    protected boolean couponTriggered;
    @XmlAttribute(name = "can-be-combined", required = true)
    protected boolean canBeCombined;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the actionContext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionContext() {
        return actionContext;
    }

    /**
     * Sets the value of the actionContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionContext(String value) {
        this.actionContext = value;
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
     * Gets the value of the couponTriggered property.
     * 
     */
    public boolean isCouponTriggered() {
        return couponTriggered;
    }

    /**
     * Sets the value of the couponTriggered property.
     * 
     */
    public void setCouponTriggered(boolean value) {
        this.couponTriggered = value;
    }

    /**
     * Gets the value of the canBeCombined property.
     * 
     */
    public boolean isCanBeCombined() {
        return canBeCombined;
    }

    /**
     * Sets the value of the canBeCombined property.
     * 
     */
    public void setCanBeCombined(boolean value) {
        this.canBeCombined = value;
    }

}
