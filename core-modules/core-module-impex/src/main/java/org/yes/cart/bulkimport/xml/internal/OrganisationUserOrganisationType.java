
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for organisation-userOrganisationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="organisation-userOrganisationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="company-name-1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="company-name-2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="company-department" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shops" type="{}organisation-userShopsType" minOccurs="0"/>
 *         &lt;element name="roles" type="{}organisation-userRolesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organisation-userOrganisationType", propOrder = {
    "companyName1",
    "companyName2",
    "companyDepartment",
    "shops",
    "roles"
})
public class OrganisationUserOrganisationType {

    @XmlElement(name = "company-name-1")
    protected String companyName1;
    @XmlElement(name = "company-name-2")
    protected String companyName2;
    @XmlElement(name = "company-department")
    protected String companyDepartment;
    protected OrganisationUserShopsType shops;
    protected OrganisationUserRolesType roles;

    /**
     * Gets the value of the companyName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyName1() {
        return companyName1;
    }

    /**
     * Sets the value of the companyName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyName1(String value) {
        this.companyName1 = value;
    }

    /**
     * Gets the value of the companyName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyName2() {
        return companyName2;
    }

    /**
     * Sets the value of the companyName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyName2(String value) {
        this.companyName2 = value;
    }

    /**
     * Gets the value of the companyDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    /**
     * Sets the value of the companyDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyDepartment(String value) {
        this.companyDepartment = value;
    }

    /**
     * Gets the value of the shops property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUserShopsType }
     *     
     */
    public OrganisationUserShopsType getShops() {
        return shops;
    }

    /**
     * Sets the value of the shops property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUserShopsType }
     *     
     */
    public void setShops(OrganisationUserShopsType value) {
        this.shops = value;
    }

    /**
     * Gets the value of the roles property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationUserRolesType }
     *     
     */
    public OrganisationUserRolesType getRoles() {
        return roles;
    }

    /**
     * Sets the value of the roles property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationUserRolesType }
     *     
     */
    public void setRoles(OrganisationUserRolesType value) {
        this.roles = value;
    }

}
