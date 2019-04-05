
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shipping-method-supportedType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shipping-method-supportedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fulfilment-centres" type="{}shipping-method-supported-fulfilment-centresType" minOccurs="0"/>
 *         &lt;element name="payment-gateways" type="{}shipping-method-supported-payment-gatewaysType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping-method-supportedType", propOrder = {
    "fulfilmentCentres",
    "paymentGateways"
})
public class ShippingMethodSupportedType {

    @XmlElement(name = "fulfilment-centres")
    protected ShippingMethodSupportedFulfilmentCentresType fulfilmentCentres;
    @XmlElement(name = "payment-gateways")
    protected ShippingMethodSupportedPaymentGatewaysType paymentGateways;

    /**
     * Gets the value of the fulfilmentCentres property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingMethodSupportedFulfilmentCentresType }
     *     
     */
    public ShippingMethodSupportedFulfilmentCentresType getFulfilmentCentres() {
        return fulfilmentCentres;
    }

    /**
     * Sets the value of the fulfilmentCentres property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingMethodSupportedFulfilmentCentresType }
     *     
     */
    public void setFulfilmentCentres(ShippingMethodSupportedFulfilmentCentresType value) {
        this.fulfilmentCentres = value;
    }

    /**
     * Gets the value of the paymentGateways property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingMethodSupportedPaymentGatewaysType }
     *     
     */
    public ShippingMethodSupportedPaymentGatewaysType getPaymentGateways() {
        return paymentGateways;
    }

    /**
     * Sets the value of the paymentGateways property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingMethodSupportedPaymentGatewaysType }
     *     
     */
    public void setPaymentGateways(ShippingMethodSupportedPaymentGatewaysType value) {
        this.paymentGateways = value;
    }

}
