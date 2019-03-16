
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customer-credentialsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customer-credentialsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="guest-email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password-expiry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="auth-token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="auth-token-expiry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="public-key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="guest" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer-credentialsType", propOrder = {
    "email",
    "guestEmail",
    "password",
    "passwordExpiry",
    "authToken",
    "authTokenExpiry",
    "publicKey"
})
public class CustomerCredentialsType {

    @XmlElement(required = true)
    protected String email;
    @XmlElement(name = "guest-email")
    protected String guestEmail;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(name = "password-expiry")
    protected String passwordExpiry;
    @XmlElement(name = "auth-token")
    protected String authToken;
    @XmlElement(name = "auth-token-expiry")
    protected String authTokenExpiry;
    @XmlElement(name = "public-key")
    protected String publicKey;
    @XmlAttribute(name = "guest", required = true)
    protected boolean guest;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the guestEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuestEmail() {
        return guestEmail;
    }

    /**
     * Sets the value of the guestEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuestEmail(String value) {
        this.guestEmail = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the passwordExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordExpiry() {
        return passwordExpiry;
    }

    /**
     * Sets the value of the passwordExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordExpiry(String value) {
        this.passwordExpiry = value;
    }

    /**
     * Gets the value of the authToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the value of the authToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthToken(String value) {
        this.authToken = value;
    }

    /**
     * Gets the value of the authTokenExpiry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthTokenExpiry() {
        return authTokenExpiry;
    }

    /**
     * Sets the value of the authTokenExpiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthTokenExpiry(String value) {
        this.authTokenExpiry = value;
    }

    /**
     * Gets the value of the publicKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Sets the value of the publicKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublicKey(String value) {
        this.publicKey = value;
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
