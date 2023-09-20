
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fulfilment-centre-configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fulfilment-centre-configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="standard-stock-lead-time" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="backorder-stock-lead-time" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="multiple-shipping-supported" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fulfilment-centre-configurationType", propOrder = {
    "standardStockLeadTime",
    "backorderStockLeadTime",
    "multipleShippingSupported",
    "forceBackorderDeliverySplit",
    "forceAllDeliverySplit"
})
public class FulfilmentCentreConfigurationType {

    @XmlElement(name = "standard-stock-lead-time")
    protected int standardStockLeadTime;
    @XmlElement(name = "backorder-stock-lead-time")
    protected int backorderStockLeadTime;
    @XmlElement(name = "multiple-shipping-supported")
    protected boolean multipleShippingSupported;
    @XmlElement(name = "force-backorder-delivery-split")
    protected boolean forceBackorderDeliverySplit;
    @XmlElement(name = "force-all-delivery-split")
    protected boolean forceAllDeliverySplit;

    /**
     * Gets the value of the standardStockLeadTime property.
     * 
     */
    public int getStandardStockLeadTime() {
        return standardStockLeadTime;
    }

    /**
     * Sets the value of the standardStockLeadTime property.
     * 
     */
    public void setStandardStockLeadTime(int value) {
        this.standardStockLeadTime = value;
    }

    /**
     * Gets the value of the backorderStockLeadTime property.
     * 
     */
    public int getBackorderStockLeadTime() {
        return backorderStockLeadTime;
    }

    /**
     * Sets the value of the backorderStockLeadTime property.
     * 
     */
    public void setBackorderStockLeadTime(int value) {
        this.backorderStockLeadTime = value;
    }

    /**
     * Gets the value of the multipleShippingSupported property.
     * 
     */
    public boolean isMultipleShippingSupported() {
        return multipleShippingSupported;
    }

    /**
     * Sets the value of the multipleShippingSupported property.
     * 
     */
    public void setMultipleShippingSupported(boolean value) {
        this.multipleShippingSupported = value;
    }

    /**
     * Gets the value of the forceBackorderDeliverySplit property.
     *
     */
    public boolean isForceBackorderDeliverySplit() {
        return forceBackorderDeliverySplit;
    }

    /**
     * Sets the value of the forceBackorderDeliverySplit property.
     *
     */
    public void setForceBackorderDeliverySplit(final boolean forceBackorderDeliverySplit) {
        this.forceBackorderDeliverySplit = forceBackorderDeliverySplit;
    }

    /**
     * Gets the value of the forceAllDeliverySplit property.
     *
     */
    public boolean isForceAllDeliverySplit() {
        return forceAllDeliverySplit;
    }

    /**
     * Sets the value of the forceAllDeliverySplit property.
     *
     */
    public void setForceAllDeliverySplit(final boolean forceAllDeliverySplit) {
        this.forceAllDeliverySplit = forceAllDeliverySplit;
    }
}
