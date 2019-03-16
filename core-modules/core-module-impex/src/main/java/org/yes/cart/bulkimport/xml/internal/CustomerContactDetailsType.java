
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customer-contact-detailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customer-contact-detailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="salutation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="middlename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer-contact-detailsType", propOrder = {
    "salutation",
    "firstname",
    "middlename",
    "lastname"
})
public class CustomerContactDetailsType {

    protected String salutation;
    @XmlElement(required = true)
    protected String firstname;
    protected String middlename;
    @XmlElement(required = true)
    protected String lastname;

    /**
     * Gets the value of the salutation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalutation() {
        return salutation;
    }

    /**
     * Sets the value of the salutation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalutation(String value) {
        this.salutation = value;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the middlename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * Sets the value of the middlename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddlename(String value) {
        this.middlename = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastname(String value) {
        this.lastname = value;
    }

}
