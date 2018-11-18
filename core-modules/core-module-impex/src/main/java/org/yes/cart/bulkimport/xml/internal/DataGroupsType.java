
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for data-groupsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="data-groupsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data-group" type="{}data-groupType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "data-groupsType", propOrder = {
    "dataGroup"
})
public class DataGroupsType {

    @XmlElement(name = "data-group", required = true)
    protected DataGroupType dataGroup;

    /**
     * Gets the value of the dataGroup property.
     * 
     * @return
     *     possible object is
     *     {@link DataGroupType }
     *     
     */
    public DataGroupType getDataGroup() {
        return dataGroup;
    }

    /**
     * Sets the value of the dataGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataGroupType }
     *     
     */
    public void setDataGroup(DataGroupType value) {
        this.dataGroup = value;
    }

}
