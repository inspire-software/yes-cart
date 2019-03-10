
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for organisation-userPreferencesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="organisation-userPreferencesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dashboard-widgets" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organisation-userPreferencesType", propOrder = {
    "dashboardWidgets"
})
public class OrganisationUserPreferencesType {

    @XmlElement(name = "dashboard-widgets", required = true)
    protected String dashboardWidgets;

    /**
     * Gets the value of the dashboardWidgets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDashboardWidgets() {
        return dashboardWidgets;
    }

    /**
     * Sets the value of the dashboardWidgets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDashboardWidgets(String value) {
        this.dashboardWidgets = value;
    }

}
