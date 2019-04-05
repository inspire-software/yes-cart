
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for seoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="seoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta-title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta-title-display" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="meta-keywords" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta-keywords-display" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="meta-description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meta-description-display" type="{}i18nsType" minOccurs="0"/>
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
@XmlType(name = "seoType", propOrder = {
    "uri",
    "metaTitle",
    "metaTitleDisplay",
    "metaKeywords",
    "metaKeywordsDisplay",
    "metaDescription",
    "metaDescriptionDisplay"
})
public class SeoType {

    protected String uri;
    @XmlElement(name = "meta-title")
    protected String metaTitle;
    @XmlElement(name = "meta-title-display")
    protected I18NsType metaTitleDisplay;
    @XmlElement(name = "meta-keywords")
    protected String metaKeywords;
    @XmlElement(name = "meta-keywords-display")
    protected I18NsType metaKeywordsDisplay;
    @XmlElement(name = "meta-description")
    protected String metaDescription;
    @XmlElement(name = "meta-description-display")
    protected I18NsType metaDescriptionDisplay;
    @XmlAttribute(name = "import-mode")
    protected I18NImportModeType importMode;

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the metaTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaTitle() {
        return metaTitle;
    }

    /**
     * Sets the value of the metaTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaTitle(String value) {
        this.metaTitle = value;
    }

    /**
     * Gets the value of the metaTitleDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getMetaTitleDisplay() {
        return metaTitleDisplay;
    }

    /**
     * Sets the value of the metaTitleDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setMetaTitleDisplay(I18NsType value) {
        this.metaTitleDisplay = value;
    }

    /**
     * Gets the value of the metaKeywords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaKeywords() {
        return metaKeywords;
    }

    /**
     * Sets the value of the metaKeywords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaKeywords(String value) {
        this.metaKeywords = value;
    }

    /**
     * Gets the value of the metaKeywordsDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getMetaKeywordsDisplay() {
        return metaKeywordsDisplay;
    }

    /**
     * Sets the value of the metaKeywordsDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setMetaKeywordsDisplay(I18NsType value) {
        this.metaKeywordsDisplay = value;
    }

    /**
     * Gets the value of the metaDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /**
     * Sets the value of the metaDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaDescription(String value) {
        this.metaDescription = value;
    }

    /**
     * Gets the value of the metaDescriptionDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getMetaDescriptionDisplay() {
        return metaDescriptionDisplay;
    }

    /**
     * Sets the value of the metaDescriptionDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setMetaDescriptionDisplay(I18NsType value) {
        this.metaDescriptionDisplay = value;
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
