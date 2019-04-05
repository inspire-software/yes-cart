
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for i18nsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="i18nsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="i18n" type="{}i18nType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="import-mode" type="{}i18nImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "i18nsType", propOrder = {
    "i18N"
})
public class I18NsType {

    @XmlElement(name = "i18n")
    protected List<I18NType> i18N;
    @XmlAttribute(name = "import-mode")
    protected I18NImportModeType importMode;

    /**
     * Gets the value of the i18N property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the i18N property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getI18N().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I18NType }
     * 
     * 
     */
    public List<I18NType> getI18N() {
        if (i18N == null) {
            i18N = new ArrayList<I18NType>();
        }
        return this.i18N;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link I18NImportModeType }
     *     
     */
    public I18NImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NImportModeType }
     *     
     */
    public void setImportMode(I18NImportModeType value) {
        this.importMode = value;
    }

}
