
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for order-configurationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-configurationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payment-gateway" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cart-guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="order-date" type="{}dateTimeType"/>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="guest" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-configurationType", propOrder = {
    "paymentGateway",
    "cartGuid",
    "orderDate",
    "locale",
    "ip",
    "guest"
})
public class OrderConfigurationType {

    @XmlElement(name = "payment-gateway", required = true)
    protected String paymentGateway;
    @XmlElement(name = "cart-guid", required = true)
    protected String cartGuid;
    @XmlElement(name = "order-date", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String orderDate;
    @XmlElement(required = true)
    protected String locale;
    protected String ip;
    protected boolean guest;

    /**
     * Gets the value of the paymentGateway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentGateway() {
        return paymentGateway;
    }

    /**
     * Sets the value of the paymentGateway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentGateway(String value) {
        this.paymentGateway = value;
    }

    /**
     * Gets the value of the cartGuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCartGuid() {
        return cartGuid;
    }

    /**
     * Sets the value of the cartGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCartGuid(String value) {
        this.cartGuid = value;
    }

    /**
     * Gets the value of the orderDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderDate(String value) {
        this.orderDate = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIp(String value) {
        this.ip = value;
    }

    /**
     * Gets the value of the guest property.
     * 
     */
    public boolean isGuest() {
        return guest;
    }

    /**
     * Sets the value of the guest property.
     * 
     */
    public void setGuest(boolean value) {
        this.guest = value;
    }

}
