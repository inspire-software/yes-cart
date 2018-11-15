
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for custom-attributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="custom-attributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="custom-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="custom-display-value" type="{}i18nsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="attribute" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custom-attributeType", propOrder = {
    "customValue",
    "customDisplayValue"
})
public class CustomAttributeType {

    @XmlElement(name = "custom-value", required = true)
    protected String customValue;
    @XmlElement(name = "custom-display-value")
    protected I18NsType customDisplayValue;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "attribute", required = true)
    protected String attribute;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the customValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomValue() {
        return customValue;
    }

    /**
     * Sets the value of the customValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomValue(String value) {
        this.customValue = value;
    }

    /**
     * Gets the value of the customDisplayValue property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getCustomDisplayValue() {
        return customDisplayValue;
    }

    /**
     * Sets the value of the customDisplayValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setCustomDisplayValue(I18NsType value) {
        this.customDisplayValue = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Sets the value of the attribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute(String value) {
        this.attribute = value;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityImportModeType }
     *     
     */
    public EntityImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityImportModeType }
     *     
     */
    public void setImportMode(EntityImportModeType value) {
        this.importMode = value;
    }

}
