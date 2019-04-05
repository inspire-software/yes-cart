
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for data-descriptorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="data-descriptorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data-descriptor" type="{}data-descriptorType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "data-descriptorsType", propOrder = {
    "dataDescriptor"
})
public class DataDescriptorsType {

    @XmlElement(name = "data-descriptor", required = true)
    protected DataDescriptorType dataDescriptor;

    /**
     * Gets the value of the dataDescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link DataDescriptorType }
     *     
     */
    public DataDescriptorType getDataDescriptor() {
        return dataDescriptor;
    }

    /**
     * Sets the value of the dataDescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataDescriptorType }
     *     
     */
    public void setDataDescriptor(DataDescriptorType value) {
        this.dataDescriptor = value;
    }

}
